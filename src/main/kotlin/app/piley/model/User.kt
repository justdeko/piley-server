package app.piley.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val email: String = "",
    val name: String = "",
    val password: String = "",
)

@Serializable
data class UserUpdate(
    val oldEmail: String = "",
    val newEmail: String = "",
    val name: String = "",
    val oldPassword: String = "",
    val newPassword: String = "",
)

object Users : Table() {
    val email = text("email")
    val name = text("name")
    val password = text("password")

    override val primaryKey = PrimaryKey(email)
}