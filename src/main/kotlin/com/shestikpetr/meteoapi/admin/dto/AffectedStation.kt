package com.shestikpetr.meteoapi.admin.dto

// Станция, которую затронет удаление параметра (показываем номер + человеческое имя)
data class AffectedStation(
    val stationNumber: String,
    val name: String?,
)
