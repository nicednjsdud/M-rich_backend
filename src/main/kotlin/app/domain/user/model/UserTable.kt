package app.domain.user.model

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("users") {  // ✅ IntIdTable을 사용하면 id는 AUTO_INCREMENT됨
    val username = varchar("username", 50) // 유저 아이디 (고유)
    val password = varchar("password", 255) // 비밀번호 (해싱 필요)
}
