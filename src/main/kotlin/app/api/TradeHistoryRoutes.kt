package app.api

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.service.TradeHistoryService
import app.utils.onError
import app.utils.onSuccess
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.tradeHistoryRoutes(tradeHistoryService: TradeHistoryService) {
    // 전체 조회
    get {
        tradeHistoryService.findAll()
            .onSuccess { call.respond(HttpStatusCode.OK, it) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }

    // 저장 & 수정
    post {
        val items = call.receive<List<TradeHistoryRequest>>()
        tradeHistoryService.saveAndUpdate(items)
            .onSuccess { call.respond(HttpStatusCode.Created, mapOf("message" to "저장/수정 완료")) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }

    // 삭제
    delete {
        val items = call.receive<List<TradeHistoryRequest>>()
        tradeHistoryService.deleteChecked(items)
            .onSuccess { call.respond(HttpStatusCode.OK, mapOf("message" to "삭제 완료")) }
            .onError { call.respond(HttpStatusCode.BadRequest, mapOf("error" to it)) }
    }
}