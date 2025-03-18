package app.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    fun init(config: ApplicationConfig): Database {
        // ✅ 설정값이 존재하지 않을 경우 기본값 설정
        val url = config.propertyOrNull("database.url")?.getString() ?: "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        val user = config.propertyOrNull("database.user")?.getString() ?: "sa"
        val password = config.propertyOrNull("database.password")?.getString() ?: ""
        val driver = config.propertyOrNull("database.driverClassName")?.getString() ?: "org.h2.Driver"
        logger.info("✅ Connecting to database: $url with driver: $driver")


        val hikariConfig = HikariConfig().apply {
            jdbcUrl = url
            username = user
            setPassword(password)
            driverClassName = driver
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }

        return Database.connect(HikariDataSource(hikariConfig))
    }
}
