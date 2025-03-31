package app.config

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.jwt.JWTPrincipal

fun Application.configureSecurity() {
    install(Authentication) {
        jwt {
            realm = JwtConfig.getIssuer()
            verifier(
                JWT.require(JwtConfig.getAlgorithm())
                    .withAudience(JwtConfig.getAudience())
                    .withIssuer(JwtConfig.getIssuer())
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                if (userId != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}