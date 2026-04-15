package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.UserStation
import org.springframework.data.jpa.repository.JpaRepository

interface UserStationRepository : JpaRepository<UserStation, Int> {

    fun findByUserId(userId: Int): List<UserStation>

    fun findByUserIdAndStationId(userId: Int, stationId: Int): UserStation?

    fun findByUserIdAndStationStationNumber(userId: Int, stationNumber: String): UserStation?

    fun existsByUserIdAndStationId(userId: Int, stationId: Int): Boolean

    fun deleteByUserIdAndStationId(userId: Int, stationId: Int)
}
