package com.shestikpetr.meteoapi.dto.station

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserStationRequest(
    @field:NotBlank(message = "Номер станции обязателен")
    @field:Size(max = 20, message = "Номер станции: максимум 20 символов")
    val stationNumber: String,

    @field:Size(max = 100, message = "Пользовательское имя: максимум 100 символов")
    val customName: String? = null,

    val isFavorite: Boolean = false,
)
