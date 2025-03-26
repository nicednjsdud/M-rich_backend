package app.domain.maple.repository

import app.domain.maple.dto.MapleResponse
import app.domain.maple.dto.MapleUserCreateRequest
import app.domain.maple.model.mapleUser.MapleUser
import app.domain.maple.model.mapleUser.MapleUserTable
import app.domain.user.model.UserTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MapleRepository(private val db: Database) {

    suspend fun isExists(userId: Int): Boolean = newSuspendedTransaction(Dispatchers.IO, db) {
        MapleUserTable.select { MapleUserTable.userId eq userId }
            .empty().not()
    }

    suspend fun findById(id: Int): MapleResponse? {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            MapleUserTable.select { MapleUserTable.id eq id }
                .map { row ->
                    MapleResponse(
                        id = row[MapleUserTable.id].value,
                        ocid = row[MapleUserTable.ocid],
                        name = row[MapleUserTable.name],
                        worldName = row[MapleUserTable.worldName],
                        classLevel = row[MapleUserTable.classLevel],
                        characterClass = row[MapleUserTable.characterClass],
                        level = row[MapleUserTable.level],
                        exp = row[MapleUserTable.exp],
                        expRate = row[MapleUserTable.expRate],
                        guildName = row[MapleUserTable.guildName] ?: "",
                        imageUrl = row[MapleUserTable.imageUrl],
                        gender = row[MapleUserTable.gender],
                        createdDate = row[MapleUserTable.createdDate],
                        isAccessible = row[MapleUserTable.isAccessible],
                        liberationQuestClear = row[MapleUserTable.liberationQuestClear],
                        searchDate = row[MapleUserTable.searchDate],
                        userId = row[MapleUserTable.userId].value
                    )
                }.singleOrNull()
        }
    }

    suspend fun insert(mapleUser: MapleUserCreateRequest, userId: Int) =
        newSuspendedTransaction(Dispatchers.IO, db) {
            MapleUserTable.insertAndGetId {
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
                it[MapleUserTable.userId] = EntityID(userId, UserTable)
            }.value
        }


    suspend fun update(mapleUser: MapleUserCreateRequest, userId: Int) =
        newSuspendedTransaction(Dispatchers.IO, db) {
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