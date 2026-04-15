package com.shestikpetr.meteoapi.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
class SensorDataSourceConfig {

    @Bean(name = ["sensorDataSource"])
    fun sensorDataSource(props: SensorDataSourceProperties): DataSource {
        val ds = buildHikariDataSource(props)
        applyPoolSettings(ds)

        return ds
    }

    @Bean(name = ["sensorJdbcTemplate"])
    fun sensorJdbcTemplate(
        @Qualifier("sensorDataSource") ds: DataSource,
    ): JdbcTemplate = JdbcTemplate(ds)

    private fun buildHikariDataSource(props: SensorDataSourceProperties): HikariDataSource =
        DataSourceBuilder
            .create()
            .type(HikariDataSource::class.java)
            .driverClassName(props.driverClassName)
            .url(props.url)
            .username(props.username)
            .password(props.password)
            .build() as HikariDataSource

    private fun applyPoolSettings(ds: HikariDataSource) {
        ds.minimumIdle = 2
        ds.maximumPoolSize = 10
        ds.poolName = "SensorDbHikariPool"
        ds.connectionTimeout = 30_000
    }
}
