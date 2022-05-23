package com.exrate.web.apm

import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.Collector
import java.util.*


fun Application.configureMonitoring() {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }

    routing {
        // keep it for validation purpose
        get("/metrics-micrometer") {
            call.respond(appMicrometerRegistry.scrape())
        }

        get("/metrics") {
            val familySamples = appMicrometerRegistry.prometheusRegistry.metricFamilySamples()
            val metrics = buildMetrics(familySamples)
            call.respond(metrics)
        }
    }
}

private fun buildMetrics(familySamples: Enumeration<Collector.MetricFamilySamples>): Metrics {
    val metrics = Metrics()

    familySamples.toList().filter {
        it.name == "ktor_http_server_requests_seconds"
    }.forEach { familySample ->
        familySample.samples.toList().forEach { sample ->
            when (sample.name) {
                "ktor_http_server_requests_seconds_count" -> {
                    metrics.meetApiCallsCounter(sample.labelValues[2], sample.value.toLong())
                }
                "ktor_http_server_requests_seconds" -> {
                    metrics.meetApiQuantile(
                        sample.labelValues[2],
                        sample.labelValues[5],
                        (sample.value * 1000).toLong()
                    )
                }
            }
        }
    }
    return metrics
}
