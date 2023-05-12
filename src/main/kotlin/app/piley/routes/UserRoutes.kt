package app.piley.routes

import app.piley.dao.userDao
import app.piley.model.User
import app.piley.model.UserUpdate
import io.ktor.http.*
import io.ktor.server.application.*
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
                val newUser = userDao.createUser(user)
                if (newUser == null) {
                    call.respondText("Error creating user", status = HttpStatusCode.InternalServerError)
                } else {
                    call.respondText("User created", status = HttpStatusCode.Created)
                }
            }
        }
        put {
            val user = call.receive<UserUpdate>()
            val existingUser = userDao.getUserUsingPassword(user.oldEmail, user.oldPassword)
            if (existingUser != null) {
                val success = userDao.updateUser(
                    User(
                        email = user.newEmail,
                        name = user.name,
                        password = user.newPassword
                    )
                )
                if (success) {
                    call.respondText("User updated", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Error updating user", status = HttpStatusCode.InternalServerError)
                }
            } else {
                call.respondText("User doesn't exist or no access", status = HttpStatusCode.Forbidden)
            }
        }
        delete("/{email}") {
            val id = call.parameters.getOrFail<String>("email")
            val deleted = userDao.deleteUser(id)
            if (deleted) {
                call.respondText("User deleted", status = HttpStatusCode.OK)
            } else {
                call.respondText("User not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}