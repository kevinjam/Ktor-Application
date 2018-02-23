package com.kevinjanvier.outbox.config

import com.kevinjanvier.outbox.constant.APP_NAME
import com.kevinjanvier.outbox.constant.PORT_NUMBER
import com.kevinjanvier.outbox.constant.SERVERPORT
import com.kevinjanvier.outbox.controllers.main
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOG: Logger = LoggerFactory.getLogger(APP_NAME)

val portArgName = SERVERPORT
val defaultPort = PORT_NUMBER

fun main(args: Array<String>) {
    val portConfigured = args.isNotEmpty() && args[0].startsWith(portArgName)

    val port = if (portConfigured) {
        LOG.debug("Custom port configured: ${args[0]}")
        args[0].split("=").last().trim().toInt()
    } else defaultPort
    embeddedServer(Netty, port, module = Application::main).start(wait = true)
}


