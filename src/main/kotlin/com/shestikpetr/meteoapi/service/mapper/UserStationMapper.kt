package com.shestikpetr.meteoapi.service.mapper

import com.shestikpetr.meteoapi.dto.station.UserStationResponse
import com.shestikpetr.meteoapi.entity.UserStation

object UserStationMapper {

    fun toResponse(link: UserStation): UserStationResponse {
        val station = link.station ?: error("UserStation без station")
        return UserStationResponse(
            stationNumber = station.stationNumber ?: error("Станция без stationNumber"),
            name = link.customName?.takeIf { it.isNotBlank() } ?: (station.name ?: error("Станция без name")),
            location = station.location,
            latitude = station.latitude,
            longitude = station.longitude,
            altitude = station.altitude,
        )
    }
}
