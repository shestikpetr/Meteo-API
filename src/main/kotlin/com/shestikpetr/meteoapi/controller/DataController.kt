package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.dto.sensor.ParameterHistoryResponse
import com.shestikpetr.meteoapi.dto.sensor.StationDataResponse
import com.shestikpetr.meteoapi.security.UserPrincipal
import com.shestikpetr.meteoapi.service.SensorDataService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stations")
class DataController(
    private val sensorDataService: SensorDataService,
) {

    @GetMapping("/{stationNumber}/data")
    fun latest(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable stationNumber: String,
    ): ApiResponse<StationDataResponse> = ApiResponse.ok(sensorDataService.latestForStation(principal.userId, stationNumber))

    @GetMapping("/{stationNumber}/parameters/{parameterCode}/history")
    fun history(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable stationNumber: String,
        @PathVariable parameterCode: String,
        @RequestParam(required = false) startTime: Long?,
        @RequestParam(required = false) endTime: Long?,
    ): ApiResponse<ParameterHistoryResponse> = ApiResponse.ok(
        sensorDataService.history(principal.userId, stationNumber, parameterCode, startTime, endTime),
    )
}
