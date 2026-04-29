package com.shestikpetr.meteoapi.dto.auth

import com.shestikpetr.meteoapi.dto.common.UserRole

data class UserResponse(
    val username: String,
    val email: String,
    val role: UserRole,
)
