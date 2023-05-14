package app.piley.dao

import app.piley.dao.DatabaseFactory.dbQuery
import app.piley.model.UserBackup
import app.piley.model.UserBackups
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

class BackupDaoImpl : BackupDao {
    override suspend fun createBackup(userBackup: UserBackup): UserBackup? = dbQuery {
        UserBackups.insert {
            it[email] = userBackup.userEmail
            it[backup] = ExposedBlob(userBackup.backup)
            it[lastModifiedAt] = userBackup.lastModifiedAt.epochSeconds
        }.resultedValues?.singleOrNull()?.let(::resultRowToBackup)
    }

    override suspend fun getBackup(email: String): UserBackup? = dbQuery {
        UserBackups.select { UserBackups.email eq email }
            .map(::resultRowToBackup)
            .singleOrNull()
    }

    override suspend fun updateBackup(userBackup: UserBackup): Boolean = dbQuery {
        UserBackups.update({ UserBackups.email eq userBackup.userEmail }) {
            it[email] = userBackup.userEmail
            it[backup] = ExposedBlob(userBackup.backup)
            it[lastModifiedAt] = userBackup.lastModifiedAt.epochSeconds
        } > 0
    }

    override suspend fun deleteBackup(email: String): Boolean = dbQuery {
        UserBackups.deleteWhere { UserBackups.email eq email } > 0
    }

    private fun resultRowToBackup(row: ResultRow) = UserBackup(
        userEmail = row[UserBackups.email],
        backup = row[UserBackups.backup].bytes,
        lastModifiedAt = Instant.fromEpochSeconds(row[UserBackups.lastModifiedAt])
    )
}

val backupDao: BackupDao = BackupDaoImpl().apply {

}