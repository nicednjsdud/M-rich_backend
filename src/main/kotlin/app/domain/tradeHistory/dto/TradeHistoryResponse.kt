package app.domain.tradeHistory.dto

import app.utils.TradeStatus

data class TradeHistoryResponse (
    val id: Int,
    val tradeDate: String,
    val itemName: String,
    val price: Int,
    val korPrice: String,
    val status: TradeStatus,
    val userId: Int,
    val isChecked: Boolean = false,
    val isDeleted: Boolean = false
)