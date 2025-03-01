package app.domain.user.model

import org.junit.jupiter.api.Assertions.*
import org.mindrot.jbcrypt.BCrypt
import kotlin.test.Test

class UserTest{

    @Test
    fun `비밀번호가 정상적으로 해싱되는가`() {
        val rawPassword = "wycPassword123"
        val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

        assertNotEquals(rawPassword, hashedPassword)
    }

    @Test
    fun `해싱된 비밀번호가 올바르게 검증되는가`() {
        val rawPassword = "wycPassword123"
        val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

        assertTrue(BCrypt.checkpw(rawPassword, hashedPassword))
    }

    @Test
    fun `잘못된 비밀번호는 검증에 실패해야 한다`() {
        val rawPassword = "wycPassword123"
        val wrongPassword = "wycPassword1234"
        val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())

        assertFalse(BCrypt.checkpw(wrongPassword, hashedPassword))
    }
}