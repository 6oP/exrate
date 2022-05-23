package com.exrate.web

import com.exrate.ratessource.client.AehExchangeRatesRepository
import com.exrate.ratessource.client.FrankfurterExchangeRatesRepository
import com.exrate.web.HttpError.badRequest
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*


fun Application.configureRouting() {

    val ratesService = ExchangeRateService(
        listOf(
            AehExchangeRatesRepository("https://api.exchangerate.host"),
            FrankfurterExchangeRatesRepository("https://api.frankfurter.app")
        )
    )

    routing {

        get("/exchangeRates/{base}") {
            val query = getQueryFromUrl()
            val rates = ratesService.getRates(query).getOrThrow()
            call.respond(rates)
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getQueryFromUrl(): GetRatesQuery {
    val base = call.parameters["base"] ?: badRequest("parameter 'base' is missing")
    val symbols =
        call.request.queryParameters["symbols"] ?: badRequest("parameter 'symbols' is missing")
    return GetRatesQuery(base.uppercase(), symbols.uppercase().split(","))
}
