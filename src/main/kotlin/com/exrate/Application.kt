package com.exrate

import com.exrate.ratessource.web.configureContentNegotiation
import com.exrate.ratessource.web.configureRouting
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import plugins.configureMonitoring

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureMonitoring()
        configureContentNegotiation()
    }.start(wait = true)

}
