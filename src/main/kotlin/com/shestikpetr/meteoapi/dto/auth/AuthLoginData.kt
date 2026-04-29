package com.shestikpetr.meteoapi.dto.auth

data class AuthLoginData(
    val user: UserResponse,
    val tokens: Tokens,
)
