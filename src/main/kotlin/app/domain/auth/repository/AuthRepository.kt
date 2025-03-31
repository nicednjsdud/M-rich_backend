package app.domain.auth.repository

import app.infrastructure.redis.RedisConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 🔐 인증 관련 Repository
 * 1. Redis를 사용하여 유저별 Refresh Token을 저장하고 관리합니다.
 */
class AuthRepository {
    /**
     * Refresh Token 저장
     * @param userId 유저 ID
     * @param token 저장할 토큰 값
     * @param expiration 만료 시간 (초 단위)
     */
    suspend fun save(userId: Int, token: String, expiration: Long = 60 * 60 * 24 * 7) {
        withContext(Dispatchers.IO) {
            RedisConfig.commands.setex("refresh:$userId", expiration, token)
        }
    }

    /**
     * Refresh Token 조회
     * @param userId 유저 ID
     * @return 저장된 토큰 값 (없을 시 null)
     */
    suspend fun findByUserId(userId: Int): String? = withContext(Dispatchers.IO) {
        RedisConfig.commands.get("refresh:$userId")
    }

    /**
     * Refresh Token 삭제
     * @param userId 유저 ID
     */
    suspend fun delete(userId: Int) {
        withContext(Dispatchers.IO) {
            RedisConfig.commands.del("refresh:$userId")
        }
    }

    /**
     * Refresh Token을 통해 유저 ID 조회
     * @param refreshToken 리프레시 토큰
     * @return 유저 ID (없을 시 null)
     */
    fun findUserIdByToken(refreshToken: String): Int? {
        RedisConfig.commands.keys("refresh:*").forEach { key ->
            val userId = key.split(":").last().toInt()
            val token = RedisConfig.commands.get(key)

            if (token == refreshToken) {
                return userId
            }
        }
        return null
    }
}