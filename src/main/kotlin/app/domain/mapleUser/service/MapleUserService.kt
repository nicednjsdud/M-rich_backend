package app.domain.mapleUser.service

import app.domain.mapleUser.model.MapleUser
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class MapleUserService(private val client: HttpClient, private val apiKey: String) {
    suspend fun getUser(nickname: String): MapleUser? {
        val response = client.get("https://open.api.nexon.com/maplestory/v1/id") {
            url {
                parameters.append("character_name", nickname)
            }
            header("X-NX-OPEN-API-KEY", apiKey)
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body<MapleUser>()
        } else {
            null
        }
    }
}