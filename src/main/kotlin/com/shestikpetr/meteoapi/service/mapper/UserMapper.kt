package com.shestikpetr.meteoapi.service.mapper

import com.shestikpetr.meteoapi.dto.auth.UserResponse
import com.shestikpetr.meteoapi.entity.User

object UserMapper {

    fun toResponse(user: User): UserResponse = UserResponse(
        username = user.username ?: error("User без username"),
        email = user.email ?: error("User без email"),
        role = user.role,
    )
}
