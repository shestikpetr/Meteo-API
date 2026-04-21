package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.config.OpenApiConfig
import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.dto.station.StationParametersResponse
import com.shestikpetr.meteoapi.dto.station.UserStationRequest
import com.shestikpetr.meteoapi.dto.station.UserStationResponse
import com.shestikpetr.meteoapi.dto.station.UserStationUpdateRequest
import com.shestikpetr.meteoapi.security.UserPrincipal
import com.shestikpetr.meteoapi.service.StationParametersService
import com.shestikpetr.meteoapi.service.UserStationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
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

@Tag(name = OpenApiConfig.TAG_STATIONS)
@RestController
@RequestMapping("/stations")
class StationsController(
    private val userStationService: UserStationService,
    private val stationParametersService: StationParametersService,
) {

    @Operation(
        summary = "Список станций пользователя",
        description = "Возвращает станции, которые пользователь привязал к своему аккаунту, с кастомным именем и флагом «избранное».",
    )
    @GetMapping
    fun list(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<List<UserStationResponse>> = ApiResponse.ok(userStationService.list(principal.userId))

    @Operation(
        summary = "Привязать станцию",
        description = "Создаёт связь пользователь—станция по серийному номеру станции.",
    )
    @PostMapping
    fun attach(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UserStationRequest,
    ): ApiResponse<UserStationResponse> = ApiResponse.ok(userStationService.attach(principal.userId, request))

    @Operation(
        summary = "Обновить атрибуты привязки",
        description = "Меняет customName и/или isFavorite у существующей привязки.",
    )
    @PatchMapping("/{stationNumber}")
    fun update(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Parameter(description = "Серийный номер станции") @PathVariable stationNumber: String,
        @Valid @RequestBody request: UserStationUpdateRequest,
    ): ApiResponse<UserStationResponse> = ApiResponse.ok(userStationService.update(principal.userId, stationNumber, request))

    @Operation(
        summary = "Отвязать станцию",
        description = "Удаляет связь пользователь—станция. Сама станция и её параметры не трогаются.",
    )
    @DeleteMapping("/{stationNumber}")
    fun detach(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Parameter(description = "Серийный номер станции") @PathVariable stationNumber: String,
    ): ApiResponse<Unit> {
        userStationService.detach(principal.userId, stationNumber)
        return ApiResponse.empty()
    }

    @Operation(
        summary = "Параметры станции",
        description = "Возвращает метаданные параметров, настроенных для станции (код, название, единица измерения, категория).",
    )
    @GetMapping("/{stationNumber}/parameters")
    fun parameters(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Parameter(description = "Серийный номер станции") @PathVariable stationNumber: String,
    ): ApiResponse<StationParametersResponse> = ApiResponse.ok(stationParametersService.listForUser(principal.userId, stationNumber))
}
