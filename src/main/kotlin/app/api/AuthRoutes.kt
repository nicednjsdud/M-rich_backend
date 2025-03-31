package app.api

import app.domain.auth.dto.LoginRequest
import app.domain.auth.dto.RefreshRequest
import app.domain.auth.service.AuthService
import app.utils.onError
import app.utils.onSuccess
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {
    post("/login") {
        val request = call.receive<LoginRequest>()
        authService.login(request)
            .onSuccess { call.respond(HttpStatusCode.OK, mapOf("token" to it)) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }

    post("/logout") {
        val userId = call.principal<UserIdPrincipal>()!!.name.toInt()
        authService.logout(userId)
        call.respond(HttpStatusCode.OK)
    }

    post("/refresh") {
        val request = call.receive<RefreshRequest>()
        authService.refreshToken(request)
            .onSuccess { call.respond(HttpStatusCode.OK, mapOf("token" to it)) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }
}
