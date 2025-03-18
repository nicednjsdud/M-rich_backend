package app.domain.maple.repository

import app.domain.maple.dto.MapleUserCreateRequest
import app.domain.maple.model.mapleUser.MapleUserTable
import app.domain.user.dto.UserCreateRequest
import app.domain.user.model.UserTable
import app.domain.user.repository.UserRepository
import app.infrastructure.database.DatabaseFactory
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapleRepositoryTest{

    private val config = HoconApplicationConfig(ConfigFactory.load("application.yaml"))

    private val database = DatabaseFactory.init(config)
    private lateinit var userRepository: UserRepository
    private lateinit var mapleRepository: MapleRepository

    @BeforeAll
    fun setup() {
        transaction(database) {
            SchemaUtils.create(UserTable, MapleUserTable)
        }
        userRepository = UserRepository(database)
        mapleRepository = MapleRepository(database)
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
    fun `insert - 새로운 유저 데이터를 저장할 수 있어야 한다`() = runBlocking {
        // Given
        val userCreateRequest = UserCreateRequest("testUser", "testPassword")
        val request = MapleUserCreateRequest(
            ocid = "test_ocid",
            name = "테스트유저",
            worldName = "루나",
            classLevel = "5",
            characterClass = "에반",
            level = 230,
            exp = 30828386992,
            expRate = "36.846",
            guildName = "테스트길드",
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

        mapleRepository.insert(request, userId)
        val savedUser = mapleRepository.findById(userId)

        // Then
        assertNotNull(savedUser, "DB에 저장된 데이터가 없습니다.")
        assertEquals(request.name, savedUser!!.name)
        assertEquals(request.level, savedUser.level)
        assertEquals(request.ocid, savedUser.ocid)
    }

    @Test
    fun `update - 기존 유저 데이터를 업데이트할 수 있어야 한다`() = runBlocking {
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

        val updatedRequest = initialRequest.copy(
            name = "업데이트된유저",
            level = 235,
            guildName = "업데이트길드"
        )

        // When
        val user = userCreateRequest.toUser().hashPassword()
        val userId = userRepository.create(user)

        mapleRepository.insert(initialRequest, userId) // 초기 데이터 삽입
        mapleRepository.update(updatedRequest, userId) // 데이터 업데이트
        val updatedUser = mapleRepository.findById(userId)

        // Then
        assertNotNull(updatedUser, "업데이트된 데이터가 없습니다.")
        assertEquals(updatedRequest.name, updatedUser!!.name)
        assertEquals(updatedRequest.level, updatedUser.level)
        assertEquals(updatedRequest.guildName, updatedUser.guildName)
    }
}