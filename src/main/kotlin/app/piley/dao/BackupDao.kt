package app.piley.dao

import app.piley.model.UserBackup

interface BackupDao {
    suspend fun createBackup(userBackup: UserBackup): UserBackup?
    suspend fun getBackup(email: String): UserBackup?
    suspend fun updateBackup(userBackup: UserBackup): Boolean
    suspend fun deleteBackup(email: String): Boolean
}