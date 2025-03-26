package app.domain.tradeHistory.service

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.dto.TradeHistoryResponse
import app.domain.tradeHistory.repository.TradeHistoryRepository
import app.utils.APIResult

class TradeHistoryService(
    private val tradeHistoryRepository: TradeHistoryRepository
) {
    suspend fun findAll(): APIResult.Success<List<TradeHistoryResponse>> {
        return APIResult.Success(tradeHistoryRepository.findAll().map { it.toResponse() })
    }

    suspend fun saveAndUpdate(items: List<TradeHistoryRequest>): APIResult<String, String> {
        items.filter { it.isChecked }.forEach { item ->
            if (item.id != null) {
                tradeHistoryRepository.update(item)
            } else {
                tradeHistoryRepository.save(item)
            }
        }
        return APIResult.Success("저장되었습니다.")
    }

    suspend fun deleteChecked(items: List<TradeHistoryRequest>): APIResult<String, String> {
        items.filter { it.isChecked && it.isDeleted && it.id != null }
            .forEach { tradeHistoryRepository.delete(it.id!!) }
        return APIResult.Success("삭제되었습니다.")
    }
}