package app.config

import app.infrastructure.database.configureDatabases
import app.infrastructure.serialzation.configureSerialization
import com.wyc.app.config.configureHTTP
import com.wyc.app.config.configureMonitoring
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // ✅ HttpClient 설정
    val client = HttpClient(CIO) {
        install(ContentNegotiation){
            json()
        }
    }

    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureMonitoring()
    configureRouting(client)
}
