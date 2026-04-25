package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.StationParameter
import com.shestikpetr.meteoapi.repository.projection.LinkedStationProjection
import com.shestikpetr.meteoapi.repository.projection.ParameterUsageProjection
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

    @Query(
        """
        SELECT sp.parameter.id AS parameterId, COUNT(sp) AS stationCount
        FROM StationParameter sp
        GROUP BY sp.parameter.id
        """,
    )
    fun countStationsGroupedByParameter(): List<ParameterUsageProjection>

    @Query(
        """
        SELECT s.stationNumber AS stationNumber, s.name AS name
        FROM Station s
        JOIN StationParameter sp ON sp.station = s
        WHERE sp.parameter.id = :parameterId
        ORDER BY s.stationNumber
        """,
    )
    fun findStationsLinkedToParameter(parameterId: Int): List<LinkedStationProjection>
}
