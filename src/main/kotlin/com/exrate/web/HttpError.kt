package com.exrate.web

object HttpError {
    fun badRequest(data: String): Nothing = throw ParameterMissingError(data)
}