package app.piley.routes

import app.piley.dao.backupDao
import app.piley.model.UserBackup
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.io.File

fun Route.backupRouting() {
    route("/backup") {
        post("/{email}") {
            val email = call.parameters.getOrFail<String>("email")
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val backupBytes = part.streamProvider().readBytes()
                    val backup = backupDao.createBackup(
                        UserBackup(email, backupBytes)
                    )
                    if (backup != null) {
                        call.respondText("Backup created", status = HttpStatusCode.OK)
                    } else {
                        call.respondText("Error creating backup", status = HttpStatusCode.InternalServerError)
                    }
                } else {
                    call.respondText("Upload is not a file", status = HttpStatusCode.BadRequest)
                }
            }
        }
        get("/{email}") {
            val email = call.parameters.getOrFail<String>("email")
            val backupEntity = backupDao.getBackup(email)
            if (backupEntity != null) {
                val file = File(".","backupfilename")
                file.writeBytes(backupEntity.backup)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment
                        .withParameter(ContentDisposition.Parameters.FileName, "backupfilename")
                        .toString()
                )
                call.respondFile(file)
            } else {
                call.respondText("Backup not found", status = HttpStatusCode.NotFound)
            }
        }
    } // TODO: backup update route
}