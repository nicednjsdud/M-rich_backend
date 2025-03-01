package app.api

import app.domain.user.dto.UserRegisterRequest
import app.domain.user.service.UserService
import app.utils.APIResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.userRoutes(userService: UserService) {
    routing {
        post("api/v1/register"){
            val request = call.receive<UserRegisterRequest>()
            when (val result = userService.register(request)) {
                is APIResult.Success -> call.respond(HttpStatusCode.Created, mapOf("userId" to result.data, "message" to "회원가입 성공!"))
                is APIResult.Error -> call.respond(HttpStatusCode.Conflict, mapOf("error" to result.error))
            }
        }

        post("api/v1/login"){
            val request = call.receive<UserRegisterRequest>()
            when (val result = userService.login(request.username, request.password)) {
                is APIResult.Success -> call.respond(HttpStatusCode.OK, mapOf("token" to result.data))
                is APIResult.Error -> call.respond(HttpStatusCode.Unauthorized, mapOf("error" to result.error))
            }
        }
    }
}
