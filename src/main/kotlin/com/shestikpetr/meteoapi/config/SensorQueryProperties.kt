package com.shestikpetr.meteoapi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.sensor-query")
data class SensorQueryProperties(
    val invalidValueThreshold: Int,
)
