package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.dto.station.StationParametersResponse
import com.shestikpetr.meteoapi.dto.station.UserStationRequest
import com.shestikpetr.meteoapi.dto.station.UserStationResponse
import com.shestikpetr.meteoapi.dto.station.UserStationUpdateRequest
import com.shestikpetr.meteoapi.security.UserPrincipal
import com.shestikpetr.meteoapi.service.StationParametersService
import com.shestikpetr.meteoapi.service.UserStationService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stations")
class StationsController(
    private val userStationService: UserStationService,
    private val stationParametersService: StationParametersService,
) {

    @GetMapping
    fun list(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<List<UserStationResponse>> = ApiResponse.ok(userStationService.list(principal.userId))

    @PostMapping
    fun attach(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UserStationRequest,
    ): ApiResponse<UserStationResponse> = ApiResponse.ok(userStationService.attach(principal.userId, request))

    @PatchMapping("/{stationNumber}")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable stationNumber: String,
        @Valid @RequestBody request: UserStationUpdateRequest,
    ): ApiResponse<UserStationResponse> = ApiResponse.ok(userStationService.update(principal.userId, stationNumber, request))

    @DeleteMapping("/{stationNumber}")
    fun detach(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable stationNumber: String,
    ): ApiResponse<Unit> {
        userStationService.detach(principal.userId, stationNumber)
        return ApiResponse.empty()
    }

    @GetMapping("/{stationNumber}/parameters")
    fun parameters(
        @AuthenticationPrincipal principal: UserPrincipal,
        @PathVariable stationNumber: String,
    ): ApiResponse<StationParametersResponse> = ApiResponse.ok(stationParametersService.listForUser(principal.userId, stationNumber))
}
