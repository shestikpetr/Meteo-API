package com.shestikpetr.meteoapi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.sensor-datasource")
data class SensorDataSourceProperties(
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String,
    val hikari: Hikari,
) {

    data class Hikari(
        val minimumIdle: Int,
        val maximumPoolSize: Int,
        val poolName: String,
        val connectionTimeoutMs: Long,
    )
}
