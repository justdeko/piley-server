package app.piley.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class UserBackup(
    val userEmail: String = "",
    val backup: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserBackup

        if (userEmail != other.userEmail) return false
        return backup.contentEquals(other.backup)
    }

    override fun hashCode(): Int {
        var result = userEmail.hashCode()
        result = 31 * result + backup.contentHashCode()
        return result
    }
}

object UserBackups : Table() {
    val email = text("email")
    val backup = blob("backup")

    override val primaryKey = PrimaryKey(email)
}