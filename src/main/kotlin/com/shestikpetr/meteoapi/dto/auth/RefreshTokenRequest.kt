package com.shestikpetr.meteoapi.dto.auth

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh-токен обязателен")
    val refreshToken: String,
)
