package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.dto.auth.AuthLoginData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenRequest
import com.shestikpetr.meteoapi.dto.auth.UserLoginRequest
import com.shestikpetr.meteoapi.dto.auth.UserRegisterRequest
import com.shestikpetr.meteoapi.entity.User
import com.shestikpetr.meteoapi.exception.AuthenticationException
import com.shestikpetr.meteoapi.exception.ConflictException
import com.shestikpetr.meteoapi.repository.UserRepository
import com.shestikpetr.meteoapi.security.JwtService
import io.jsonwebtoken.Claims
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {

    fun register(request: UserRegisterRequest): AuthLoginData {
        requireUniqueCredentials(request)
        val user = userRepository.save(newUser(request))
        return issueLoginData(user)
    }

    fun login(request: UserLoginRequest): AuthLoginData {
        val user = findUserByUsername(request.username)
        verifyPassword(request.password, user)
        ensureActive(user)
        return issueLoginData(user)
    }

    @Transactional(readOnly = true)
    fun refresh(request: RefreshTokenRequest): RefreshTokenData {
        val claims = jwtService.parseRefreshToken(request.refreshToken)
        val user = loadActiveUserFromClaims(claims)
        return RefreshTokenData(accessToken = newAccessToken(user))
    }

    private fun requireUniqueCredentials(request: UserRegisterRequest) {
        requireUsernameAvailable(request.username)
        requireEmailAvailable(request.email)
    }

    private fun requireUsernameAvailable(username: String) {
        if (userRepository.existsByUsername(username)) {
            throw ConflictException("Пользователь с таким username уже существует")
        }
    }

    private fun requireEmailAvailable(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw ConflictException("Пользователь с таким email уже существует")
        }
    }

    private fun newUser(request: UserRegisterRequest): User = User().apply {
        username = request.username
        email = request.email.lowercase()
        passwordHash = passwordEncoder.encode(request.password)
    }

    private fun findUserByUsername(username: String): User = userRepository
        .findByUsername(username)
        ?: throw AuthenticationException(INVALID_CREDENTIALS)

    private fun verifyPassword(
        rawPassword: String,
        user: User,
    ) {
        if (!passwordEncoder.matches(rawPassword, user.passwordHash)) {
            throw AuthenticationException(INVALID_CREDENTIALS)
        }
    }

    private fun ensureActive(user: User) {
        if (!user.isActive) throw AuthenticationException("Аккаунт деактивирован")
    }

    private fun loadActiveUserFromClaims(claims: Claims): User {
        val userId = extractUserId(claims)
        val user = userRepository.findById(userId).orElseThrow {
            AuthenticationException("Пользователь не найден")
        }
        ensureActive(user)
        return user
    }

    private fun extractUserId(claims: Claims): Int = claims.subject?.toIntOrNull()
        ?: throw AuthenticationException("Некорректный subject в refresh-токене")

    private fun issueLoginData(user: User): AuthLoginData = AuthLoginData(
        userId = user.id!!,
        username = user.username!!,
        accessToken = newAccessToken(user),
        refreshToken = jwtService.generateRefreshToken(user.id!!),
        expiresIn = jwtService.accessTokenTtlSeconds,
    )

    private fun newAccessToken(user: User): String = jwtService.generateAccessToken(user.id!!, user.username!!, user.role)

    private companion object {
        const val INVALID_CREDENTIALS = "Неверный username или пароль"
    }
}
