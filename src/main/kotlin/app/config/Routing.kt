package app.config

import app.api.userRoutes
import app.domain.user.repository.UserRepository
import app.domain.user.service.UserService
import app.infrastructure.database.DatabaseFactory
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.slf4j.event.*

fun Application.configureRouting() {
    val config = environment.config
    val database = DatabaseFactory.init(config)

    routing {
        get("/"){
            call.respondText("Hello, mRich!", ContentType.Text.Plain)
        }
        route("/api/v1/users"){
            val userRepository = UserRepository(database)
            val userService = UserService(userRepository)
            userRoutes(userService)
        }
    }
}
