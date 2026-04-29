package com.shestikpetr.meteoapi.dto.sensor

import java.math.BigDecimal

// Последние показания одной станции
data class StationDataResponse(
    val stationNumber: String,
    val name: String,
    val location: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val time: Long? = null,
    val parameters: List<ParameterWithValue> = emptyList(),
)
