package app.domain.auth.service

import app.config.JwtConfig
import app.domain.auth.dto.LoginRequest
import app.domain.auth.dto.RefreshRequest
import app.domain.auth.repository.AuthRepository
import app.domain.user.model.User
import app.domain.user.model.UserTable
import app.domain.user.repository.UserRepository
import app.infrastructure.database.DatabaseFactory
import app.infrastructure.redis.RedisConfig
import app.utils.APIResult
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest{
    val config = HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
    private val database = DatabaseFactory.init(config)

    private lateinit var authService: AuthService
    private lateinit var authRepository: AuthRepository
    private lateinit var userRepository: UserRepository

    @BeforeAll
    fun setup() {
        JwtConfig.init(config)
        RedisConfig.init(config)
        transaction(database) {
            SchemaUtils.create(UserTable)
        }
        userRepository = UserRepository(database)
        authRepository = AuthRepository()
        authService = AuthService(authRepository, userRepository)
    }

    @AfterEach
    fun tearDown() {
        transaction(database) {
            UserTable.deleteAll()
        }
    }

    @AfterAll
    fun tearDownAll() {
        transaction {
            SchemaUtils.drop(UserTable)
        }
    }

    @Test
    fun `login - 사용자가 로그인할 수 있어야 한다`(): Unit = runBlocking {
        // Given
        val createUser = User.create("wycUser", "wycPassword123")
        userRepository.create(createUser)

        val loginRequest = LoginRequest(username = "wycUser", password = "wycPassword123")

        // When
        val result = authService.login(loginRequest)

        // Then
        assertTrue(result is APIResult.Success)
        assertNotNull((result as APIResult.Success).data.accessToken)
        assertNotNull(result.data.refreshToken)
    }

    @Test
    fun `login - 사용자가 없을 경우 에러가 발생해야 한다`(): Unit = runBlocking {
        // Given
        val loginRequest = LoginRequest(username = "wycUser", password = "wycPassword123")

        // When
        val result = authService.login(loginRequest)

        // Then
        assertTrue(result is APIResult.Error)
        assertEquals("사용자를 찾을 수 없습니다.", (result as APIResult.Error).error)
    }

    @Test
    fun `login - 비밀번호가 일치하지 않을 경우 에러가 발생해야 한다`(): Unit = runBlocking {
        // Given
        val createUser = User.create("wycUser", "wycPassword123")
        userRepository.create(createUser)

        val loginRequest = LoginRequest(username = "wycUser", password = "wycPassword1234")

        // When
        val result = authService.login(loginRequest)

        // Then
        assertTrue(result is APIResult.Error)
        assertEquals("비밀번호가 일치하지 않습니다.", (result as APIResult.Error).error)
    }

    @Test
    fun `logout - 사용자가 로그아웃할 수 있어야 한다`(): Unit = runBlocking {
        // Given
        val createUser = User.create("wycUser", "wycPassword123")
        userRepository.create(createUser)

        val loginRequest = LoginRequest(username = "wycUser", password = "wycPassword123")
        val loginResult = authService.login(loginRequest)
        val userId = userRepository.findByUsername("wycUser")!!.id!!

        // When
        authService.logout(userId)

        // Then
        assertNull(authRepository.findUserIdByToken((loginResult as APIResult.Success).data.refreshToken))
    }

    @Test
    fun `refreshToken - 리프레시 토큰을 검증하고 새로운 액세스 토큰을 발급해야 한다`(): Unit = runBlocking {
        // Given
        val createUser = User.create("wycUser", "wycPassword123")
        userRepository.create(createUser)

        val loginRequest = LoginRequest(username = "wycUser", password = "wycPassword123")
        val loginResult = authService.login(loginRequest)
        val refreshToken = (loginResult as APIResult.Success).data.refreshToken

        val refreshRequest = RefreshRequest(refreshToken)
        // When
        val result = authService.refreshToken(refreshRequest)

        // Then
        assertTrue(result is APIResult.Success)
        assertNotNull((result as APIResult.Success).data.accessToken)
        assertNotEquals(refreshToken, result.data.accessToken)
    }

    @Test
    fun `refreshToken - 리프레시 토큰이 유효하지 않을 경우 에러가 발생해야 한다`(): Unit = runBlocking {
        // Given
        val refreshRequest = RefreshRequest("invalidRefreshToken")

        // When
        val result = authService.refreshToken(refreshRequest)

        // Then
        assertTrue(result is APIResult.Error)
        assertEquals("리프레시 토큰이 유효하지 않습니다.", (result as APIResult.Error).error)
    }
}