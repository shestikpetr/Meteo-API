package com.shestikpetr.meteoapi.dto.sensor

data class ParameterHistoryResponse(
    val stationNumber: String,
    val parameter: ParameterMetadata,
    val data: List<TimeSeriesPoint>,
    val count: Int,
    val success: Boolean = true,
)
