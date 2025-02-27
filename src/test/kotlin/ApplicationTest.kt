package com.wyc

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import io.ktor.server.config.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        // 테스트 환경에서 application.yaml을 로드하도록 설정
        environment {
            config = ApplicationConfig("application.yaml")
        }

        client.get("/").apply {
            assertEquals(200, status.value)
            assertEquals("Hello, mRich!", bodyAsText())
        }
    }

}
