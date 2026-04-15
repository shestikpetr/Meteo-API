package com.shestikpetr.meteoapi.dto.station

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class StationCreateRequest(
    @field:NotBlank(message = "Номер станции обязателен")
    @field:Size(max = 20, message = "Номер станции: максимум 20 символов")
    val stationNumber: String,

    @field:NotBlank(message = "Название обязательно")
    @field:Size(max = 100, message = "Название: максимум 100 символов")
    val name: String,

    @field:Size(max = 200, message = "Локация: максимум 200 символов")
    val location: String? = null,

    @field:DecimalMin(value = "-90", message = "Широта: от -90 до 90")
    @field:DecimalMax(value = "90", message = "Широта: от -90 до 90")
    val latitude: BigDecimal? = null,

    @field:DecimalMin(value = "-180", message = "Долгота: от -180 до 180")
    @field:DecimalMax(value = "180", message = "Долгота: от -180 до 180")
    val longitude: BigDecimal? = null,

    val altitude: BigDecimal? = null,
)
