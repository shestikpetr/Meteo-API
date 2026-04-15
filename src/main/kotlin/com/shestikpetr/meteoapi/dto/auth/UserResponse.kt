package com.shestikpetr.meteoapi.dto.auth

import com.shestikpetr.meteoapi.dto.common.UserRole
import java.time.Instant

data class UserResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: UserRole,
    val isActive: Boolean,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)
