package com.exrate.web

import com.exrate.awaitFirstSuccess
import com.exrate.ratessource.client.DecimalToDoubleSerializer
import com.exrate.ratessource.client.ExchangeRatesWebRepository
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class ExchangeRateService(private val ratesRepositories: List<ExchangeRatesWebRepository>) {

    private val counter: AtomicInteger = AtomicInteger()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val cache: LoadingCache<GetRatesQuery, Deferred<Result<Rates>>> =
        CacheBuilder.newBuilder().maximumSize(1000).expireAfterWrite(15, TimeUnit.SECONDS)
            .build(CacheLoader.from { key: GetRatesQuery ->
                // TODO change to another more appropriate scope
                scope.async {
                    queryExternal(key)
                }
            })

    suspend fun getRates(query: GetRatesQuery): Result<Rates> {
        return cache.getUnchecked(query).await()
    }

    private suspend fun queryExternal(query: GetRatesQuery): Result<Rates> {
        return coroutineScope {
            ratesRepositories.map { repository ->
                async {
                    withContext(CoroutineName("request-${counter.incrementAndGet()}")) {
                        runCatching {
                            repository.getRates(query)
                        }
                    }
                }
            }
        }.awaitFirstSuccess()
    }
}


@Serializable
data class Rates(
    val rates: Map<String, @Serializable(with = DecimalToDoubleSerializer::class) BigDecimal>
)

data class GetRatesQuery(val base: String, val to: List<String>)
