package app.piley.model

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Task(
    val id: Long = 0,
    val title: String = "",
    val pileId: Long = 1,
    val description: String = "",
    val createdAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val modifiedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val reminder: LocalDateTime? = null,
    val status: TaskStatus = TaskStatus.DEFAULT,
)

object Tasks : Table() {
    val id = long("id").autoIncrement()
    val title = mediumText("title")
    val description = largeText("description")
    val createdAt = long("createdAt")
    val modifiedAt = long("modifiedAt")
    val reminder = long("reminder").nullable()
    val status = integer("status")

    override val primaryKey = PrimaryKey(id)
}