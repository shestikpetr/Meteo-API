package com.shestikpetr.meteoapi.service.mapper

import com.shestikpetr.meteoapi.dto.sensor.ParameterWithValue
import com.shestikpetr.meteoapi.dto.sensor.StationDataResponse
import com.shestikpetr.meteoapi.entity.Parameter
import com.shestikpetr.meteoapi.entity.UserStation

object StationDataMapper {

    fun toResponse(
        link: UserStation,
        time: Long?,
        values: List<ParameterWithValue>,
    ): StationDataResponse {
        val station = link.station ?: error("UserStation без station")
        return StationDataResponse(
            stationNumber = station.stationNumber ?: error("Станция без stationNumber"),
            name = link.customName?.takeIf { it.isNotBlank() } ?: (station.name ?: error("Станция без name")),
            location = station.location,
            latitude = station.latitude,
            longitude = station.longitude,
            time = time,
            parameters = values,
        )
    }

    fun toParameterWithValue(
        code: Int,
        metadata: Parameter?,
        value: Double?,
    ): ParameterWithValue = ParameterWithValue(
        code = code,
        name = metadata?.name ?: code.toString(),
        value = value,
        unit = metadata?.unit,
        description = metadata?.description,
    )
}
