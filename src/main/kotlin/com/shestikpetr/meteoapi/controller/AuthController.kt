package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.config.OpenApiConfig
import com.shestikpetr.meteoapi.dto.auth.AuthLoginData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenRequest
import com.shestikpetr.meteoapi.dto.auth.UserLoginRequest
import com.shestikpetr.meteoapi.dto.auth.UserRegisterRequest
import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = OpenApiConfig.TAG_AUTH)
@SecurityRequirements
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {

    @Operation(
        summary = "Регистрация пользователя",
        description = "Создаёт нового пользователя по username/password и сразу возвращает пару access + refresh токенов.",
    )
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: UserRegisterRequest,
    ): ApiResponse<AuthLoginData> = ApiResponse.ok(authService.register(request))

    @Operation(
        summary = "Вход по логину и паролю",
        description = "Проверяет username/password и выдаёт пару JWT-токенов.",
    )
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLoginRequest,
    ): ApiResponse<AuthLoginData> = ApiResponse.ok(authService.login(request))

    @Operation(
        summary = "Обновление access-токена",
        description = "Принимает refresh-токен и возвращает новый access (refresh сохраняется).",
    )
    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ApiResponse<RefreshTokenData> = ApiResponse.ok(authService.refresh(request))
}
