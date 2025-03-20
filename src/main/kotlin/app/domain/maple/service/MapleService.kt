package app.domain.maple.service

import app.domain.maple.dto.MapleUserCreateRequest
import app.domain.maple.model.mapleUser.MapleUser
import app.domain.maple.repository.MapleRepository
import app.domain.user.repository.UserRepository
import app.utils.APIResult
import app.utils.APIResult.Error
import app.utils.APIResult.Success
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import kotlinx.serialization.json.Json.Default.decodeFromJsonElement
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MapleService(config: ApplicationConfig,
                   private val client: HttpClient,
                   private val mapleRepository: MapleRepository,
                   private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(MapleService::class.java)
//    private val apiKey: String = config.property("maple.apiKey").getString()
    private val apiKey: String = "test_2a550831e8b2c1a4360fce104d32a4958e07a625226f41463ce7c72d466dce8fefe8d04e6d233bd35cf2fabdeb93fb0d"
    suspend fun getMapleUser(nickname: String): APIResult<MapleUser, String> {
        if (nickname.isBlank()) return Error("닉네임을 입력해주세요.")

        return try {
            val encodedNickname = URLEncoder.encode(nickname, "UTF-8")
            val response = client.get("https://open.api.nexon.com/maplestory/v1/id?character_name=$encodedNickname") {
                header("accept", "application/json")
                header("x-nxopen-api-key", apiKey)
            }

            if (response.status == HttpStatusCode.OK) {
                val result: Map<String, String> = response.body()
                val ocid = result["ocid"]

                if (ocid != null) {
                    logger.info("✅ 유저 OCID 조회 성공: $ocid")
                    val user = getUserInfo(ocid)
                    user?.let {
                        logger.info("✅ 유저 정보 조회 성공: ${it.name}, ${it.worldName}, ${it.level}")
                        return Success(it)
                    }
                }

                logger.warn("⚠️ OCID 조회는 성공했으나 유저 정보를 찾을 수 없음")
                Error("유저 정보를 찾을 수 없습니다.")
            } else {
                logger.warn("⚠️ 유저 OCID 조회 실패 (HTTP ${response.status})")
                Error("유저 정보를 찾을 수 없습니다.")
            }
        } catch (e: Exception) {
            logger.error("❌ API 요청 중 오류 발생: ${e.message}")
            Error("API 요청 중 오류 발생: ${e.message}")
        }
    }

    /**
     * 유저 기본 정보를 가져오는 함수
     * @param ocid - 유저의 OCID
     * @return MapleUser 객체 또는 null
     */
    private suspend fun getUserInfo(ocid: String): MapleUser? {
        return try {
            val yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
            val response = client.get("https://open.api.nexon.com/maplestory/v1/character/basic") {
                url {
                    parameters.append("ocid", ocid) // ✅ JSON이 아니라 문자열로 전달
                    parameters.append("date", yesterday) // ✅ 어제 날짜 기준
                }
                header("accept", "application/json")
                header("x-nxopen-api-key", apiKey)
            }

            if (response.status == HttpStatusCode.OK) {
                val responseText = response.body<String>()
                logger.info("✅ 유저 기본 정보 응답 수신")
                val jsonElement = Json.parseToJsonElement(responseText).jsonObject
                val updatedJson = jsonElement.toMutableMap().apply {
                    put("ocid", JsonPrimitive(ocid)) // ocid 추가
                }.toMap()
                val user = Json.decodeFromJsonElement<MapleUser>(JsonObject(updatedJson))
                return user
            } else {
                logger.warn("⚠️ 유저 정보 조회 실패 (HTTP ${response.status})")
                null
            }
        } catch (e: Exception) {
            logger.error("❌ 유저 정보 조회 중 오류 발생: ${e.message}")
            null
        }
    }

    suspend fun createAndUpdate(request: MapleUserCreateRequest, userId: Int): APIResult<Int, String> {
        return withContext(Dispatchers.IO) {
            userRepository.findByUserId(userId) ?: return@withContext Error("잘못된 접근입니다.")
            if (mapleRepository.isExists(userId)) {
                mapleRepository.update(request, userId)
            }else{
                mapleRepository.insert(request, userId)
            }
            return@withContext Success(userId)
        }
    }
}
