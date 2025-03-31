package app.domain.user.model

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class User (
    val id: Int? = null,
    val username: String,
    val password: String
){

    companion object {
        fun create(username: String, rawPassword: String): User {
            return User(null, username, BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
        }
    }


    // 비밀번호 검증
    fun checkPassword(password: String): Boolean {
        // 저장된 비밀번호와 입력된 비밀번호를 비교
        return BCrypt.checkpw(password, this.password)
    }
}