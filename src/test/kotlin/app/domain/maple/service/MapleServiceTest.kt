package app.domain.maple.service

import app.domain.maple.dto.MapleUserCreateRequest
import app.domain.maple.model.mapleUser.MapleUserTable
import app.domain.maple.repository.MapleRepository
import app.domain.user.dto.UserCreateRequest
import app.domain.user.model.UserTable
import app.domain.user.repository.UserRepository
import app.infrastructure.database.DatabaseFactory
import app.utils.APIResult
import com.typesafe.config.ConfigFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertNotNull
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapleServiceTest{
    private val config = HoconApplicationConfig(ConfigFactory.load("application.yaml"))

    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            json()
        }
    }

    private val database = DatabaseFactory.init(config)

    private lateinit var mapleService: MapleService
    private lateinit var userRepository: UserRepository
    private lateinit var mapleRepository: MapleRepository


    @BeforeAll
    fun setUp() {
        transaction(database) {
            SchemaUtils.create(UserTable, MapleUserTable)
        }
        userRepository = UserRepository(database)
        mapleRepository = MapleRepository(database)
        mapleService = MapleService(config, client, mapleRepository, userRepository)
    }

    @AfterEach
    fun tearDown() {
        transaction(database) {
            MapleUserTable.deleteAll()
            UserTable.deleteAll()
        }
    }

    @AfterAll
    fun tearDownAll() {
        transaction {
            SchemaUtils.drop(MapleUserTable)
            SchemaUtils.drop(UserTable)
        }
    }

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

    @Test
    fun `createMapleUser - 저장된 데이터가 없을경우 저장을 해야한다`() = runBlocking {
        // Given
        val userCreateRequest = UserCreateRequest("testUser", "testPassword")
        val initialRequest = MapleUserCreateRequest(
            ocid = "test_ocid",
            name = "초기유저",
            worldName = "루나",
            classLevel = "5",
            characterClass = "에반",
            level = 230,
            exp = 30828386992,
            expRate = "36.846",
            guildName = "초기길드",
            imageUrl = "https://example.com/character.png",
            gender = "남",
            createdDate = "2016-07-03",
            isAccessible = false,
            liberationQuestClear = false,
            searchDate = "2025-03-17"
        )

        // When
        val user = userCreateRequest.toUser().hashPassword()
        val userId = userRepository.create(user)

        mapleService.createAndUpdate(initialRequest, userId)
        val savedUserResponse = mapleRepository.findById(userId)

        // Then
        assertNotNull(savedUserResponse, "DB에 저장된 데이터가 없습니다.")
        assert(savedUserResponse!!.name == initialRequest.name)
        assert(savedUserResponse.level == initialRequest.level)
        assert(savedUserResponse.userId == userId)
    }

    @Test
    fun `updateMapleUser - 저장된 데이터가 있을경우 업데이트를 해야한다`() = runBlocking {
        // Given
        val userCreateRequest = UserCreateRequest("testUser", "testPassword")
        val initialRequest = MapleUserCreateRequest(
            ocid = "test_ocid",
            name = "초기유저",
            worldName = "루나",
            classLevel = "5",
            characterClass = "에반",
            level = 230,
            exp = 30828386992,
            expRate = "36.846",
            guildName = "초기길드",
            imageUrl = "https://example.com/character.png",
            gender = "남",
            createdDate = "2016-07-03",
            isAccessible = false,
            liberationQuestClear = false,
            searchDate = "2025-03-17"
        )
        val changeRequest = MapleUserCreateRequest(
            ocid = "test_ocid",
            name = "변경유저", // ✅ 변경된 이름
            worldName = "루나",
            classLevel = "5",
            characterClass = "에반",
            level = 230,
            exp = 30828386992,
            expRate = "36.846",
            guildName = "변경길드",
            imageUrl = "https://example.com/character.png",
            gender = "남",
            createdDate = "2016-07-03",
            isAccessible = false,
            liberationQuestClear = false,
            searchDate = "2025-03-17"
        )

        // When

        val user = userCreateRequest.toUser().hashPassword()
        val userId = userRepository.create(user)

        mapleService.createAndUpdate(initialRequest, userId)
        mapleService.createAndUpdate(changeRequest, userId)

        val savedUserResponse = mapleRepository.findById(userId)

        // Then
        assertNotNull(savedUserResponse, "DB에 저장된 데이터가 없습니다.")
        assert(savedUserResponse!!.name == changeRequest.name)
        assert(savedUserResponse.userId == userId)
    }
}