package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.StationParameter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface StationParameterRepository : JpaRepository<StationParameter, Int> {

    @Query(
        """
        SELECT sp.parameter.code
        FROM StationParameter sp
        WHERE sp.station.stationNumber = :stationNumber
          AND sp.isActive = true
        """,
    )
    fun findActiveParameterCodesByStationNumber(stationNumber: String): List<Int>
}
