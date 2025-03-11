package app.domain.mapleUser.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapleSearchRequest(
    val nickName: String,
)