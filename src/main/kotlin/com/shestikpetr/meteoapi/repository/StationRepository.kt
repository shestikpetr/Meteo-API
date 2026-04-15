package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.Station
import org.springframework.data.jpa.repository.JpaRepository

interface StationRepository : JpaRepository<Station, Int> {

    fun findByStationNumber(stationNumber: String): Station?

    fun existsByStationNumber(stationNumber: String): Boolean
}
