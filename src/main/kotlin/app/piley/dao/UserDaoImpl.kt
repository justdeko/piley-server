package app.piley.dao

import app.piley.dao.DatabaseFactory.dbQuery
import app.piley.model.User
import app.piley.model.UserUpdate
import app.piley.model.Users
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.mindrot.jbcrypt.BCrypt

class UserDaoImpl : UserDao {
    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun getUserUsingPassword(email: String, password: String): User? = dbQuery {
        Users.select { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull { BCrypt.checkpw(password, it.password) }
    }

    override suspend fun getUser(email: String): User? = dbQuery {
        Users.select { Users.email eq email }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    override suspend fun createUser(user: User): User? = dbQuery {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = BCrypt.hashpw(user.password, BCrypt.gensalt())
        }.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }

    override suspend fun updateUser(update: UserUpdate): Boolean = dbQuery {
        Users.update({ Users.email eq update.oldEmail }) {
            it[name] = update.name
            it[email] = update.newEmail
            it[password] = BCrypt.hashpw(update.newPassword, BCrypt.gensalt())
        } > 0
    }

    override suspend fun deleteUser(email: String): Boolean = dbQuery {
        Users.deleteWhere { Users.email eq email } > 0
    }

    private fun resultRowToUser(row: ResultRow) = User(
        email = row[Users.email],
        name = row[Users.name],
        password = row[Users.password]
    )
}

val userDao: UserDao = UserDaoImpl().apply {
    runBlocking {
        if (getUsers().isEmpty()) {
            createUser(User(name = "Admin", password = "Admin", email = "admin@admin.com"))
        }
    }
}