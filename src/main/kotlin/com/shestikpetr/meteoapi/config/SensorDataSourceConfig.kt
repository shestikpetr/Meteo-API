package com.shestikpetr.meteoapi.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.simple.JdbcClient
import javax.sql.DataSource

@Configuration
class SensorDataSourceConfig {

    @Bean(name = ["sensorDataSource"])
    fun sensorDataSource(props: SensorDataSourceProperties): DataSource {
        val ds = buildHikariDataSource(props)
        applyPoolSettings(ds, props.hikari)

        return ds
    }

    @Bean(name = ["sensorJdbcClient"])
    fun sensorJdbcClient(
        @Qualifier("sensorDataSource") ds: DataSource,
    ): JdbcClient = JdbcClient.create(ds)

    private fun buildHikariDataSource(props: SensorDataSourceProperties): HikariDataSource = DataSourceBuilder
        .create()
        .type(HikariDataSource::class.java)
        .driverClassName(props.driverClassName)
        .url(props.url)
        .username(props.username)
        .password(props.password)
        .build()

    private fun applyPoolSettings(
        ds: HikariDataSource,
        settings: SensorDataSourceProperties.Hikari,
    ) {
        ds.minimumIdle = settings.minimumIdle
        ds.maximumPoolSize = settings.maximumPoolSize
        ds.poolName = settings.poolName
        ds.connectionTimeout = settings.connectionTimeoutMs
    }
}
