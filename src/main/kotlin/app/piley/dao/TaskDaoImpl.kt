package app.piley.dao

import app.piley.dao.DatabaseFactory.dbQuery
import app.piley.model.Task
import app.piley.model.Tasks
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TaskDaoImpl : TaskDao {
    override suspend fun getTaskList(): List<Task> = dbQuery {
        Tasks.selectAll().map(::resultRowToTask)
    }

    override suspend fun getTask(id: Long): Task? = dbQuery {
        Tasks
            .select { Tasks.id eq id }
            .map(::resultRowToTask)
            .singleOrNull()
    }

    override suspend fun createTask(task: Task): Task? = dbQuery {
        val creationTime = Clock.System.now().epochSeconds
        val insertStatement = Tasks.insert {
            it[title] = task.title
            it[description] = task.description
            it[createdAt] = creationTime
            it[modifiedAt] = creationTime
            it[reminder] = task.reminder?.toInstant(TimeZone.UTC)?.epochSeconds
            it[status] = task.status.value
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToTask)
    }

    override suspend fun updateTask(task: Task): Boolean = dbQuery {
        Tasks.update({ Tasks.id eq task.id }) {
            it[title] = task.title
            it[description] = task.description
        } > 0
    }

    override suspend fun deleteTaskById(id: Long): Boolean = dbQuery {
        Tasks.deleteWhere { Tasks.id eq id } > 0
    }

    private fun resultRowToTask(row: ResultRow) = Task(
        id = row[Tasks.id],
        title = row[Tasks.title],
        description = row[Tasks.description],
    )
}

val taskDao: TaskDao = TaskDaoImpl().apply {
    runBlocking {
        if (getTaskList().isEmpty()) {
            createTask(Task(title = "testTitle", description = "TestDescription"))
        }
    }
}
