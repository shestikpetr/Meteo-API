package com.shestikpetr.meteoapi.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateMeRequest(
    @field:Size(min = 3, max = 50, message = "Username должен быть от 3 до 50 символов")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username: только латиница, цифры и _")
    val username: String? = null,

    @field:Email(message = "Некорректный email")
    val email: String? = null,
)
