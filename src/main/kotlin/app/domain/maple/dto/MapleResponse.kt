package app.domain.maple.dto

data class MapleResponse (
    val id : Int,
    val searchDate: String,
    val ocid: String,
    val name: String,
    val worldName: String,
    val characterClass: String,
    val level: Int,
    val exp: Long,
    val guildName: String,
    val imageUrl: String,
    val gender: String,
    val classLevel: String,
    val expRate: String,
    val createdDate: String,
    val isAccessible: Boolean,
    val liberationQuestClear: Boolean
)