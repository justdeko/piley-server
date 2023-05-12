package app.piley.plugins

import app.piley.dao.userDao
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*

fun Application.configureAuth() {
    install(Authentication) {
        basic("auth-basic-hashed") {
            realm = "Access to the protected user paths"
            validate { it.attemptAuth() }
        }
    }
}

val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }

suspend fun UserPasswordCredential.attemptAuth(): UserIdPrincipal? {
    val userTable = mutableMapOf<String, ByteArray>()
    userDao.getUserUsingPassword(this.name, this.password)?.let {
        userTable.put(this.name, digestFunction(this.password))
    }
    println(userTable)
    return UserHashedTableAuth(digestFunction, userTable).authenticate(this)
}
