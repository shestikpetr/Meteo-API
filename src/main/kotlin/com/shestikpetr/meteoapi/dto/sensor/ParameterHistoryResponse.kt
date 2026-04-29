package com.shestikpetr.meteoapi.dto.sensor

data class ParameterHistoryResponse(
    val parameter: ParameterMetadata,
    val data: List<TimeSeriesPoint>,
)
