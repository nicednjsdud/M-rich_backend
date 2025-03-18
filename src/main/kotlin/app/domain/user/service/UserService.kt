package app.domain.user.service

import app.domain.user.dto.UserCreateRequest
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
            val user = request.toUser().hashPassword()
            val userId = userRepository.create(user)

            return@withContext APIResult.Success(userId)
        }
    }

    suspend fun login(request: UserCreateRequest): APIResult<String, String> {
        return withContext(Dispatchers.IO) {
            val user = userRepository.findByUsername(request.username)
                ?: return@withContext APIResult.Error("사용자를 찾을 수 없습니다.")

            // 도메인 객체에서 비밀번호 검증
            return@withContext if (user.checkPassword(request.password)) {
                APIResult.Success("fake_token_${user.id}")
            } else {
                APIResult.Error("비밀번호가 일치하지 않습니다.")
            }
        }
    }
}
