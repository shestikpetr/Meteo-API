package com.shestikpetr.meteoapi.repository.projection

// Сколько станций ссылается на параметр
interface ParameterUsageProjection {
    val parameterId: Int
    val stationCount: Long
}
