package app.piley.dao

import app.piley.model.User

interface UserDao {
    suspend fun getUsers(): List<User>
    suspend fun getUserUsingPassword(email: String, password: String): User?
    suspend fun getUser(email: String): User?
    suspend fun createUser(user: User): User?
    suspend fun updateUser(user: User): Boolean
    suspend fun deleteUser(email: String): Boolean
}