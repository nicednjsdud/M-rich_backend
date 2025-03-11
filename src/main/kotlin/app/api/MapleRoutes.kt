package app.api

import app.domain.maple.service.MapleService
import app.domain.mapleUser.dto.MapleSearchRequest
import app.utils.onError
import app.utils.onSuccess
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.mapleRoutes(mapleService: MapleService) {
    routing {
        get("/maple/user") {
            val nickname = call.receive<MapleSearchRequest>().nickName
            mapleService.getUser(nickname)
                .onSuccess { call.respond(HttpStatusCode.OK, it) }
                .onError { call.respond(HttpStatusCode.NotFound, mapOf("error" to it)) }
        }
    }
}