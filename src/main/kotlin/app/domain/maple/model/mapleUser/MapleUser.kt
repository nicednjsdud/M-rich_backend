package app.domain.maple.model.mapleUser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapleUser(
    @SerialName("date") val searchDate: String?, // ✅ 조회 기준 날짜
    @SerialName("ocid") val ocid: String, // ✅ 캐릭터 고유 번호 (ocid
    @SerialName("character_name") val name: String, // ✅ 캐릭터 이름
    @SerialName("world_name") val worldName: String, // ✅ 월드 이름 (서버)
    @SerialName("character_class") val characterClass: String, // ✅ 직업명
    @SerialName("character_level") val level: Int, // ✅ 캐릭터 레벨
    @SerialName("character_exp") val exp: Long, // ✅ 경험치
    @SerialName("character_guild_name") val guildName: String?, // ✅ 길드명 (nullable)
    @SerialName("character_image") val imageUrl: String, // ✅ 캐릭터 이미지 URL
    @SerialName("character_gender") val gender: String, // ✅ 성별 추가
    @SerialName("character_class_level") val classLevel: String, // ✅ 직업 세부 단계 추가
    @SerialName("character_exp_rate") val expRate: String, // ✅ 경험치 비율 (String 타입)
    @SerialName("character_date_create") val createdDate: String, // ✅ 캐릭터 생성일
    @SerialName("access_flag") val isAccessible: Boolean, // ✅ 접근 가능 여부
    @SerialName("liberation_quest_clear_flag") val liberationQuestClear: Boolean // ✅ 해방 퀘스트 클리어 여부
)
