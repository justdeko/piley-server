package app.piley.dao

import app.piley.model.UserBackups
import app.piley.model.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val baseUrl = System.getenv()["DB_BASE_URL"] ?: "localhost"
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://$baseUrl:5432/piley?user=postgres"
        val database = Database.connect(jdbcURL, driverClassName)
        transaction(database) {
            SchemaUtils.create(Users, UserBackups)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}