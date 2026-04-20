package com.shestikpetr.meteoapi.service.mapper

import com.shestikpetr.meteoapi.dto.station.StationResponse
import com.shestikpetr.meteoapi.entity.Station

object StationMapper {

    fun toResponse(station: Station): StationResponse = StationResponse(
        id = station.requireId(),
        stationNumber = station.requireStationNumber(),
        name = station.requireName(),
        location = station.location,
        latitude = station.latitude,
        longitude = station.longitude,
        altitude = station.altitude,
        isActive = station.isActive,
        createdAt = station.createdAt,
        updatedAt = station.updatedAt,
    )

    private fun Station.requireId(): Int = id ?: error("Станция без id")

    private fun Station.requireStationNumber(): String = stationNumber ?: error("Станция без stationNumber")

    private fun Station.requireName(): String = name ?: error("Станция без name")
}
