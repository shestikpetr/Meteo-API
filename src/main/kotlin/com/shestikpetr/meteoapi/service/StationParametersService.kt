package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.dto.sensor.ParameterMetadata
import com.shestikpetr.meteoapi.dto.station.StationParametersResponse
import com.shestikpetr.meteoapi.entity.Parameter
import com.shestikpetr.meteoapi.repository.ParameterRepository
import com.shestikpetr.meteoapi.repository.StationParameterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StationParametersService(
    private val stationParameterRepository: StationParameterRepository,
    private val parameterRepository: ParameterRepository,
    private val accessControlService: AccessControlService,
) {

    fun listForUser(
        userId: Int,
        stationNumber: String,
    ): StationParametersResponse {
        accessControlService.requireAccess(userId, stationNumber)
        val metadata = loadActiveMetadata(stationNumber)
        return StationParametersResponse(stationNumber = stationNumber, parameters = metadata)
    }

    private fun loadActiveMetadata(stationNumber: String): List<ParameterMetadata> {
        val activeCodes = findActiveParameterCodes(stationNumber)
        return parameterRepository.findAllByCodeIn(activeCodes).map(::toMetadata)
    }

    private fun findActiveParameterCodes(stationNumber: String): List<String> = stationParameterRepository
        .findByStationStationNumberAndIsActiveTrue(stationNumber)
        .mapNotNull { it.parameterCode }

    private fun toMetadata(parameter: Parameter): ParameterMetadata = ParameterMetadata(
        code = parameter.code!!,
        name = parameter.name!!,
        unit = parameter.unit,
        category = parameter.category,
    )
}
