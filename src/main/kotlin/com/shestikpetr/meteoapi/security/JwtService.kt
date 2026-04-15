package com.shestikpetr.meteoapi.security

import com.shestikpetr.meteoapi.exception.AuthenticationException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    private val properties: JwtProperties,
) {

    private val signingKey: SecretKey = Keys.hmacShaKeyFor(properties.secret.toByteArray(Charsets.UTF_8))

    val accessTokenTtlSeconds: Long get() = properties.accessTokenExpiration

    fun generateAccessToken(
        userId: Int,
        username: String,
        role: String,
    ): String = buildToken(
        subject = userId.toString(),
        ttlSeconds = properties.accessTokenExpiration,
        extraClaims = mapOf(
            CLAIM_USERNAME to username,
            CLAIM_ROLE to role,
            CLAIM_TYPE to TYPE_ACCESS,
        ),
    )

    fun generateRefreshToken(userId: Int): String = buildToken(
        subject = userId.toString(),
        ttlSeconds = properties.refreshTokenExpiration,
        extraClaims = mapOf(CLAIM_TYPE to TYPE_REFRESH),
    )

    fun parseAccessToken(token: String): Claims = parseTokenOfType(token, TYPE_ACCESS)

    fun parseRefreshToken(token: String): Claims = parseTokenOfType(token, TYPE_REFRESH)

    private fun buildToken(
        subject: String,
        ttlSeconds: Long,
        extraClaims: Map<String, Any>,
    ): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(subject)
            .claims(extraClaims)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(ttlSeconds)))
            .signWith(signingKey)
            .compact()
    }

    private fun parseTokenOfType(
        token: String,
        expectedType: String,
    ): Claims {
        val claims = parseAndVerify(token)
        requireTokenType(claims, expectedType)
        return claims
    }

    private fun parseAndVerify(token: String): Claims = try {
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
    } catch (e: JwtException) {
        throw AuthenticationException("Недействительный или просроченный токен")
    } catch (e: IllegalArgumentException) {
        throw AuthenticationException("Недействительный или просроченный токен")
    }

    private fun requireTokenType(
        claims: Claims,
        expectedType: String,
    ) {
        val actualType = claims[CLAIM_TYPE] as? String
        if (actualType != expectedType) {
            throw AuthenticationException("Ожидался токен типа $expectedType, получен $actualType")
        }
    }

    private companion object {
        const val CLAIM_USERNAME = "username"
        const val CLAIM_ROLE = "role"
        const val CLAIM_TYPE = "type"
        const val TYPE_ACCESS = "access"
        const val TYPE_REFRESH = "refresh"
    }
}
