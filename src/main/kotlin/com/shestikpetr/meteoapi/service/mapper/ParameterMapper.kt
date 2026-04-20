package com.shestikpetr.meteoapi.service.mapper

import com.shestikpetr.meteoapi.dto.sensor.ParameterMetadata
import com.shestikpetr.meteoapi.entity.Parameter

object ParameterMapper {

    fun toMetadata(parameter: Parameter): ParameterMetadata = ParameterMetadata(
        code = parameter.code ?: error("Parameter без code"),
        name = parameter.name ?: error("Parameter без name"),
        unit = parameter.unit,
        category = parameter.category,
    )
}
