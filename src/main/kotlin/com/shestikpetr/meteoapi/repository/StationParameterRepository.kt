package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.StationParameter
import org.springframework.data.jpa.repository.JpaRepository

interface StationParameterRepository : JpaRepository<StationParameter, Int> {

    fun findByStationIdAndIsActiveTrue(stationId: Int): List<StationParameter>

    fun findByStationStationNumberAndIsActiveTrue(stationNumber: String): List<StationParameter>
}
