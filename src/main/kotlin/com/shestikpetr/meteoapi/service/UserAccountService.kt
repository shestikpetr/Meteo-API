package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.dto.auth.ChangePasswordRequest
import com.shestikpetr.meteoapi.dto.auth.UpdateMeRequest
import com.shestikpetr.meteoapi.dto.auth.UserResponse
import com.shestikpetr.meteoapi.entity.User
import com.shestikpetr.meteoapi.exception.AuthenticationException
import com.shestikpetr.meteoapi.exception.ConflictException
import com.shestikpetr.meteoapi.exception.NotFoundException
import com.shestikpetr.meteoapi.exception.ValidationException
import com.shestikpetr.meteoapi.repository.UserRepository
import com.shestikpetr.meteoapi.service.mapper.UserMapper
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Управление профилем пользователя: чтение, обновление, смена пароля
@Service
@Transactional
class UserAccountService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    @Transactional(readOnly = true)
    fun me(userId: Int): UserResponse = UserMapper.toResponse(loadUser(userId))

    fun updateMe(userId: Int, request: UpdateMeRequest): UserResponse {
        if (request.username == null && request.email == null) {
            throw ValidationException("Нужно указать username и/или email")
        }
        val user = loadUser(userId)
        request.username?.let { applyUsername(user, it) }
        request.email?.let { applyEmail(user, it) }
        return UserMapper.toResponse(userRepository.save(user))
    }

    fun changePassword(userId: Int, request: ChangePasswordRequest) {
        val user = loadUser(userId)
        if (!passwordEncoder.matches(request.currentPassword, user.passwordHash)) {
            throw AuthenticationException("Неверный текущий пароль")
        }
        if (passwordEncoder.matches(request.newPassword, user.passwordHash)) {
            throw ValidationException("Новый пароль должен отличаться от текущего")
        }
        user.passwordHash = passwordEncoder.encode(request.newPassword)
        userRepository.save(user)
    }

    private fun applyUsername(user: User, newUsername: String) {
        if (newUsername == user.username) return
        if (userRepository.existsByUsername(newUsername)) {
            throw ConflictException("Пользователь с таким username уже существует")
        }
        user.username = newUsername
    }

    private fun applyEmail(user: User, newEmail: String) {
        val normalized = newEmail.lowercase()
        if (normalized == user.email) return
        if (userRepository.existsByEmail(normalized)) {
            throw ConflictException("Пользователь с таким email уже существует")
        }
        user.email = normalized
    }

    private fun loadUser(userId: Int): User = userRepository.findById(userId)
        .orElseThrow { NotFoundException("Пользователь не найден") }
}
