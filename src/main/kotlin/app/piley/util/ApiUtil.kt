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