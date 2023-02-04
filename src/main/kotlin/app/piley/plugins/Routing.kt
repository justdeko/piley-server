package app.piley.plugins

import app.piley.routes.taskRouting
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        authenticate("auth-basic-hashed") {
            taskRouting()
        }
    }
}
