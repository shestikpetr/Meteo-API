package com.shestikpetr.meteoapi.dto.station

import java.math.BigDecimal
import java.time.Instant

data class StationResponse(
    val id: Int,
    val stationNumber: String,
    val name: String,
    val location: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val altitude: BigDecimal? = null,
    val isActive: Boolean = true,
    val createdAt: Instant? = null,
    val updatedAt: Instant? = null,
)
