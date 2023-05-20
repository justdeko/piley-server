package app.piley.routes

import app.piley.dao.backupDao
import app.piley.dao.userDao
import app.piley.model.User
import app.piley.model.UserUpdate
import app.piley.util.handleResult
import app.piley.util.resourceAccessDenied
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Route.userRouting() {
    route("/users") {
        post {
            val user = call.receive<User>()
            val exists = userDao.getUser(user.email) != null
            if (exists) {
                call.respondText("User already exists", status = HttpStatusCode.Conflict)
            } else {
                call.handleResult(
                    successCondition = userDao.createUser(user) != null,
                    successMessage = "User created",
                    successStatusCode = HttpStatusCode.Created,
                    errorMessage = "Error creating user"
                )
            }
        }
        authenticate("auth-basic-hashed") {
            get("/{email}") {
                val email = call.parameters.getOrFail<String>("email")
                val userEntity = userDao.getUser(email)
                if (userEntity != null) {
                    call.respond(userEntity.copy ( password = "" ))
                } else {
                    call.respondText("User not found", status = HttpStatusCode.NotFound)
                }
            }
            put {
                val userUpdate = call.receive<UserUpdate>()
                if (call.resourceAccessDenied(userUpdate.oldEmail)) return@put
                val existingUser = userDao.getUserUsingPassword(userUpdate.oldEmail, userUpdate.oldPassword)
                if (existingUser != null) {
                    call.handleResult(
                        successCondition = userDao.updateUser(userUpdate),
                        successMessage = "User updated",
                        errorMessage = "Error updating user"
                    )
                } else {
                    call.respondText("User doesn't exist", status = HttpStatusCode.NotFound)
                }
            }
            delete("/{email}") {
                val email = call.parameters.getOrFail<String>("email")
                if (call.resourceAccessDenied(email)) return@delete
                call.handleResult(
                    successCondition = userDao.deleteUser(email).also {
                        // additionally attempt to delete backup, but this can fail (e.g. backup already deleted)
                        backupDao.deleteBackup(email)
                    },
                    successMessage = "User deleted",
                    errorStatusCode = HttpStatusCode.NotFound,
                    errorMessage = "User not found"
                )
            }
        }
    }
}