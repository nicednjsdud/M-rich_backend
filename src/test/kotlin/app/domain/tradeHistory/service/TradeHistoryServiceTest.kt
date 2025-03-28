package app.domain.tradeHistory.service

import app.domain.tradeHistory.dto.TradeHistoryRequest
import app.domain.tradeHistory.model.TradeHistoryTable
import app.domain.tradeHistory.repository.TradeHistoryRepository
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
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TradeHistoryServiceTest{
    private val config = HoconApplicationConfig(ConfigFactory.load("application.yaml"))
    private val database = DatabaseFactory.init(config)

    private lateinit var tradeHistoryService: TradeHistoryService
    private lateinit var tradeHistoryRepository: TradeHistoryRepository
    private lateinit var userRepository: UserRepository

    @BeforeAll
    fun setup() {
        transaction(database) {
            SchemaUtils.create(TradeHistoryTable, UserTable)
        }
        tradeHistoryRepository = TradeHistoryRepository(database)
        userRepository = UserRepository(database)
        tradeHistoryService = TradeHistoryService(tradeHistoryRepository)
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
    fun `findAll - 거래내역을 전체 조회할 수 있어야 한다`() = runBlocking {
        // Given
        val createUser = User(username = "wycUser", password = "wycPassword123")
        val userId = userRepository.create(createUser)

        val tradeHistoryRequestList = listOf(
            TradeHistoryRequest(
                id = null,
                tradeDate = LocalDateTime.now().toString(),
                itemName = "아이템1",
                price = 1000,
                korPrice = "(1,000원)",
                status = TradeStatus.COMPLETE,
                isChecked = true,
                isDeleted = false
            ),
            TradeHistoryRequest(
                id = null,
                tradeDate = LocalDateTime.now().toString(),
                itemName = "아이템2",
                price = 2000,
                korPrice = "(2,000원)",
                status = TradeStatus.COMPLETE,
                isChecked = true,
                isDeleted = false
        ))

        tradeHistoryService.save(tradeHistoryRequestList, userId)

        // When
        val result = tradeHistoryService.findAllByUserId(userId)
        // Then
        assertNotNull(result)
        assertEquals(2, result.data.size)
        assertEquals("아이템1", result.data[0].itemName)
    }

    @Test
    fun `save - 거래내역을 저장할 수 있어야 한다`() = runBlocking {
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
        tradeHistoryService.save(listOf(tradeHistoryRequest), userId)

        val tradeHistory = tradeHistoryService.findAllByUserId(userId).data.first()

        // Then
        assertNotNull(tradeHistory)
        assertEquals("아이템1", tradeHistory.itemName)
        assertEquals(1000, tradeHistory.price)
        assertEquals("(1,000원)", tradeHistory.korPrice)
        assertEquals(TradeStatus.COMPLETE, tradeHistory.status)
    }

    @Test
    fun `update - 거래내역을 수정할 수 있어야 한다`() = runBlocking {
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

        tradeHistoryService.save(listOf(tradeHistoryRequest), userId)

        val tradeHistoryId = tradeHistoryService.findAllByUserId(userId).data.first().id

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
        tradeHistoryService.update(listOf(changeHistoryRequest))

        val historyList = tradeHistoryService.findAllByUserId(userId).data

        // Then
        assertNotNull(historyList)
        assertEquals(1, historyList.size)
        assertEquals("아이템2", historyList[0].itemName)
    }

    @Test
    fun `deleteChecked - 체크된 거래내역을 삭제할 수 있어야 한다`() = runBlocking {
        // Given
        val createUser = User(username = "wycUser", password = "wycPassword123")
        val userId = userRepository.create(createUser)

        val tradeHistoryRequestList = listOf(
            TradeHistoryRequest(
                id = null,
                tradeDate = LocalDateTime.now().toString(),
                itemName = "아이템1",
                price = 1000,
                korPrice = "(1,000원)",
                status = TradeStatus.COMPLETE,
                isChecked = true,
                isDeleted = false
            ),
            TradeHistoryRequest(
                id = null,
                tradeDate = LocalDateTime.now().toString(),
                itemName = "아이템2",
                price = 2000,
                korPrice = "(2,000원)",
                status = TradeStatus.COMPLETE,
                isChecked = true,
                isDeleted = false
        ))

        tradeHistoryService.save(tradeHistoryRequestList, userId)
        val savedHistoryList = tradeHistoryService.findAllByUserId(userId)
        val deleteRequest = listOf(
            TradeHistoryRequest(
                id = savedHistoryList.data[0].id,
                tradeDate = LocalDateTime.now().toString(),
                itemName = "아이템1",
                price = 1000,
                korPrice = "(1,000원)",
                status = TradeStatus.COMPLETE,
                isChecked = true,
                isDeleted = true
            )
        )

        // When
        tradeHistoryService.delete(deleteRequest)

        val result = tradeHistoryService.findAllByUserId(userId)
        // Then
        assertNotNull(result)
        assertEquals(1, result.data.size)
        assertEquals("아이템2", result.data[0].itemName)
    }
}