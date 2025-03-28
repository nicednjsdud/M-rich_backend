package app.domain.tradeHistory.model

import app.domain.user.model.UserTable
import app.utils.TradeStatus
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption


object TradeHistoryTable : IntIdTable("trade_history") {
    val tradeDate = varchar("trade_date", 30) // ✅ 거래일
    val itemName = varchar("item_name", 50) // ✅ 아이템명
    val price = integer("price") // ✅ 가격
    val korPrice = varchar("kor_price", 20) // ✅ 한화 가격
    val status = enumerationByName("status", 20, TradeStatus::class) // ✅ 거래 상태
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE) // ✅ 외래키 추가 (UserTable 연결)
}
