package app.config

import app.api.mapleRoutes
import app.api.userRoutes
import app.domain.maple.repository.MapleRepository
import app.domain.maple.service.MapleService
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


    routing {
        get("/"){
            call.respondText("Hello, mRich!", ContentType.Text.Plain)
        }
        route("/api/v1/users"){
            val userRepository = UserRepository(database)
            val userService = UserService(userRepository)
            userRoutes(userService)
        }
        route("/api/v1/maple"){
            val userRepository = UserRepository(database)
            val mapleRepository = MapleRepository(database)
             val mapleService = MapleService(config, client, mapleRepository, userRepository)
             mapleRoutes(mapleService)
        }
    }
}
