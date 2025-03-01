package app.domain.user.model

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("users") {
    val username = varchar("username", 50) .uniqueIndex() // 유저 이름 (고유)
    val password = varchar("password", 255) // 비밀번호 (해싱 필요)
}
