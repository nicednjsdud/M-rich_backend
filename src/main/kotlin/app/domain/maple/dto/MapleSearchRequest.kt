package app.domain.maple.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapleSearchRequest(
    val nickName: String,
)