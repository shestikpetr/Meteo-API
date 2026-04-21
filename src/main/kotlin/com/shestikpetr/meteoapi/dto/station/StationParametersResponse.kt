package com.shestikpetr.meteoapi.dto.station

import com.shestikpetr.meteoapi.dto.sensor.ParameterMetadata

data class StationParametersResponse(
    val stationNumber: String,
    val parameters: List<ParameterMetadata>,
)
