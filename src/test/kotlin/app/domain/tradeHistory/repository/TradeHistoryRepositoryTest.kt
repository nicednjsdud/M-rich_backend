package app.domain.tradeHistory.repository

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.model.TradeHistoryTable
import app.domain.user.model.User
import app.domain.user.model.UserTable
import app.domain.user.repository.UserRepository
import app.infrastructure.database.DatabaseFactory
import app.utils.TradeStatus
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class TradeHistoryRepositoryTest {

    private val config = HoconApplicationConfig(ConfigFactory.load("application.yaml"))

    private val database = DatabaseFactory.init(config)
    private lateinit var tradeHistoryRepository: TradeHistoryRepository
    private lateinit var userRepository: UserRepository

    @BeforeAll
    fun setup() {
        transaction(database) {
            SchemaUtils.create(TradeHistoryTable, UserTable)
        }
        tradeHistoryRepository = TradeHistoryRepository(database)
        userRepository = UserRepository(database)
    }

    @AfterEach
    fun tearDown() {
        transaction(database) {
            TradeHistoryTable.deleteAll()
            UserTable.deleteAll()
        }
    }

    @AfterAll
    fun tearDownAll() {
        transaction {
            SchemaUtils.drop(TradeHistoryTable)
            SchemaUtils.drop(UserTable)
        }
    }

    @Test
    fun `insert - 새로운 거래내역 데이터를 저장할 수 있어야 한다`() = runBlocking {
        // Given
        val createUser = User(username = "wycUser", password = "wycPassword123")
        val userId = userRepository.create(createUser)

        val tradeHistoryRequest = TradeHistoryRequest(
            id = null,
            tradeDate = LocalDateTime.now().toString(),
            itemName = "아이템1",
            price = 1000,
            korPrice = "(1,000원)",
            status = TradeStatus.COMPLETE,
            isChecked = true,
            isDeleted = false
        )

        // When
        val tradeHistoryId = tradeHistoryRepository.save(tradeHistoryRequest, userId)

        var tradeHistory = tradeHistoryRepository.findById(tradeHistoryId)

        // Then
        assertNotNull(tradeHistory)
        assertEquals("아이템1", tradeHistory?.itemName)
        assertEquals(1000, tradeHistory?.price)
        assertEquals("(1,000원)", tradeHistory?.korPrice)
        assertEquals(TradeStatus.COMPLETE, tradeHistory?.status)
        assertEquals(userId, tradeHistory?.userId)
    }

    @Test
    fun `update - 거래내역 데이터를 수정할 수 있어야 한다`() = runBlocking {
        // Given
        val createUser = User(username = "wycUser", password = "wycPassword123")
        val userId = userRepository.create(createUser)

        val tradeHistoryRequest = TradeHistoryRequest(
            id = null,
            tradeDate = LocalDateTime.now().toString(),
            itemName = "아이템1",
            price = 1000,
            korPrice = "(1,000원)",
            status = TradeStatus.COMPLETE,
            isChecked = true,
            isDeleted = false
        )

        val tradeHistoryId = tradeHistoryRepository.save(tradeHistoryRequest, userId)

        // When
        val changeHistoryRequest = TradeHistoryRequest(
            id = tradeHistoryId,
            tradeDate = LocalDateTime.now().toString(),
            itemName = "아이템2",
            price = 2000,
            korPrice = "(2,000원)",
            status = TradeStatus.COMPLETE,
            isChecked = true,
            isDeleted = false
        )
        tradeHistoryRepository.update(changeHistoryRequest)

        val historyList = tradeHistoryRepository.findAllByUserId(userId)

        // Then
        assertNotNull(historyList)
        assertEquals(1, historyList.size)
        assertEquals("아이템2", historyList[0].itemName)
        assertEquals(2000, historyList[0].price)
    }

    @Test
    fun `update - 존재하지 않는 거래내역 수정 시 아무 일도 일어나지 않아야 한다`() = runBlocking {
        val updateRequest = TradeHistoryRequest(
            id = 99999, // 없는 ID
            tradeDate = LocalDateTime.now().toString(),
            itemName = "잘못된 데이터",
            price = 0,
            korPrice = "(0원)",
            status = TradeStatus.COMPLETE,
            isChecked = true,
            isDeleted = false
        )

        tradeHistoryRepository.update(updateRequest)

        val all = tradeHistoryRepository.findAllByUserId(1)
        assertEquals(0, all.size)
    }

    @Test
    fun `delete - 거래내역 데이터를 삭제할 수 있어야 한다`() = runBlocking {
        // Given
        val createUser = User(username = "wycUser", password = "wycPassword123")
        val userId = userRepository.create(createUser)

        val tradeHistoryRequest = TradeHistoryRequest(
            id = null,
            tradeDate = LocalDateTime.now().toString(),
            itemName = "아이템1",
            price = 1000,
            korPrice = "(1,000원)",
            status = TradeStatus.COMPLETE,
            isChecked = true,
            isDeleted = false
        )

        val tradeHistoryId = tradeHistoryRepository.save(tradeHistoryRequest, userId)

        // When
        tradeHistoryRepository.delete(tradeHistoryId)

        val historyList = tradeHistoryRepository.findAllByUserId(userId)

        // Then
        assertNotNull(historyList)
        assertEquals(0, historyList.size)
    }
}