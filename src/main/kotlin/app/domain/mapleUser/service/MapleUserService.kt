package app.domain.maple.service

import app.domain.mapleUser.model.MapleUser
import app.utils.APIResult
import app.utils.APIResult.Error
import app.utils.APIResult.Success
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*

class MapleService(config: ApplicationConfig, private val client: HttpClient) {
    private val apiKey: String = config.property("maple.apiKey").getString()

    suspend fun getUser(nickname: String): APIResult<MapleUser, String> {
        if(nickname.isBlank())
            return Error("닉네임을 입력해주세요.")
        return try {
            val response = client.get("https://open.api.nexon.com/maplestory/v1/id") {
                url {
                    parameters.append("character_name", nickname)
                }
                header("X-NX-OPEN-API-KEY", apiKey)
            }

            if (response.status == HttpStatusCode.OK) {
                val user = response.body<MapleUser>()
                Success(user)
            } else {
                Error("유저 정보를 찾을 수 없습니다.")
            }
        } catch (e: Exception) {
            Error("API 요청 중 오류 발생: ${e.message}")
        }
    }
}
