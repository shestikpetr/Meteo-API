package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.dto.station.StationResponse
import com.shestikpetr.meteoapi.dto.station.UserStationRequest
import com.shestikpetr.meteoapi.dto.station.UserStationResponse
import com.shestikpetr.meteoapi.dto.station.UserStationUpdateRequest
import com.shestikpetr.meteoapi.entity.Station
import com.shestikpetr.meteoapi.entity.User
import com.shestikpetr.meteoapi.entity.UserStation
import com.shestikpetr.meteoapi.exception.ConflictException
import com.shestikpetr.meteoapi.exception.NotFoundException
import com.shestikpetr.meteoapi.repository.StationRepository
import com.shestikpetr.meteoapi.repository.UserRepository
import com.shestikpetr.meteoapi.repository.UserStationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserStationService(
    private val userRepository: UserRepository,
    private val stationRepository: StationRepository,
    private val userStationRepository: UserStationRepository,
    private val accessControlService: AccessControlService,
) {

    fun attach(
        userId: Int,
        request: UserStationRequest,
    ): UserStationResponse {
        val station = loadStationByNumber(request.stationNumber)
        requireNotAlreadyAttached(userId, station.id!!)
        val user = loadUserById(userId)
        val link = userStationRepository.save(newLink(user, station, request))
        return toUserStationResponse(link)
    }

    fun detach(
        userId: Int,
        stationNumber: String,
    ) {
        val link = accessControlService.requireAccess(userId, stationNumber)
        userStationRepository.delete(link)
    }

    fun update(
        userId: Int,
        stationNumber: String,
        request: UserStationUpdateRequest,
    ): UserStationResponse {
        val link = accessControlService.requireAccess(userId, stationNumber)
        applyUpdates(link, request)
        return toUserStationResponse(userStationRepository.save(link))
    }

    @Transactional(readOnly = true)
    fun list(userId: Int): List<UserStationResponse> = userStationRepository
        .findByUserId(userId)
        .map(::toUserStationResponse)

    private fun requireNotAlreadyAttached(
        userId: Int,
        stationId: Int,
    ) {
        if (userStationRepository.existsByUserIdAndStationId(userId, stationId)) {
            throw ConflictException("Станция уже добавлена пользователю")
        }
    }

    private fun loadStationByNumber(stationNumber: String): Station = stationRepository
        .findByStationNumber(stationNumber)
        ?: throw NotFoundException("Станция $stationNumber не найдена")

    private fun loadUserById(userId: Int): User = userRepository
        .findById(userId)
        .orElseThrow { NotFoundException("Пользователь $userId не найден") }

    private fun newLink(
        user: User,
        station: Station,
        request: UserStationRequest,
    ): UserStation = UserStation().apply {
        this.user = user
        this.station = station
        this.customName = request.customName
        this.isFavorite = request.isFavorite
    }

    private fun applyUpdates(
        link: UserStation,
        request: UserStationUpdateRequest,
    ) {
        if (request.customName != null) link.customName = request.customName
        if (request.isFavorite != null) link.isFavorite = request.isFavorite
    }

    private fun toUserStationResponse(link: UserStation): UserStationResponse {
        val station = link.station!!
        return UserStationResponse(
            id = link.id!!,
            userId = link.user!!.id!!,
            stationId = station.id!!,
            customName = link.customName,
            isFavorite = link.isFavorite,
            createdAt = link.createdAt,
            station = toStationResponse(station),
        )
    }

    private fun toStationResponse(station: Station): StationResponse = StationResponse(
        id = station.id!!,
        stationNumber = station.stationNumber!!,
        name = station.name!!,
        location = station.location,
        latitude = station.latitude,
        longitude = station.longitude,
        altitude = station.altitude,
        isActive = station.isActive,
        createdAt = station.createdAt,
        updatedAt = station.updatedAt,
    )
}
