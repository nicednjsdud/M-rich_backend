package app.domain.ocr.dto

import app.utils.TradeStatus
import kotlinx.serialization.Serializable

@Serializable
data class OcrResponse(
    var tradeDate : String,
    var itemName : String,
    var price : Int,
    var korPrice: String,
    var status : TradeStatus,

)