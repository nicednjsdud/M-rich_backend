package app.domain.tradeHistory.repository

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.model.TradeHistory
import app.domain.tradeHistory.model.TradeHistoryTable
import app.domain.user.model.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class TradeHistoryRepository(private val db: Database) {

    suspend fun findById(id: Int): TradeHistory? = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.select { TradeHistoryTable.id eq id }.map { TradeHistory.fromRow(it) }.firstOrNull()
    }

    suspend fun findAllByUserId(userId : Int): List<TradeHistory> = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.select(TradeHistoryTable.userId eq userId)
            .map { TradeHistory.fromRow(it) }
    }

    suspend fun save(item: TradeHistoryRequest, userId: Int) = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.insertAndGetId {
            it[tradeDate] = item.tradeDate
            it[itemName] = item.itemName
            it[price] = item.price
            it[korPrice] = item.korPrice
            it[status] = item.status
            it[TradeHistoryTable.userId] = EntityID(userId, UserTable)
        }.value
    }

    suspend fun update(item: TradeHistoryRequest) = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.update({ TradeHistoryTable.id eq item.id!! }) {
            it[tradeDate] = item.tradeDate
            it[itemName] = item.itemName
            it[price] = item.price
            it[korPrice] = item.korPrice
            it[status] = item.status
        }
    }

    suspend fun delete(id: Int) = newSuspendedTransaction(Dispatchers.IO, db) {
        TradeHistoryTable.deleteWhere { TradeHistoryTable.id eq id }
    }
}
