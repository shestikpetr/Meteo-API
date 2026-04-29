package com.shestikpetr.meteoapi.dto.station

import java.math.BigDecimal

data class UserStationResponse(
    val stationNumber: String,
    val name: String,
    val location: String? = null,
    val latitude: BigDecimal? = null,
    val longitude: BigDecimal? = null,
    val altitude: BigDecimal? = null,
)
