package com.shestikpetr.meteoapi.security

import com.shestikpetr.meteoapi.dto.common.UserRole

data class UserPrincipal(
    val userId: Int,
    val username: String,
    val role: UserRole,
)
