package app.config

import com.wyc.app.config.configureHTTP
import com.wyc.app.config.configureMonitoring
import com.wyc.app.config.configureRouting
import app.infrastructure.database.configureDatabases
import com.wyc.app.infrastructure.serialzation.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureMonitoring()
    configureRouting()
}
