package app.domain.tradeHistory.service

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.dto.TradeHistoryResponse
import app.domain.tradeHistory.repository.TradeHistoryRepository
import app.utils.APIResult

class TradeHistoryService(
    private val tradeHistoryRepository: TradeHistoryRepository
) {
    suspend fun findAllByUserId(userId : Int): APIResult.Success<List<TradeHistoryResponse>> {
        return APIResult.Success(tradeHistoryRepository.findAllByUserId(userId).map { it.toResponse() })
    }

    suspend fun save(items: List<TradeHistoryRequest>, userId: Int): APIResult<String, String> {
        items.filter { it.isChecked }.forEach { item ->
            tradeHistoryRepository.save(item,userId)
        }
        return APIResult.Success("저장되었습니다.")
    }

    suspend fun update(items: List<TradeHistoryRequest>): APIResult<String, String> {
        items.filter { it.isChecked && !it.isDeleted && it.id != null }
            .forEach { tradeHistoryRepository.update(it) }
        return APIResult.Success("수정되었습니다.")
    }

    suspend fun delete(items: List<TradeHistoryRequest>): APIResult<String, String> {
        items.filter { it.isChecked && it.isDeleted && it.id != null }
            .forEach { tradeHistoryRepository.delete(it.id!!) }
        return APIResult.Success("삭제되었습니다.")
    }
}