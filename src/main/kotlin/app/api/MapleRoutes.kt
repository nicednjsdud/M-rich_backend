package app.api

import app.domain.maple.dto.MapleSearchRequest
import app.domain.maple.dto.MapleUserCreateRequest
import app.domain.maple.service.MapleService
import app.utils.onError
import app.utils.onSuccess
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.mapleRoutes(mapleService: MapleService) {
    get("/user") {
        val nickname = call.receive<MapleSearchRequest>().nickName
        mapleService.getMapleUser(nickname)
            .onSuccess { call.respond(HttpStatusCode.OK, it) }
            .onError { call.respond(HttpStatusCode.NotFound, mapOf("error" to it)) }
    }
    post("/user") {
        // todo token을 통해 받아야함
        val userId : Int = 1;
        val request = call.receive<MapleUserCreateRequest>()
        mapleService.createAndUpdate(request,userId)
            .onSuccess { call.respond(HttpStatusCode.OK, it) }
            .onError { call.respond(HttpStatusCode.NotFound, mapOf("error" to it)) }
    }
}