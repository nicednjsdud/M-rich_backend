package app.domain.user.repository

import app.domain.user.model.User
import app.domain.user.model.UserTable
import app.infrastructure.database.DatabaseFactory
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest{

    private lateinit var userRepository: UserRepository

    // 테스트용 H2 인메모리 DB 설정
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
    }

    @AfterEach
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UserTable)
            SchemaUtils.create(UserTable)
        }
    }


    @Test
    fun `유저를 정상적으로 생성할 수 있다`() = runBlocking {
        val createUser = User(username = "wycUser", password = "wycPassword123")
        val userId = userRepository.create(createUser)
        assertNotNull(userId)

        val user = userRepository.findByUsername("wycUser")
        assertNotNull(user)
        assertEquals("wycUser", user?.username)
    }
}