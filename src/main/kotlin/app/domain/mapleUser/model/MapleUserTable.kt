package app.domain.mapleUser.model

import org.jetbrains.exposed.sql.Table

object MapleUsers : Table("maple_users") {
    val ocid = varchar("ocid", 50).uniqueIndex()
    val name = varchar("name", 30)
    val worldName = varchar("world_name", 30)
    val characterClass = varchar("character_class", 30)
    val level = integer("level")
    val exp = long("exp")
    val guildName = varchar("guild_name", 30).nullable()
    val popularity = integer("popularity")
    val imageUrl = varchar("image_url", 255)
    var user_id = integer("user_id")
    // todo add foreign key
}
