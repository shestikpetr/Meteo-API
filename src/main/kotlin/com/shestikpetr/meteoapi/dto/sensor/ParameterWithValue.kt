package com.shestikpetr.meteoapi.dto.sensor

data class ParameterWithValue(
    val code: String,
    val name: String,
    val value: Double? = null,
    val time: Long? = null,
    val unit: String? = null,
    val category: String? = null,
)
