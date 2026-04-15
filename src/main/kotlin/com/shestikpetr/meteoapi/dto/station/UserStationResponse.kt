package com.shestikpetr.meteoapi.dto.station

import java.time.Instant

data class UserStationResponse(
    val id: Int,
    val userId: Int,
    val stationId: Int,
    val customName: String? = null,
    val isFavorite: Boolean = false,
    val createdAt: Instant? = null,
    val station: StationResponse? = null,
)
