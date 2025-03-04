package app.domain.mapleUser.model

data class MapleUser(
    val ocid: String,
    val name: String,
    val worldName: String,
    val characterClass: String,
    val level: Int,
    val exp: Long,
    val guildName: String?,
    val popularity: Int,
    val imageUrl: String,
    val userId: Int
)