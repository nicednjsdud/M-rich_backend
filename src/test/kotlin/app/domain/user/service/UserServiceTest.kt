package app.domain.user.service

import app.domain.user.dto.UserCreateRequest
import app.domain.user.model.UserTable
import app.domain.user.repository.UserRepository
import app.infrastructure.database.DatabaseFactory
import app.utils.APIResult
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository

    private val config = HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
    private val database = DatabaseFactory.init(config)

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
    fun `registerUser - 새로운 유저를 등록하면 ID를 반환해야 한다`() = runBlocking {
        val request = UserCreateRequest("wycUser", "wycPassword123")
        val result = userService.register(request)

        val user = userRepository.findByUsername("wycUser")
        assert(result is APIResult.Success)
        assertEquals(user?.id, (result as APIResult.Success).data)
    }

    @Test
    fun `registerUser - 중복된 유저를 등록하면 예외가 발생해야 한다`() = runBlocking {
        val request = UserCreateRequest("wycUser", "wycPassword123")
        userService.register(request)

        val result = userService.register(request)

        assert(result is APIResult.Error)
        assertEquals("이미 존재하는 사용자입니다.", (result as APIResult.Error).error)

    }
}
