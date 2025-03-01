package app.api

import app.domain.user.dto.UserRegisterRequest
import app.domain.user.service.UserService
import app.utils.onError
import app.utils.onSuccess
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes(userService: UserService) {
    routing {
        post("/api/v1/register") {
            val request = call.receive<UserRegisterRequest>()
            val result = userService.register(request)

            result.onSuccess { userId ->
                call.respond(HttpStatusCode.Created, mapOf("id" to userId, "message" to "회원가입 성공!"))
            }.onError { errorMessage ->
                call.respond(HttpStatusCode.Conflict, mapOf("error" to errorMessage))
            }
        }

        post("/api/v1/login") {
            val request = call.receive<UserRegisterRequest>()
            val result = userService.login(request.username, request.password)

            result.onSuccess { token ->
                call.respond(HttpStatusCode.OK, mapOf("token" to token, "message" to "로그인 성공!"))
            }.onError { errorMessage ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to errorMessage))
            }
        }
    }
}
