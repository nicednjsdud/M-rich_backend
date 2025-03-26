package app.domain.tradeHistory.model

import app.domain.tradeHistory.dto.TradeHistoryResponse
import app.utils.TradeStatus
import org.jetbrains.exposed.sql.ResultRow

data class TradeHistory(
    val id: Int,
    val tradeDate: String,
    val itemName: String,
    val price: Int,
    val korPrice: String,
    val status: TradeStatus,
    val userId: Int
) {
    companion object {
        fun fromRow(row: ResultRow): TradeHistory {
            return TradeHistory(
                id = row[TradeHistoryTable.id].value,
                tradeDate = row[TradeHistoryTable.tradeDate],
                itemName = row[TradeHistoryTable.itemName],
                price = row[TradeHistoryTable.price],
                korPrice = row[TradeHistoryTable.korPrice],
                status = row[TradeHistoryTable.status],
                userId = row[TradeHistoryTable.userId].value
            )
        }
    }

    fun toResponse(): TradeHistoryResponse {
        return TradeHistoryResponse(
            id = id,
            tradeDate = tradeDate,
            itemName = itemName,
            price = price,
            korPrice = korPrice,
            status = status,
            userId = userId,
            isChecked = false,
            isDeleted = false
        )
    }
}