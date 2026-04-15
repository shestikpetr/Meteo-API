package com.shestikpetr.meteoapi.dto.station

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UserStationRequest(
    @field:NotNull(message = "ID станции обязателен")
    val stationId: Int,

    @field:Size(max = 100, message = "Пользовательское имя: максимум 100 символов")
    val customName: String? = null,

    val isFavorite: Boolean = false,
)
