package com.exrate.ratessource.web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {

        get("/exchangeRates/{base}") {
            // TODO treat getting prams with corresponding 404
            val base = call.parameters["base"]!!
            val symbols = call.request.queryParameters["symbols"]!!


            val result = ExchangeRateService.getRates(GetRatesQuery(base, symbols.split(",")))
            if (result.isSuccess) {
                call.respond(result.getOrThrow())
            } else {
                val ex = result.exceptionOrNull()!!
                val exClassName = ex.javaClass.name
                call.respondText(
                    "$exClassName: ${ex.message}",
                    status = HttpStatusCode.ServiceUnavailable
                )
            }
        }
    }
}
