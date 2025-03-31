package app.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.config.*
import java.util.*

object JwtConfig {
    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var audience: String
    private lateinit var realm: String
    private const val accessTokenValidityInMs = 15 * 60 * 1000L
    private const val refreshTokenValidityInMs = 7 * 24 * 60 * 60 * 1000L
    private lateinit var algorithm: Algorithm

    fun init(config: ApplicationConfig) {
        secret = config.property("jwt.secret").getString()
        issuer = config.property("jwt.issuer").getString()
        audience = config.property("jwt.audience").getString()
        algorithm = Algorithm.HMAC256(secret)
    }

    fun getAlgorithm(): Algorithm = algorithm
    fun getIssuer(): String = issuer
    fun getAudience(): String = audience

    fun createAccessToken(userId: Int): String =
        JWT.create()
            .withSubject("AccessToken")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenValidityInMs))
            .sign(algorithm)

    fun createRefreshToken(userId: Int): String =
        JWT.create()
            .withSubject("RefreshToken")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenValidityInMs))
            .sign(algorithm)

    fun verifyRefreshToken(refreshToken: String): Int {
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

        val decodedJWT = verifier.verify(refreshToken)
        return decodedJWT.getClaim("userId").asInt()
    }

    fun getRefreshTokenExpiration() = refreshTokenValidityInMs.toLong()

}