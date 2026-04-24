package com.shestikpetr.meteoapi.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig(
    @param:Value("\${app.public-base-url:}") private val publicBaseUrl: String,
) {

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(apiInfo())
        .tags(orderedTags())
        .also(::applyPublicServer)
        .addSecurityItem(SecurityRequirement().addList(BEARER_SCHEME))
        .components(Components().addSecuritySchemes(BEARER_SCHEME, bearerJwtScheme()))

    // Если задан публичный base-URL (за nginx с TLS) - фиксируем его в servers[]
    private fun applyPublicServer(api: OpenAPI) {
        if (publicBaseUrl.isBlank()) return
        api.addServersItem(Server().url(publicBaseUrl))
    }

    private fun apiInfo(): Info = Info()
        .title("MeteoAPI")
        .version("0.0.1")
        .description("API для доступа к показаниям метеостанций")

    // Явный порядок тегов в Swagger UI
    private fun orderedTags(): List<Tag> = listOf(
        tag(TAG_AUTH, "Регистрация, вход и обновление JWT-токенов"),
        tag(TAG_STATIONS, "Привязка станций к пользователю и их параметры"),
        tag(TAG_DATA, "Показания датчиков: последние значения и история"),
        tag(TAG_HEALTH, "Служебная проверка доступности сервиса"),
    )

    private fun tag(name: String, description: String): Tag = Tag().name(name).description(description)

    private fun bearerJwtScheme(): SecurityScheme = SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")

    companion object {
        const val BEARER_SCHEME = "bearerAuth"
        const val TAG_AUTH = "Auth"
        const val TAG_STATIONS = "Stations"
        const val TAG_DATA = "Data"
        const val TAG_HEALTH = "Health"
    }
}
