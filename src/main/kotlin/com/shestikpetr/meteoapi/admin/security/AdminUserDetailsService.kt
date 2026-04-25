package com.shestikpetr.meteoapi.admin.security

import com.shestikpetr.meteoapi.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.User as SpringUser

// Источник пользователей для form-login админки
// Загружает кого угодно; ограничение по роли делает SecurityFilterChain (hasRole('ADMIN'))
@Service
class AdminUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Пользователь не найден: $username")
        return toSpringUser(user)
    }

    private fun toSpringUser(user: com.shestikpetr.meteoapi.entity.User): UserDetails = SpringUser
        .withUsername(user.username!!)
        .password(user.passwordHash!!)
        .disabled(!user.isActive)
        .authorities(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        .build()
}
