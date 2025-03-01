package app.domain.user.dto

import app.domain.user.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequest(
    val username: String,
    val password: String
) {
    fun toUser(): User {
        return User(
            username = this.username,
            password = this.password
        )
    }
}