package app.domain.user.model

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class User (
    val id: Int? = null,
    val username: String,
    val password: String
){

    // 비밀번호 해싱
    fun hashPassword(): User {
        val hashedPassword = BCrypt.hashpw(this.password, BCrypt.gensalt())
        return this.copy(password = hashedPassword)
    }

    // 비밀번호 검증
    fun checkPassword(password: String): Boolean {
        return BCrypt.checkpw(password, this.password)
    }
}