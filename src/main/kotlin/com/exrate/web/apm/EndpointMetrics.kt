package com.exrate.web.apm

import kotlinx.serialization.Serializable


@Serializable
class Metrics(var totalQueries: Long = 0) {
    val apis: MutableMap<String, ApiMetrics> = mutableMapOf()
}

@Serializable
class ApiMetrics(val name: String) {
    val metrics: ApiTimeMetrics = ApiTimeMetrics()
}

@Serializable
class ApiTimeMetrics(var totalRequests: Long = 0, var totalResponses: Long = 0) {
    val responseTimeMillis: MutableMap<String, Long> = mutableMapOf()
}


fun Metrics.meetApiCallsCounter(apiName: String, count: Long) {
    totalQueries += count
    apis.getOrPut(apiName) {
        ApiMetrics(apiName)
    }.let {
        it.metrics.totalRequests = count
        it.metrics.totalResponses = count
    }
}

fun Metrics.meetApiQuantile(name: String, quantile: String, millis: Long) {
    apis.getOrPut(name) {
        ApiMetrics(name)
    }.let {
        val quantiles = it.metrics.responseTimeMillis
        val value = quantiles.getOrDefault(quantile, 0L)
        quantiles.put(quantile, value + millis)
    }
}