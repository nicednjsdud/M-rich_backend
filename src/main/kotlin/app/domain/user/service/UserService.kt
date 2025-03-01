package app.domain.user.service

import app.domain.user.dto.UserRegisterRequest
import app.domain.user.model.UserTable
import app.utils.APIResult
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService(private val db: Database) {

    init {
        transaction(db) {
            SchemaUtils.create(UserTable)
        }
    }

    // 회원가입
    suspend fun register(userRegisterRequest: UserRegisterRequest): APIResult<Int, String> {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            val existingUser = UserTable.select(UserTable.username eq userRegisterRequest.username).singleOrNull()
            if (existingUser != null) return@newSuspendedTransaction APIResult.Error("이미 존재하는 사용자입니다.")

            val hashedPassword = BCrypt.hashpw(userRegisterRequest.password, BCrypt.gensalt())

            val userId = UserTable.insertAndGetId {
                it[username] = userRegisterRequest.username
                it[password] = hashedPassword
            }.value

            APIResult.Success(userId)
        }
    }

    // 로그인
    suspend fun login(username: String, password: String): APIResult<String, String> {
        return newSuspendedTransaction(Dispatchers.IO, db) {
            val user = UserTable.select(UserTable.username eq username).singleOrNull()
                ?: return@newSuspendedTransaction APIResult.Error("사용자를 찾을 수 없습니다.")

            val checkPassword = BCrypt.checkpw(password, user[UserTable.password])
            if (!checkPassword) return@newSuspendedTransaction APIResult.Error("비밀번호가 일치하지 않습니다.")

            //todo 2025.03.01 token 발급 로직 추가 필요
            APIResult.Success("fake_token_${user[UserTable.id].value}")
        }
    }
}
