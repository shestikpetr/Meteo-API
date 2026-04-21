package com.shestikpetr.meteoapi.dto.sensor

import java.math.BigDecimal

data class StationDataResponse(
    val stationNumber: String,
    val customName: String? = null,
    val isFavorite: Boolean = false,
    val location: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val parameters: List<ParameterWithValue> = emptyList(),
)
