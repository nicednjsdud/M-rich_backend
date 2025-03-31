package app.domain.auth.service

import app.config.JwtConfig
import app.domain.auth.dto.LoginRequest
import app.domain.auth.dto.RefreshRequest
import app.domain.auth.dto.TokenResponse
import app.domain.auth.repository.AuthRepository
import app.domain.user.dto.LoginResponse
import app.domain.user.repository.UserRepository
import app.utils.APIResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthService (private val authRepository: AuthRepository, private val userRepository: UserRepository){

    suspend fun login(request: LoginRequest): APIResult<LoginResponse, String> {
        return withContext(Dispatchers.IO) {
            val user = userRepository.findByUsername(request.username)
                ?: return@withContext APIResult.Error("사용자를 찾을 수 없습니다.")

            // 도메인 객체에서 비밀번호 검증
            return@withContext if (user.checkPassword(request.password)) {
                APIResult.Success(createToken(user.id!!))
            } else {
                APIResult.Error("비밀번호가 일치하지 않습니다.")
            }
        }
    }

    suspend fun logout(userId: Int) {
        authRepository.delete(userId)
    }

    private suspend fun createToken(userId: Int): LoginResponse {
        val accessToken = JwtConfig.createAccessToken(userId)
        val refreshToken = JwtConfig.createRefreshToken(userId)

        // Refresh Token 저장 (Redis)
        authRepository.save(userId, refreshToken, JwtConfig.getRefreshTokenExpiration())

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    /**
     * Refresh Token 을 검증하고, 새로운 Access Token 을 발급한다.
     */
    suspend fun refreshToken(request: RefreshRequest): APIResult<TokenResponse, String> = withContext(Dispatchers.IO) {
        try {
            val userId = authRepository.findUserIdByToken(request.refreshToken)
                ?: return@withContext APIResult.Error("리프레시 토큰이 유효하지 않습니다.")

            val newAccessToken = JwtConfig.createAccessToken(userId)

            println("🔐 새로운 액세스 토큰 발급: $newAccessToken")

            APIResult.Success(TokenResponse(newAccessToken))
        } catch (e: Exception) {
            APIResult.Error("액세스 토큰 발급에 실패했습니다.")
        }
    }
}