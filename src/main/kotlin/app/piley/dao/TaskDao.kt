package app.piley.dao

import app.piley.model.Task

interface TaskDao {
    suspend fun getTaskList(): List<Task>
    suspend fun getTask(id: Long): Task?
    suspend fun createTask(task: Task): Task?
    suspend fun updateTask(task: Task): Boolean
    suspend fun deleteTaskById(id: Long): Boolean
}