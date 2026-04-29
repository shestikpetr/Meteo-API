package com.shestikpetr.meteoapi.dto.auth

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long,
)
