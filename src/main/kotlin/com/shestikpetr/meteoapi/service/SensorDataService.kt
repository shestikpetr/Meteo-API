package com.shestikpetr.meteoapi.service

import com.shestikpetr.meteoapi.dto.sensor.ParameterHistoryResponse
import com.shestikpetr.meteoapi.dto.sensor.ParameterWithValue
import com.shestikpetr.meteoapi.dto.sensor.StationDataResponse
import com.shestikpetr.meteoapi.dto.sensor.TimeSeriesPoint
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
        val latestRow = if (activeCodes.isEmpty()) null else sensorRepository.findLatestRow(stationNumber, activeCodes)
        val values = activeCodes.map { code -> buildParameterWithValue(code, metadata[code], latestRow?.values?.get(code)) }
        return buildStationDataResponse(link, station, latestRow?.time, values)
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
        val series = loadSeries(stationNumber, parameterCode, startTime, endTime)
        return ParameterHistoryResponse(
            parameter = ParameterMapper.toMetadata(parameter),
            data = series,
        )
    }

    // Без startTime/endTime отдаём одну последнюю точку
    private fun loadSeries(
        stationNumber: String,
        parameterCode: Int,
        startTime: Long?,
        endTime: Long?,
    ): List<TimeSeriesPoint> {
        if (startTime == null && endTime == null) {
            val point = sensorRepository.findLatestPoint(stationNumber, parameterCode)
            return listOfNotNull(point)
        }
        return sensorRepository.findTimeSeries(stationNumber, parameterCode, startTime, endTime)
    }

    private fun buildParameterWithValue(
        code: Int,
        metadata: Parameter?,
        value: Double?,
    ): ParameterWithValue = ParameterWithValue(
        code = code,
        name = metadata?.name ?: code.toString(),
        value = value,
        unit = metadata?.unit,
        description = metadata?.description,
    )

    private fun buildStationDataResponse(
        link: UserStation,
        station: Station,
        time: Long?,
        values: List<ParameterWithValue>,
    ): StationDataResponse = StationDataResponse(
        stationNumber = station.stationNumber ?: error("Станция без stationNumber"),
        name = link.customName?.takeIf { it.isNotBlank() } ?: (station.name ?: error("Станция без name")),
        location = station.location,
        latitude = station.latitude,
        longitude = station.longitude,
        time = time,
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
