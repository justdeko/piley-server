package app.piley.routes

import app.piley.dao.userDao
import app.piley.model.User
import app.piley.model.UserUpdate
import app.piley.util.handleResult
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
            put {
                val user = call.receive<UserUpdate>()
                val existingUser = userDao.getUserUsingPassword(user.oldEmail, user.oldPassword)
                if (existingUser != null) {
                    call.handleResult(
                        successCondition = userDao.updateUser(
                            User(
                                email = user.newEmail,
                                name = user.name,
                                password = user.newPassword
                            )
                        ),
                        successMessage = "User updated",
                        errorMessage = "Error updating user"
                    )
                } else {
                    call.respondText("User doesn't exist or no access", status = HttpStatusCode.Forbidden)
                }
            }
            delete("/{email}") {
                val email = call.parameters.getOrFail<String>("email")
                call.handleResult(
                    successCondition = userDao.deleteUser(email),
                    successMessage = "User deleted",
                    errorStatusCode = HttpStatusCode.NotFound,
                    errorMessage = "User not found"
                )
            }
        }
    }
}