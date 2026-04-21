package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.dto.auth.AuthLoginData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenRequest
import com.shestikpetr.meteoapi.dto.auth.UserLoginRequest
import com.shestikpetr.meteoapi.dto.auth.UserRegisterRequest
import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.service.AuthService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: UserRegisterRequest,
    ): ApiResponse<AuthLoginData> = ApiResponse.ok(authService.register(request))

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLoginRequest,
    ): ApiResponse<AuthLoginData> = ApiResponse.ok(authService.login(request))

    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ApiResponse<RefreshTokenData> = ApiResponse.ok(authService.refresh(request))
}
