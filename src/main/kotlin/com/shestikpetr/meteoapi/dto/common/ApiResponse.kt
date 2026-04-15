package com.shestikpetr.meteoapi.dto.common

data class ApiResponse<T>(
    val data: T,
    val success: Boolean = true,
)
