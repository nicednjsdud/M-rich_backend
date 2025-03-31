package app.infrastructure.redis

import io.lettuce.core.RedisClient
import io.ktor.server.config.*
import io.lettuce.core.api.sync.RedisCommands

object RedisConfig {
    lateinit var client: RedisClient
    lateinit var commands: RedisCommands<String, String>

    fun init(config: ApplicationConfig) {
        val host = config.property("redis.host").getString()
        val port = config.property("redis.port").getString()
        val timeout = config.property("redis.timeout").getString()

        val redisUrl = "redis://$host:$port"
        client = RedisClient.create(redisUrl)
        val connection = client.connect()
        commands = connection.sync()

        println("✅ Redis connected: $redisUrl")
    }
}
