package app.infrastructure.database

import io.ktor.server.application.*

fun Application.configureDatabases() {
   val config = environment.config
    DatabaseFactory.init(config)
}
