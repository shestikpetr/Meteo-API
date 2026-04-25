package com.shestikpetr.meteoapi.repository.projection

// Станция, привязанная к параметру
interface LinkedStationProjection {
    val stationNumber: String
    val name: String?
}
