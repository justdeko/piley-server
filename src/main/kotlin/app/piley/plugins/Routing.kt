package app.piley.plugins

import app.piley.dao.taskDao
import app.piley.model.Task
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/tasks") {
            call.respond(taskDao.getTaskList())
        }
        get("/tasks/{id}") {
            val id = call.parameters.getOrFail<Int>("id").toLong()
            val task = taskDao.getTask(id)
            if (task != null) {
                call.respond(task)
            } else {
                call.respondText("Task not found", status = HttpStatusCode.NotFound)
            }
        }
        post("/tasks") {
            val task = call.receive<Task>()
            val exists = taskDao.getTask(task.id) != null
            if (exists) {
                val success = taskDao.updateTask(task)
                if (success) {
                    call.respondText("Task updated", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Error updating task", status = HttpStatusCode.InternalServerError)
                }
            } else {
                val newTask = taskDao.createTask(task)
                if (newTask != null) {
                    call.respondText("Task created with id ${newTask.id}", status = HttpStatusCode.Created)
                } else {
                    call.respondText("Error creating task", status = HttpStatusCode.InternalServerError)
                }
            }
        }
        delete("/tasks/{id}") {
            val id = call.parameters.getOrFail<Int>("id").toLong()
            val deleted = taskDao.deleteTaskById(id)
            if (deleted) {
                call.respondText("Task deleted", status = HttpStatusCode.OK)
            } else {
                call.respondText("Task not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
