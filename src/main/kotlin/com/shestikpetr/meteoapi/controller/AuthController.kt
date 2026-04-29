package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.config.ApiRoutes
import com.shestikpetr.meteoapi.config.OpenApiConfig
import com.shestikpetr.meteoapi.dto.auth.AuthLoginData
import com.shestikpetr.meteoapi.dto.auth.ChangePasswordRequest
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenData
import com.shestikpetr.meteoapi.dto.auth.RefreshTokenRequest
import com.shestikpetr.meteoapi.dto.auth.UpdateMeRequest
import com.shestikpetr.meteoapi.dto.auth.UserLoginRequest
import com.shestikpetr.meteoapi.dto.auth.UserRegisterRequest
import com.shestikpetr.meteoapi.dto.auth.UserResponse
import com.shestikpetr.meteoapi.dto.common.ApiResponse
import com.shestikpetr.meteoapi.security.UserPrincipal
import com.shestikpetr.meteoapi.service.AuthService
import com.shestikpetr.meteoapi.service.UserAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = OpenApiConfig.TAG_AUTH)
@RestController
@RequestMapping(ApiRoutes.AUTH)
class AuthController(
    private val authService: AuthService,
    private val userAccountService: UserAccountService,
) {

    @Operation(
        summary = "Регистрация пользователя",
        description = "Создаёт нового пользователя по username/password и сразу возвращает пару access + refresh токенов.",
    )
    @SecurityRequirements
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: UserRegisterRequest,
    ): ApiResponse<AuthLoginData> = ApiResponse.ok(authService.register(request))

    @Operation(
        summary = "Вход по логину и паролю",
        description = "Проверяет username/password и выдаёт пару JWT-токенов.",
    )
    @SecurityRequirements
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: UserLoginRequest,
    ): ApiResponse<AuthLoginData> = ApiResponse.ok(authService.login(request))

    @Operation(
        summary = "Обновление access-токена",
        description = "Принимает refresh-токен и возвращает новый access (refresh сохраняется).",
    )
    @SecurityRequirements
    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody request: RefreshTokenRequest,
    ): ApiResponse<RefreshTokenData> = ApiResponse.ok(authService.refresh(request))

    @Operation(
        summary = "Текущий пользователь",
        description = "Возвращает данные пользователя, чей JWT прислан в заголовке Authorization.",
    )
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @GetMapping("/me")
    fun me(
        @AuthenticationPrincipal principal: UserPrincipal,
    ): ApiResponse<UserResponse> = ApiResponse.ok(userAccountService.me(principal.userId))

    @Operation(
        summary = "Изменение профиля",
        description = "Меняет username и/или email текущего пользователя. Хотя бы одно поле должно быть указано.",
    )
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @PatchMapping("/me")
    fun updateMe(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: UpdateMeRequest,
    ): ApiResponse<UserResponse> = ApiResponse.ok(userAccountService.updateMe(principal.userId, request))

    @Operation(
        summary = "Смена пароля",
        description = "Меняет пароль при знании текущего. " +
            "Старые JWT-токены остаются валидными до истечения (stateless): после смены пароля рекомендуется заново залогиниться.",
    )
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @PostMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal principal: UserPrincipal,
        @Valid @RequestBody request: ChangePasswordRequest,
    ): ApiResponse<Unit> {
        userAccountService.changePassword(principal.userId, request)
        return ApiResponse.ok(Unit)
    }

    @Operation(
        summary = "Выход",
        description = "Клиентский logout: сервер ничего не делает (JWT stateless). " +
            "Клиент должен сам удалить access/refresh-токены из хранилища.",
    )
    @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    @PostMapping("/logout")
    fun logout(): ApiResponse<Unit> = ApiResponse.ok(Unit)
}
