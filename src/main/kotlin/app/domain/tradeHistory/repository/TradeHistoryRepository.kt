package app.domain.tradeHistory.repository

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.model.TradeHistory
import app.domain.tradeHistory.model.TradeHistoryTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TradeHistoryRepository(private val db: Database) {

    suspend fun findAll(): List<TradeHistory> = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.selectAll().map { TradeHistory.fromRow(it) }
    }

    suspend fun save(item: TradeHistoryRequest) = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.insert {
            it[tradeDate] = item.tradeDate
            it[itemName] = item.itemName
            it[price] = item.price
            it[korPrice] = item.korPrice
            it[status] = item.status
            it[userId] = item.userId
        }
    }

    suspend fun update(item: TradeHistoryRequest) = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.update({ TradeHistoryTable.id eq item.id!! }) {
            it[tradeDate] = item.tradeDate
            it[itemName] = item.itemName
            it[price] = item.price
            it[korPrice] = item.korPrice
            it[status] = item.status
            it[userId] = item.userId
        }
    }

    suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.deleteWhere { TradeHistoryTable.id eq id }
    }
}
