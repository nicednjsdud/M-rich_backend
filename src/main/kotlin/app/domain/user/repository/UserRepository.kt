package app.domain.user.repository

import app.domain.user.model.User
import app.domain.user.model.UserTable
import app.domain.user.model.UserTable.username
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserRepository(private val db: Database){

    suspend fun findByUsername(username: String): User? {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            UserTable.select (UserTable.username eq username)
                .map { row ->
                    User(
                        id = row[UserTable.id].value,
                        username = row[UserTable.username],
                        password = row[UserTable.password]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun create(user: User): Int {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            UserTable.insertAndGetId {
                it[username] = user.username
                it[password] = user.password
            }.value
        }
    }

    suspend fun findById(id: Int): User? {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            UserTable.select { UserTable.id eq id }
                .map { row ->
                    User(
                        id = row[UserTable.id].value,
                        username = row[UserTable.username],
                        password = row[UserTable.password]
                    )
                }
                .singleOrNull()
        }

    }

    suspend fun update(updatedUser: User) {
        newSuspendedTransaction(Dispatchers.IO, db) {
            UserTable.update({ UserTable.id eq updatedUser.id }) {
                it[username] = updatedUser.username
                it[password] = updatedUser.password
            }
        }

    }

    suspend fun delete(id: Int): Boolean {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            UserTable.deleteWhere { UserTable.id eq id } > 0
        }
    }
}