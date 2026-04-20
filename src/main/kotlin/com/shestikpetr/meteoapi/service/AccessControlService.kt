package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.entity.UserStation
import com.shestikpetr.meteoapi.exception.NotFoundException
import com.shestikpetr.meteoapi.repository.UserStationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AccessControlService(
    private val userStationRepository: UserStationRepository,
) {

    fun findAccess(
        userId: Int,
        stationNumber: String,
    ): UserStation? = userStationRepository.findByUserIdAndStationStationNumber(userId, stationNumber)

    fun requireAccess(
        userId: Int,
        stationNumber: String,
    ): UserStation = findAccess(userId, stationNumber)
        ?: throw NotFoundException("Станция $stationNumber не найдена или нет доступа")
}
