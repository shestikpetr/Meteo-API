package com.shestikpetr.meteoapi.dto.sensor

data class ParameterWithValue(
    val code: Int,
    val name: String,
    val value: Double? = null,
    val time: Long? = null,
    val unit: String? = null,
)
