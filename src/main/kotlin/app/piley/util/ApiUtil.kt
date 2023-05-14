package app.piley.util

import io.ktor.http.*
import io.ktor.server.application.*
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