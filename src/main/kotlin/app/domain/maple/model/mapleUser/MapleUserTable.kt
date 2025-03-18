package app.domain.maple.model.mapleUser

import app.domain.user.model.UserTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object MapleUserTable : IntIdTable("maple_users") {
    val ocid = varchar("ocid", 50) // ✅ OCID 추가
    val name = varchar("character_name", 30) // ✅ 캐릭터 이름 (API와 동일한 필드명으로 수정)
    val worldName = varchar("world_name", 30) // ✅ 월드명 (서버명)
    val characterClass = varchar("character_class", 30) // ✅ 직업명
    val classLevel = varchar("character_class_level", 10) // ✅ 직업 세부 단계 추가
    val level = integer("character_level") // ✅ 캐릭터 레벨
    val exp = long("character_exp") // ✅ 경험치
    val expRate = varchar("character_exp_rate", 10) // ✅ 경험치 비율 (String)
    val guildName = varchar("character_guild_name", 30).nullable() // ✅ 길드명 (nullable)
    val imageUrl = varchar("character_image", 255) // ✅ 캐릭터 이미지 URL
    val gender = varchar("character_gender", 10) // ✅ 성별 추가
    val createdDate = varchar("character_date_create", 20) // ✅ 캐릭터 생성일 (String으로 저장)
    val isAccessible = bool("access_flag") // ✅ 접근 가능 여부
    val liberationQuestClear = bool("liberation_quest_clear_flag") // ✅ 해방 퀘스트 클리어 여부
    val searchDate = varchar("date", 20) // ✅ 조회 기준일 추가
    val userId = reference("user_id", UserTable.id, onDelete = ReferenceOption.CASCADE) // ✅ 외래키 추가 (UserTable 연결)
}
