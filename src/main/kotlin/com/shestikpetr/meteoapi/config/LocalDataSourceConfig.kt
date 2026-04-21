package com.shestikpetr.meteoapi.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class LocalDataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    fun localDataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun dataSource(localDataSourceProperties: DataSourceProperties): HikariDataSource = buildHikariDataSource(localDataSourceProperties)

    private fun buildHikariDataSource(props: DataSourceProperties): HikariDataSource = props
        .initializeDataSourceBuilder()
        .type(HikariDataSource::class.java)
        .build()
}
