package app.domain.maple.service

import app.domain.maple.model.MapleUser
import app.utils.APIResult
import app.utils.APIResult.Error
import app.utils.APIResult.Success
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import java.net.URLEncoder
import java.time.format.DateTimeFormatter

class MapleService(config: ApplicationConfig, private val client: HttpClient) {
    private val apiKey: String = config.property("maple.apiKey").getString()

    suspend fun getUser(nickname: String): APIResult<MapleUser, String> {
        if (nickname.isBlank())
            return Error("닉네임을 입력해주세요.")
        return try {
            val encodedNickname = URLEncoder.encode(nickname, "UTF-8")
            val response = client.get("https://open.api.nexon.com/maplestory/v1/id?character_name=$encodedNickname") {

                header("accept", "application/json")
                header("x-nxopen-api-key", apiKey)
            }
            if (response.status == HttpStatusCode.OK) {
                val result: Map<String, String> = response.body()
                val user = getUserInfo(result.get("ocid")!!)
                if (user != null) {
                    Success(user)
                } else {
                    Error("유저 정보를 찾을 수 없습니다.")
                }
            } else {
                Error("유저 정보를 찾을 수 없습니다.")
            }
        } catch (e: Exception) {
            Error("API 요청 중 오류 발생: ${e.message}")
        }
    }

    /**
     * 유저 정보를 가져오는 함수
     * @param ocid
     * @return MapleUser
     * @throws Exception
     * @see MapleUser
     *
     * @since 1.0
     */
    private suspend fun getUserInfo(ocid: String): MapleUser? {
        return try {
            val yesterday = java.time.LocalDate.now().minusDays(1).toString().format(DateTimeFormatter.ISO_DATE)
            val response = client.get("https://open.api.nexon.com/maplestory/v1/character/basic") {
                url {
                    parameters.append("ocid", ocid) // ✅ JSON이 아니라 문자열로 ocid 전달
                    parameters.append("date", yesterday) // ✅ 당일 날짜 추가
                }
                header("accept", "application/json")
                header("x-nxopen-api-key", apiKey)
            }
            val responseText = response.body<String>()
            println("✅ API 응답: $responseText")
            if (response.status == HttpStatusCode.OK) {
                val user = kotlinx.serialization.json.Json.decodeFromString<MapleUser>(responseText)
                println("✅ 유저 정보: $user")
                return user
            } else {
                println("⚠️ API 오류 응답: ${response.status}")
                return null
            }
        } catch (e: Exception) {
            println("❌ API 요청 중 오류 발생: ${e.message}")
            return null
        }
    }
}
