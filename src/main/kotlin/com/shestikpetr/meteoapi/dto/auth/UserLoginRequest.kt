package com.shestikpetr.meteoapi.dto.auth

import jakarta.validation.constraints.NotBlank

data class UserLoginRequest(
    @field:NotBlank(message = "Username обязателен")
    val username: String,

    @field:NotBlank(message = "Пароль обязателен")
    val password: String,
)
