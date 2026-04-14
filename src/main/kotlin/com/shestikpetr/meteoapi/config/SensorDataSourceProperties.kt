package com.shestikpetr.meteoapi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.sensor-datasource")
data class SensorDataSourceProperties(
    val url: String,
    val username: String,
    val password: String,
    val driverClassName: String,
)
