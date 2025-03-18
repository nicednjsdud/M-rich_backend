package app.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {


        application {
            val client = HttpClient(CIO) {
                install(ContentNegotiation){
                    json()
                }
            }
            configureRouting(client)
        }

        client.get("/").apply {
//            assertEquals(200, status.value)
//            assertEquals("Hello, mRich!", bodyAsText())
        }
    }
}
