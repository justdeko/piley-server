package app.piley.plugins

import app.piley.routes.backupRouting
import app.piley.routes.userRouting
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        userRouting()
        authenticate("auth-basic-hashed") {
            backupRouting()
        }
    }
}
