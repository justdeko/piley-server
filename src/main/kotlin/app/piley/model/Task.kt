package app.piley.model

import app.piley.util.utcZoneId
import org.jetbrains.exposed.sql.*
import java.time.LocalDateTime

data class Task(
    val id: Long,
    val title: String = "",
    val pileId: Long = 1,
    val description: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    val modifiedAt: LocalDateTime = LocalDateTime.now(utcZoneId),
    val reminder: LocalDateTime? = null,
    val status: TaskStatus = TaskStatus.DEFAULT,
)
object Tasks : Table() {
    val id = long("id").autoIncrement()
    val title = mediumText("title")
    val description = largeText("description")
    val createdAt = long("createdAt")
    val modifiedAt = long("modifiedAt")
    val reminder = long("reminder")
    val status = integer("status")

    override val primaryKey = PrimaryKey(id)
}