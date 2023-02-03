package app.piley.plugins

import app.piley.dao.taskDao
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/tasks") {
            call.respond(mapOf("tasks" to taskDao.getTaskList()).toString())
        }
    }
}
