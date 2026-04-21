package com.shestikpetr.meteoapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(apiInfo())
        .addSecurityItem(SecurityRequirement().addList(BEARER_SCHEME))
        .components(Components().addSecuritySchemes(BEARER_SCHEME, bearerJwtScheme()))

    private fun apiInfo(): Info = Info()
        .title("MeteoAPI")
        .version("0.0.1")
        .description("API для доступа к показаниям метеостанций")

    private fun bearerJwtScheme(): SecurityScheme = SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")

    private companion object {
        const val BEARER_SCHEME = "bearerAuth"
    }
}
