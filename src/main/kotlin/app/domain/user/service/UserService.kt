package app.domain.user.service

import app.domain.user.dto.UserCreateRequest
import app.domain.user.model.User
import app.domain.user.repository.UserRepository
import app.utils.APIResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService(private val userRepository: UserRepository) {

    suspend fun register(request: UserCreateRequest): APIResult<Int, String> {
        return withContext(Dispatchers.IO) {
            val existingUser = userRepository.findByUsername(request.username)

            if (existingUser != null) {
                return@withContext APIResult.Error("이미 존재하는 사용자입니다.")
            }

            // 도메인 객체에서 비밀번호 해싱
            val user = User.create(request.username, request.password)
            val userId = userRepository.create(user)

            return@withContext APIResult.Success(userId)
        }
    }
}
