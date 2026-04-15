package com.shestikpetr.meteoapi.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserRegisterRequest(
    @field:NotBlank(message = "Username обязателен")
    @field:Size(min = 3, max = 50, message = "Username должен быть от 3 до 50 символов")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username: только латиница, цифры и _")
    val username: String,

    @field:NotBlank(message = "Email обязателен")
    @field:Email(message = "Некорректный email")
    val email: String,

    @field:NotBlank(message = "Пароль обязателен")
    @field:Size(min = 6, message = "Пароль: минимум 6 символов")
    val password: String,
)
