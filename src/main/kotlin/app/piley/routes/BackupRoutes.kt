package app.piley.routes

import app.piley.dao.backupDao
import app.piley.model.UserBackup
import app.piley.util.handleResult
import app.piley.util.logError
import app.piley.util.logInfo
import app.piley.util.resourceAccessDenied
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

fun Route.backupRouting() {
    route("/backup") {
        post("/{email}") {
            val email = call.parameters.getOrFail<String>("email")
            if (call.resourceAccessDenied(email)) return@post
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val existingBackup = backupDao.getBackup(email)
                    val backupBytes = part.streamProvider().readBytes()
                    val newBackup = UserBackup(email, backupBytes)
                    if (existingBackup != null) {
                        call.logInfo(
                            "Updating existing backup for user $email"
                                    + " | "
                                    + "Last backup date: ${existingBackup.lastModifiedAt.toLocalDateTime(TimeZone.UTC)} UTC"
                        )
                        call.handleResult(
                            successCondition = backupDao.updateBackup(newBackup),
                            successMessage = "Backup updated",
                            errorMessage = "Error updating backup"
                        )
                    } else {
                        call.logInfo("Creating new backup for user $email")
                        call.handleResult(
                            successCondition = backupDao.createBackup(newBackup) != null,
                            successStatusCode = HttpStatusCode.Created,
                            successMessage = "Backup created",
                            errorMessage = "Error creating backup"
                        )
                    }
                } else {
                    call.respondText("Upload is not a file", status = HttpStatusCode.BadRequest)
                }
            }
        }
        get("/{email}") {
            val email = call.parameters.getOrFail<String>("email")
            if (call.resourceAccessDenied(email)) return@get
            val backupEntity = backupDao.getBackup(email)
            if (backupEntity != null) {
                val file = File(".", "piley_backupfile")
                file.writeBytes(backupEntity.backup)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment
                        .withParameter(ContentDisposition.Parameters.FileName, "backupfilename.db") //TODO rename
                        .toString()
                )
                call.respondFile(file)
                try {
                    file.delete()
                } catch (e: Exception) {
                    call.logError("Error deleting temp file", e)
                }
            } else {
                call.respondText("Backup not found", status = HttpStatusCode.NotFound)
            }
        }
        delete("/{email}") {
            val email = call.parameters.getOrFail<String>("email")
            if (call.resourceAccessDenied(email)) return@delete
            val success = backupDao.deleteBackup(email)
            call.handleResult(
                successCondition = success,
                successStatusCode = HttpStatusCode.OK,
                successMessage = "Backup deleted",
                errorStatusCode = HttpStatusCode.NotFound,
                errorMessage = "Backup not found"
            )
        }
    }
}