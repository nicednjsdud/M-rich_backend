package app.config

import app.infrastructure.database.configureDatabases
import app.infrastructure.redis.RedisConfig
import app.infrastructure.serialzation.configureSerialization
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // ✅ JWT 설정
    val config = environment.config
    JwtConfig.init(config)

    // ✅ Redis 설정
    RedisConfig.init(config)

    // ✅ 서버 ContentNegotiation 설정
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // ✅ 클라이언트 설정
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureMonitoring()
    configureRouting(client)
    configureSecurity()
}
