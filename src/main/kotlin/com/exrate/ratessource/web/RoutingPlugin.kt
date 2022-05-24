package com.exrate.ratessource.web

import io.ktor.http.*
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


        get("/convertAmount") {
            val base = call.request.queryParameters["from"]!!
            val symbols = call.request.queryParameters["to"]!!
            val amount = call.request.queryParameters["amount"]


            val result = ExchangeRateService.convert(GetRatesQuery(base, symbols.split(",")), amount!!.toDouble())

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

private fun PipelineContext<Unit, ApplicationCall>.getQueryFromUrl(): GetRatesQuery {
    val base = call.parameters["base"] ?: throw ParameterMissingError("parameter 'base' is missing")
    val symbols =
        call.request.queryParameters["symbols"] ?: throw ParameterMissingError("parameter 'symbols' is missing")
    return GetRatesQuery(base.uppercase(), symbols.uppercase().split(","))
}
