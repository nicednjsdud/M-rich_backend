package app.domain.user.service

import app.domain.user.dto.UserRegisterRequest
import app.domain.user.model.UserTable
import app.domain.user.repository.UserRepository
import app.infrastructure.database.DatabaseFactory
import app.utils.APIResult
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository

    // ✅ H2 인메모리 DB 설정
    private val testConfig = MapApplicationConfig().apply {
        put("database.url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        put("database.user", "sa")
        put("database.password", "")
        put("database.driverClassName", "org.h2.Driver")
    }
    private val database = DatabaseFactory.init(testConfig)

    @BeforeAll
    fun setUp() {
        transaction(database) {
            SchemaUtils.create(UserTable)
        }
        userRepository = UserRepository(database)
        userService = UserService(userRepository)
    }

    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UserTable)
            SchemaUtils.create(UserTable)
        }
    }

    @Test
    fun `registerUser - 새로운 유저를 등록하면 ID를 반환해야 한다`() = runBlocking {
        val request = UserRegisterRequest("wycUser", "wycPassword123")
        val result = userService.register(request)

        assert(result is APIResult.Success)
        assertEquals(1, (result as APIResult.Success).data)
    }

    @Test
    fun `registerUser - 중복된 유저를 등록하면 예외가 발생해야 한다`() = runBlocking {
        val request = UserRegisterRequest("wycUser", "wycPassword123")
        userService.register(request)

        val result = userService.register(request)

        assert(result is APIResult.Error)
        assertEquals("이미 존재하는 사용자입니다.", (result as APIResult.Error).error)

    }

    @Test
    fun `loginUser - 올바른 정보로 로그인하면 토큰을 반환해야 한다`() = runBlocking {
        val request = UserRegisterRequest("wycUser", "wycPassword123")
        val register = userService.register(request)

        val result = userService.login(request);

        assertNotNull(result)
        assertEquals("fake_token_${(register as APIResult.Success).data}", (result as APIResult.Success).data)
    }

    @Test
    fun `loginUser - 잘못된 비밀번호로 로그인하면 실패해야 한다`() = runBlocking {
        val request = UserRegisterRequest("wycUser", "wycPassword123")
        userService.register(request)

        val result = userService.login(UserRegisterRequest("wycUser", "wrongPassword"))

        assert(result is APIResult.Error)
        assertEquals("비밀번호가 일치하지 않습니다.", (result as APIResult.Error).error)
    }

    @Test
    fun `loginUser - 존재하지 않는 유저로 로그인하면 실패해야 한다`() = runBlocking {
        val request = UserRegisterRequest("wycUser", "wycPassword123")

        val result = userService.login(request)

        assert(result is APIResult.Error)
        assertEquals("사용자를 찾을 수 없습니다.", (result as APIResult.Error).error)
    }
}
