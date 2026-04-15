package com.shestikpetr.meteoapi.dto.station

import jakarta.validation.constraints.Size

data class UserStationUpdateRequest(
    @field:Size(max = 100, message = "Пользовательское имя: максимум 100 символов")
    val customName: String? = null,

    val isFavorite: Boolean? = null,
)
