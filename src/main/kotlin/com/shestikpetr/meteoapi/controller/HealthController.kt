package com.shestikpetr.meteoapi.controller

import com.shestikpetr.meteoapi.config.OpenApiConfig
import com.shestikpetr.meteoapi.dto.common.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = OpenApiConfig.TAG_HEALTH)
@SecurityRequirements
@RestController
class HealthController {

    @GetMapping("/health")
    fun health(): ApiResponse<Map<String, String>> = ApiResponse.ok(mapOf("status" to "ok"))
}
