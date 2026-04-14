package com.shestikpetr.meteoapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MeteoApiApplication

fun main(args: Array<String>) {
    runApplication<MeteoApiApplication>(*args)
}
