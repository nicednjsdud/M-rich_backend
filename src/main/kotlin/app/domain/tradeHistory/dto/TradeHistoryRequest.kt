package app.domain.tradeHistory.dto

import app.utils.TradeStatus

data class TradeHistoryRequest (
    val id: Int? = null,
    val tradeDate: String,
    val itemName: String,
    val price: Int,
    val korPrice: String,
    val status: TradeStatus,
    val isChecked: Boolean = false,
    val isDeleted: Boolean = false
)