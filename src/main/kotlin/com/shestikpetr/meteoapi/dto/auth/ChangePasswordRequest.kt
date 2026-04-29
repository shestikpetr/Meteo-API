package com.shestikpetr.meteoapi.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    @field:NotBlank(message = "Текущий пароль обязателен")
    val currentPassword: String,

    @field:NotBlank(message = "Новый пароль обязателен")
    @field:Size(min = 6, message = "Пароль: минимум 6 символов")
    val newPassword: String,
)
