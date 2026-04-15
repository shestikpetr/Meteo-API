package com.shestikpetr.meteoapi.dto.auth

data class AuthLoginData(
    val userId: Int,
    val username: String,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "bearer",
    val expiresIn: Long,
)
