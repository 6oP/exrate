package com.exrate.ratessource.client

import com.exrate.web.GetRatesQuery
import com.exrate.web.Rates
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineName
import org.slf4j.LoggerFactory
import kotlin.coroutines.coroutineContext

abstract class ExchangeRatesWebRepository(val contextRootUrl: String) {
    private val logger = LoggerFactory.getLogger(javaClass)

    abstract fun buildUrlParameters(query: GetRatesQuery): String

    suspend fun getRates(query: GetRatesQuery): Rates {
        val urlString = buildUrlParameters(query)
        logger.trace("Execute request '{}' / '{}'", coroutineContext[CoroutineName]?.name, urlString)
        val body = SharedHttpClient.client.get(urlString).body<Rates>()
        logger.trace("Request '{}' execution  completed", coroutineContext[CoroutineName]?.name)
        return body
    }
}

/**
 * Aeh public service which provides access to rates data
 * https://api.exchangerate.host/latest
 */
class AehExchangeRatesRepository(contextRootUrl: String) : ExchangeRatesWebRepository(contextRootUrl) {
    override fun buildUrlParameters(query: GetRatesQuery): String {
        return "$contextRootUrl/latest?base=${query.base}&symbols=${query.to.joinToString(separator = ",")} "
    }
}

/**
 * Frankfurter.app
 * https://www.frankfurter.app/docs/
 */
class FrankfurterExchangeRatesRepository(contextRootUrl: String) : ExchangeRatesWebRepository(contextRootUrl) {
    override fun buildUrlParameters(query: GetRatesQuery): String {
        return "$contextRootUrl/latest?from=${query.base}&to=${query.to.joinToString(separator = ",")} "
    }
}
