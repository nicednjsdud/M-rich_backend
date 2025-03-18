package app.domain.maple.dto

import kotlinx.serialization.Serializable


@Serializable
data class MapleUserCreateRequest (
    val searchDate: String, // ✅ 조회 기준 날짜
    val ocid: String, // ✅ 캐릭터 고유 번호 (ocid
    val name: String, // ✅ 캐릭터 이름
    val worldName: String, // ✅ 월드 이름 (서버)
    val characterClass: String, // ✅ 직업명
    val level: Int, // ✅ 캐릭터 레벨
    val exp: Long, // ✅ 경험치
    val guildName: String, // ✅ 길드명 (nullable)
    val imageUrl: String, // ✅ 캐릭터 이미지 URL
    val gender: String, // ✅ 성별 추가
    val classLevel: String, // ✅ 직업 세부 단계 추가
    val expRate: String, // ✅ 경험치 비율 (String 타입)
    val createdDate: String, // ✅ 캐릭터 생성일
    val isAccessible: Boolean, // ✅ 접근 가능 여부
    val liberationQuestClear: Boolean // ✅ 해방 퀘스트 클리어 여부
)
