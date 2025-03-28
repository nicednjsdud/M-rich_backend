package app.config

import app.api.mapleRoutes
import app.api.ocrRoutes
import app.api.tradeHistoryRoutes
import app.api.userRoutes
import app.domain.maple.repository.MapleRepository
import app.domain.maple.service.MapleService
import app.domain.ocr.service.OcrService
import app.domain.tradeHistory.repository.TradeHistoryRepository
import app.domain.tradeHistory.service.TradeHistoryService
import app.domain.user.repository.UserRepository
import app.domain.user.service.UserService
import app.infrastructure.database.DatabaseFactory
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(client: HttpClient) {
    val config = environment.config
    val database = DatabaseFactory.init(config)

    val userRepository = UserRepository(database)
    val userService = UserService(userRepository)

    val mapleRepository = MapleRepository(database)
    val mapleService = MapleService(config, client, mapleRepository, userRepository)

    val ocrService = OcrService()

    val tradeHistoryRepository = TradeHistoryRepository(database)
    val tradeHistoryService = TradeHistoryService(tradeHistoryRepository)

    routing {
        get("/") {
            call.respondText("Hello, mRich!", ContentType.Text.Plain)
        }

        route("/api/v1/users") {
            userRoutes(userService)
        }

        route("/api/v1/maple") {
            mapleRoutes(mapleService)
        }

        route("/api/v1/ocrTest") {
            ocrRoutes(ocrService)
        }

        route("/api/v1/tradeHistory") {
            tradeHistoryRoutes(tradeHistoryService)
        }
    }
}
