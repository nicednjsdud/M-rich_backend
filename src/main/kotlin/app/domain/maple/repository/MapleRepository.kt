package app.domain.maple.repository

import app.domain.maple.dto.MapleUserCreateRequest
import app.domain.maple.model.mapleUser.MapleUserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class MapleRepository(private val db: Database){

    suspend fun isExists(userId: Int): Boolean = newSuspendedTransaction(Dispatchers.IO, db) {
        MapleUserTable.select { MapleUserTable.userId eq userId }
            .empty().not() // ✅ 데이터가 있으면 true, 없으면 false 반환
    }

   suspend fun insert(mapleUser: MapleUserCreateRequest, userId: Int) = newSuspendedTransaction(Dispatchers.IO, db) {
       MapleUserTable.insert {
           it[ocid] = mapleUser.ocid
           it[name] = mapleUser.name
           it[worldName] = mapleUser.worldName
           it[classLevel] = mapleUser.classLevel
           it[characterClass] = mapleUser.characterClass
           it[level] = mapleUser.level
           it[exp] = mapleUser.exp
           it[expRate] = mapleUser.expRate
           it[guildName] = mapleUser.guildName
           it[imageUrl] = mapleUser.imageUrl
           it[gender] = mapleUser.gender
           it[createdDate] = mapleUser.createdDate
           it[isAccessible] = mapleUser.isAccessible
           it[liberationQuestClear] = mapleUser.liberationQuestClear
           it[searchDate] = mapleUser.searchDate
           it[userId] = userId
       }
    }

    suspend fun update(mapleUser: MapleUserCreateRequest, userId: Int) = newSuspendedTransaction(Dispatchers.IO, db) {
        MapleUserTable.update({ MapleUserTable.userId eq userId }) {
            it[name] = mapleUser.name
            it[worldName] = mapleUser.worldName
            it[classLevel] = mapleUser.classLevel
            it[characterClass] = mapleUser.characterClass
            it[level] = mapleUser.level
            it[exp] = mapleUser.exp
            it[expRate] = mapleUser.expRate
            it[guildName] = mapleUser.guildName
            it[imageUrl] = mapleUser.imageUrl
            it[gender] = mapleUser.gender
            it[createdDate] = mapleUser.createdDate
            it[isAccessible] = mapleUser.isAccessible
            it[liberationQuestClear] = mapleUser.liberationQuestClear
        }
    }
}