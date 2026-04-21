package com.shestikpetr.meteoapi.dto.sensor

data class ParameterMetadata(
    val code: Int,
    val name: String,
    val unit: String? = null,
    val category: String? = null,
)
