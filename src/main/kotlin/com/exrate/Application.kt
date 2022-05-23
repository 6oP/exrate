package com.exrate

import com.exrate.web.apm.configureMonitoring
import com.exrate.web.configureContentNegotiation
import com.exrate.web.configureRouting
import com.exrate.web.errorHandling
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        errorHandling()
        configureRouting()
        configureMonitoring()
        configureContentNegotiation()
    }.start(wait = true)

}
