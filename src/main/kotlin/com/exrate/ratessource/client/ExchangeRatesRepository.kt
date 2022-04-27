package com.exrate.ratessource.client

import com.exrate.ratessource.web.GetRatesQuery
import com.exrate.ratessource.web.Rates
import io.ktor.client.call.*
import io.ktor.client.request.*
import org.slf4j.LoggerFactory
import java.util.*

abstract class ExchangeRatesRepository(val contextRootUrl: String) {
    private val logger = LoggerFactory.getLogger(javaClass)

    abstract fun buildUrlParameters(query: GetRatesQuery): String

    suspend fun getRates(query: GetRatesQuery): Rates {
        val requestId = UUID.randomUUID()
        val urlString = buildUrlParameters(query)
        logger.trace("Execute request '{}' / '{}'", requestId, urlString)
        val body = SharedHttpClient.client.get(urlString).body<Rates>()
        logger.trace("Request '{}' execution  completed", requestId)
        return body
    }
}

/**
 * Aeh public service which provides access to rates data
 * https://api.exchangerate.host/latest
 */
class AehExchangeRatesRepository(contextRootUrl: String) : ExchangeRatesRepository(contextRootUrl) {
    override fun buildUrlParameters(query: GetRatesQuery): String {
        return "$contextRootUrl/latest?base=${query.base}&symbols=${query.to.joinToString(separator = ",")} "
    }
}

/**
 * Frankfurter.app
 * https://www.frankfurter.app/docs/
 */
class FrankfurterExchangeRatesRepository(contextRootUrl: String) : ExchangeRatesRepository(contextRootUrl) {
    override fun buildUrlParameters(query: GetRatesQuery): String {
        return "$contextRootUrl/latest?from=${query.base}&to=${query.to.joinToString(separator = ",")} "
    }
}
