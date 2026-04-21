package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.config.SensorQueryProperties
import com.shestikpetr.meteoapi.dto.sensor.TimeSeriesPoint
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class SensorRepository(
    @param:Qualifier("sensorJdbcClient") private val sensorJdbcClient: JdbcClient,
    private val queryProperties: SensorQueryProperties,
) {

    fun findLatestPoint(
        stationNumber: String,
        parameterCode: Int,
    ): TimeSeriesPoint? {
        val table = safeIdentifier(stationNumber)
        val column = safeColumnForCode(parameterCode)
        return sensorJdbcClient
            .sql(latestPointSql(table, column))
            .query(POINT_ROW_MAPPER)
            .optional()
            .orElse(null)
    }

    fun findTimeSeries(
        stationNumber: String,
        parameterCode: Int,
        startTime: Long?,
        endTime: Long?,
    ): List<TimeSeriesPoint> {
        val table = safeIdentifier(stationNumber)
        val column = safeColumnForCode(parameterCode)
        val spec = sensorJdbcClient.sql(timeSeriesSql(table, column, startTime, endTime))
        bindTimeRange(spec, startTime, endTime)
        return spec.query(POINT_ROW_MAPPER).list()
    }

    private fun latestPointSql(
        table: String,
        column: String,
    ): String = """
        SELECT time, `$column` AS value
        FROM `$table`
        WHERE `$column` > ${queryProperties.invalidValueThreshold}
        ORDER BY time DESC
        LIMIT 1
    """.trimIndent()

    private fun timeSeriesSql(
        table: String,
        column: String,
        startTime: Long?,
        endTime: Long?,
    ): String {
        val base = "SELECT time, `$column` AS value FROM `$table` " +
            "WHERE `$column` > ${queryProperties.invalidValueThreshold}"
        return base + timeRangeClause(startTime, endTime) + " ORDER BY time ASC"
    }

    private fun timeRangeClause(
        startTime: Long?,
        endTime: Long?,
    ): String {
        val filters = buildList {
            if (startTime != null) add("time >= :startTime")
            if (endTime != null) add("time <= :endTime")
        }
        return if (filters.isEmpty()) "" else " AND " + filters.joinToString(" AND ")
    }

    private fun bindTimeRange(
        spec: JdbcClient.StatementSpec,
        startTime: Long?,
        endTime: Long?,
    ) {
        if (startTime != null) spec.param("startTime", startTime)
        if (endTime != null) spec.param("endTime", endTime)
    }

    private fun safeIdentifier(raw: String): String {
        require(raw.matches(SAFE_IDENTIFIER)) { "Недопустимый идентификатор: '$raw'" }
        return raw
    }

    // Числовой код параметра используется как имя колонки в sensor-БД (в обратных кавычках).
    private fun safeColumnForCode(code: Int): String {
        require(code >= 0) { "Код параметра должен быть неотрицательным: $code" }
        return code.toString()
    }

    private companion object {
        val SAFE_IDENTIFIER = Regex("^[A-Za-z0-9_]{1,64}$")

        val POINT_ROW_MAPPER: RowMapper<TimeSeriesPoint> =
            RowMapper { rs, _ -> TimeSeriesPoint(time = rs.getLong("time"), value = rs.getDouble("value")) }
    }
}
