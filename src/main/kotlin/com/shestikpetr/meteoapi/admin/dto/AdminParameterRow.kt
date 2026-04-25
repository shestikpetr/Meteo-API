package com.shestikpetr.meteoapi.admin.dto

// Строка таблицы параметров
data class AdminParameterRow(
    val id: Int,
    val code: Int,
    val name: String,
    val unit: String?,
    val description: String?,
    val stationCount: Long,
)
