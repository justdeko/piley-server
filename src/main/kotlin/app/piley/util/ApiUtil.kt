package app.piley.util

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend fun ApplicationCall.handleResult(
    successCondition: Boolean,
    successStatusCode: HttpStatusCode = HttpStatusCode.OK,
    successMessage: String,
    errorStatusCode: HttpStatusCode = HttpStatusCode.InternalServerError,
    errorMessage: String
) {
    if (successCondition) {
        respondText(successMessage, status = successStatusCode)
    } else {
        respondText(errorMessage, status = errorStatusCode)
    }
}

fun ApplicationCall.logInfo(message: String) = application.environment.log.info(message)
fun ApplicationCall.logDebug(message: String) = application.environment.log.debug(message)
fun ApplicationCall.logError(message: String, exception: Throwable?) =
    application.environment.log.error(message, exception)

suspend fun ApplicationCall.resourceAccessDenied(resourceOwnerId: String): Boolean {
    val authEmail = principal<UserIdPrincipal>()?.name
    // admins are allowed to access anything
    if (authEmail == "admin@admin.com") return false
    val operationVerb = when (request.httpMethod) {
        HttpMethod.Get -> "access"
        HttpMethod.Post -> "create"
        HttpMethod.Put -> "modify"
        HttpMethod.Delete -> "delete"
        else -> "use"
    }
    return if (resourceOwnerId != authEmail) {
        respondText("Not allowed to $operationVerb this resource", status = HttpStatusCode.Forbidden)
        true
    } else false
}