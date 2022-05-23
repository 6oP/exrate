package com.exrate.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

// just as marker
object ErrorHandling


fun Application.errorHandling() {

    val logger = LoggerFactory.getLogger(ErrorHandling::class.java)

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is ParameterMissingError -> {
                    call.respondText(cause.message, status = cause.status)
                }
                else -> {
                    logger.error("Unknown error occurred", cause)
                    call.respondText(
                        "Something went wrong, please try later",
                        status = HttpStatusCode.ServiceUnavailable
                    )
                }
            }
        }
    }
}

class ParameterMissingError(override val message: String) :
    StatusException(message, HttpStatusCode.BadRequest)

open class StatusException(
    message: String?,
    open val status: HttpStatusCode = HttpStatusCode.InternalServerError,
    cause: Throwable? = null
) : Exception(message, cause)
