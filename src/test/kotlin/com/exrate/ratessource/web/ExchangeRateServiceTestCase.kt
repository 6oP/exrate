package com.exrate.ratessource.web

import io.mockk.coEvery
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals


internal class ExchangeRateServiceTestCase {

//
//    @AfterEach
//    internal fun tearDown() {
//        ExchangeRateService.cache.invalidateAll()
//    }

    @Test
    internal fun goodCase() {
        mockkObject(ExchangeRateService)

        val ratesQuery = GetRatesQuery("NZD", listOf("EUR"))

        coEvery { ExchangeRateService.queryExternal(ratesQuery) } returns Result.success(Rates(mapOf("USD" to 0.67.toBigDecimal())))
//        coEvery { ExchangeRateService.cache.get(ratesQuery)} returns CompletableDeferred(Result.success(Rates(mapOf("USD" to 0.67.toBigDecimal()))))

        val result = runBlocking {
            ExchangeRateService.convert(ratesQuery, 10000.0)
        }
        assertEquals(true, result.isSuccess)
        assertEquals(BigDecimal("6700.000"), (result as Result<Rates>).getOrNull()!!.rates["USD"])
    }


    @Test
    internal fun badCase() {
        mockkObject(ExchangeRateService)
        val ratesQuery = GetRatesQuery("NZD", listOf("EUR"))

        coEvery { ExchangeRateService.queryExternal(ratesQuery) } returns Result.failure(Exception("THE CACHE BLEW UP!"))

        val result = runBlocking {
            ExchangeRateService.convert(ratesQuery, 10000.0)
        }
        assertEquals(true, result.isFailure)
        assertEquals("THE CACHE BLEW UP!", result.exceptionOrNull()!!.message)
    }
}