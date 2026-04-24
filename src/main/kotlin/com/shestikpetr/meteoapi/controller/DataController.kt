package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.config.ApiRoutes
import com.shestikpetr.meteoapi.config.OpenApiConfig
import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.dto.sensor.ParameterHistoryResponse
import com.shestikpetr.meteoapi.dto.sensor.StationDataResponse
import com.shestikpetr.meteoapi.security.UserPrincipal
import com.shestikpetr.meteoapi.service.SensorDataService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = OpenApiConfig.TAG_DATA)
@RestController
@RequestMapping(ApiRoutes.STATIONS)
class DataController(
    private val sensorDataService: SensorDataService,
) {

    @Operation(
        summary = "Последние показания станции",
        description = "Возвращает свежайшие значения всех параметров станции вместе с меткой времени измерения.",
    )
    @GetMapping("/{stationNumber}/data")
    fun latest(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Parameter(description = "Номер станции") @PathVariable stationNumber: String,
    ): ApiResponse<StationDataResponse> = ApiResponse.ok(sensorDataService.latestForStation(principal.userId, stationNumber))

    @Operation(
        summary = "История значений параметра",
        description = "Возвращает временной ряд значений одного параметра. " +
            "Границы задаются epoch-секундами: без параметров отдаются последние сутки.",
    )
    @GetMapping("/{stationNumber}/parameters/{parameterCode}/history")
    fun history(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Parameter(description = "Номер станции") @PathVariable stationNumber: String,
        @Parameter(description = "Код параметра)") @PathVariable parameterCode: Int,
        @Parameter(description = "Начало интервала, epoch-seconds") @RequestParam(required = false) startTime: Long?,
        @Parameter(description = "Конец интервала, epoch-seconds") @RequestParam(required = false) endTime: Long?,
    ): ApiResponse<ParameterHistoryResponse> = ApiResponse.ok(
        sensorDataService.history(principal.userId, stationNumber, parameterCode, startTime, endTime),
    )
}
