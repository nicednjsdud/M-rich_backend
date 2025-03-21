package app.api

import app.domain.user.dto.UserCreateRequest
import app.domain.user.service.UserService
import app.utils.onError
import app.utils.onSuccess
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {
    post("/register") {
        val request = call.receive<UserCreateRequest>()
        userService.register(request)
            .onSuccess { call.respond(HttpStatusCode.Created, mapOf("id" to it)) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }

    post("/login") {
        val request = call.receive<UserCreateRequest>()
        userService.login(request)
            .onSuccess { call.respond(HttpStatusCode.OK, mapOf("token" to it)) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }
}
