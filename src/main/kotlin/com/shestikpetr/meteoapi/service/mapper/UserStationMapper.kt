package com.shestikpetr.meteoapi.service.mapper

import com.shestikpetr.meteoapi.dto.station.UserStationResponse
import com.shestikpetr.meteoapi.entity.UserStation

object UserStationMapper {

    fun toResponse(link: UserStation): UserStationResponse {
        val station = link.station ?: error("UserStation без station")
        val user = link.user ?: error("UserStation без user")
        return UserStationResponse(
            id = link.id ?: error("UserStation без id"),
            userId = user.id ?: error("User без id"),
            stationId = station.id ?: error("Станция без id"),
            customName = link.customName,
            isFavorite = link.isFavorite,
            createdAt = link.createdAt,
            station = StationMapper.toResponse(station),
        )
    }
}
