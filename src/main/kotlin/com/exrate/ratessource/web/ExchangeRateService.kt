package com.exrate.ratessource.web

import com.exrate.awaitFirstSuccess
import com.exrate.ratessource.client.AehExchangeRatesRepository
import com.exrate.ratessource.client.DecimalToDoubleSerializer
import com.exrate.ratessource.client.FrankfurterExchangeRatesRepository
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

object ExchangeRateService {

    private val ratesRepositories = listOf(
        AehExchangeRatesRepository("https://api.exchangerate.host"),
        FrankfurterExchangeRatesRepository("https://api.frankfurter.app")
    )

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    val cache: LoadingCache<GetRatesQuery, Deferred<Result<Rates>>> = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(15, TimeUnit.SECONDS)
        .build(
            CacheLoader.from { key: GetRatesQuery ->
                // TODO change to another more appropriate scope
                scope.async {
                    queryExternal(key)
                }
            }
        )

    suspend fun getRates(query: GetRatesQuery): Result<Rates> {
        return cache.getUnchecked(query).await()
    }


    suspend fun queryExternal(query: GetRatesQuery): Result<Rates> {
        return coroutineScope {
            ratesRepositories.map { repository ->
                async {
                    runCatching {
                        repository.getRates(query)
                    }
                }
            }
        }.awaitFirstSuccess()
    }


    suspend fun convert(query: GetRatesQuery, value: Double): Result<Any> {
        val rates: Result<Rates> = cache.getUnchecked(query).await() as Result<Rates>

        val converted = rates.getOrNull()!!.rates

        if (rates.isFailure) {
            return rates
        }

        return Result.success(Rates(converted.mapValues { it.value.multiply(value.toBigDecimal()) }))
    }
}


@Serializable
data class Rates(
    val rates: Map<String, @Serializable(with = DecimalToDoubleSerializer::class) BigDecimal>
)

data class GetRatesQuery(val base: String, val to: List<String>)
