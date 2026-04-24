package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.dto.sensor.ParameterHistoryResponse
import com.shestikpetr.meteoapi.dto.sensor.ParameterWithValue
import com.shestikpetr.meteoapi.dto.sensor.StationDataResponse
import com.shestikpetr.meteoapi.entity.Parameter
import com.shestikpetr.meteoapi.entity.Station
import com.shestikpetr.meteoapi.entity.UserStation
import com.shestikpetr.meteoapi.exception.NotFoundException
import com.shestikpetr.meteoapi.exception.ValidationException
import com.shestikpetr.meteoapi.repository.ParameterRepository
import com.shestikpetr.meteoapi.repository.SensorRepository
import com.shestikpetr.meteoapi.repository.StationParameterRepository
import com.shestikpetr.meteoapi.service.mapper.ParameterMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SensorDataService(
    private val accessControlService: AccessControlService,
    private val stationParameterRepository: StationParameterRepository,
    private val parameterRepository: ParameterRepository,
    private val sensorRepository: SensorRepository,
) {

    fun latestForStation(
        userId: Int,
        stationNumber: String,
    ): StationDataResponse {
        val link = accessControlService.requireAccess(userId, stationNumber)
        val station = link.station ?: error("UserStation без station")
        val activeCodes = activeParameterCodes(stationNumber)
        val metadata = parameterRepository.findAllByCodeIn(activeCodes).associateBy { it.requireCode() }
        val values = activeCodes.map { code -> buildParameterWithValue(stationNumber, code, metadata[code]) }
        return buildStationDataResponse(link, station, values)
    }

    fun history(
        userId: Int,
        stationNumber: String,
        parameterCode: Int,
        startTime: Long?,
        endTime: Long?,
    ): ParameterHistoryResponse {
        accessControlService.requireAccess(userId, stationNumber)
        requireActiveOnStation(stationNumber, parameterCode)
        requireValidTimeRange(startTime, endTime)
        val parameter = parameterRepository.findByCode(parameterCode)
            ?: throw NotFoundException("Параметр $parameterCode не найден")
        val series = sensorRepository.findTimeSeries(stationNumber, parameterCode, startTime, endTime)
        return ParameterHistoryResponse(
            stationNumber = stationNumber,
            parameter = ParameterMapper.toMetadata(parameter),
            data = series,
        )
    }

    private fun buildParameterWithValue(
        stationNumber: String,
        code: Int,
        metadata: Parameter?,
    ): ParameterWithValue {
        val point = sensorRepository.findLatestPoint(stationNumber, code)
        return ParameterWithValue(
            code = code,
            name = metadata?.name ?: code.toString(),
            value = point?.value,
            time = point?.time,
            unit = metadata?.unit,
            description = metadata?.description,
        )
    }

    private fun buildStationDataResponse(
        link: UserStation,
        station: Station,
        values: List<ParameterWithValue>,
    ): StationDataResponse = StationDataResponse(
        stationNumber = station.stationNumber ?: error("Станция без stationNumber"),
        customName = link.customName,
        location = station.location,
        latitude = station.latitude,
        longitude = station.longitude,
        parameters = values,
    )

    private fun activeParameterCodes(stationNumber: String): List<Int> = stationParameterRepository.findActiveParameterCodesByStationNumber(stationNumber)

    private fun requireActiveOnStation(
        stationNumber: String,
        parameterCode: Int,
    ) {
        if (parameterCode !in activeParameterCodes(stationNumber)) {
            throw NotFoundException("Параметр $parameterCode не активен на станции $stationNumber")
        }
    }

    private fun requireValidTimeRange(
        startTime: Long?,
        endTime: Long?,
    ) {
        if (startTime != null && endTime != null && startTime > endTime) {
            throw ValidationException("startTime должен быть ≤ endTime")
        }
    }

    private fun Parameter.requireCode(): Int = code ?: error("Parameter без code")
}
