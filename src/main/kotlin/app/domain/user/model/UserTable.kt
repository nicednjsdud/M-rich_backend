package app.domain.user.model

import org.jetbrains.exposed.dao.id.IntIdTable

object UserTable : IntIdTable("users") {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
}

