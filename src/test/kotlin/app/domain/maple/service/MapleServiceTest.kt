package app.domain.maple.service

import app.utils.APIResult
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNotNull
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapleServiceTest{

    private val config = ApplicationConfig("application.yaml")
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            json()
        }
    }
    private val mapleService = MapleService(config, client)

    @Test
    fun `getMapleUserOcid - 실제 닉네임을 요청하면 정상 응답이 와야 한다` () = runBlocking {
        val testNickName = "뵤등"
        val result = mapleService.getMapleUser(testNickName)

        assertNotNull(result, "result is null")
        assert(result is APIResult.Success)
    }

    @Test
    fun `getMapleUserOcid - 빈 닉네임을 요청하면 에러 응답이 와야 한다` () = runBlocking {
        val testNickName = ""
        val result = mapleService.getMapleUser(testNickName)

        assertNotNull(result, "result is null")
        assert(result is APIResult.Error)
        assert((result as APIResult.Error).error == "닉네임을 입력해주세요.")
    }

    @Test
    fun `getMapleUserOcid - 잘못된 닉네임을 요청하면 에러 응답이 와야 한다` () = runBlocking {
        val testNickName = "뵤등뵤등"
        val result = mapleService.getMapleUser(testNickName)

        assertNotNull(result, "result is null")
        assert(result is APIResult.Error)
        assert((result as APIResult.Error).error == "유저 정보를 찾을 수 없습니다.")
    }
}