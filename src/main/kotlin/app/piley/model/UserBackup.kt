package app.piley.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserBackup(
    val userEmail: String = "",
    val backup: ByteArray,
    val lastModifiedAt: Instant = Clock.System.now(),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserBackup

        if (userEmail != other.userEmail) return false
        if (!backup.contentEquals(other.backup)) return false
        return lastModifiedAt == other.lastModifiedAt
    }

    override fun hashCode(): Int {
        var result = userEmail.hashCode()
        result = 31 * result + backup.contentHashCode()
        result = 31 * result + lastModifiedAt.hashCode()
        return result
    }
}

object UserBackups : Table() {
    val email = text("email")
    val backup = blob("backup")
    val lastModifiedAt = long("lastModifiedAt")
    override val primaryKey = PrimaryKey(email)
}