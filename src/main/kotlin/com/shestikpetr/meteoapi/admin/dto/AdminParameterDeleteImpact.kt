package com.shestikpetr.meteoapi.admin.dto

// Превью последствий удаления параметра для шаблона подтверждения
data class AdminParameterDeleteImpact(
    val parameter: AdminParameterRow,
    val affectedStations: List<AffectedStation>,
)
