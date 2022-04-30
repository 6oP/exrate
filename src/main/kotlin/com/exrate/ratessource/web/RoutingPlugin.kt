package com.exrate.ratessource.web

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Application.configureRouting() {
    routing {

        get("/exchangeRates/{base}") {
            val query = getQueryFromUrl()
            val rates = ExchangeRateService.getRates(query).getOrThrow()
            call.respond(rates)
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getQueryFromUrl(): GetRatesQuery {
    val base = call.parameters["base"] ?: throw ParameterMissingError("parameter 'base' is missing")
    val symbols =
        call.request.queryParameters["symbols"] ?: throw ParameterMissingError("parameter 'symbols' is missing")
    return GetRatesQuery(base.uppercase(), symbols.uppercase().split(","))
}
