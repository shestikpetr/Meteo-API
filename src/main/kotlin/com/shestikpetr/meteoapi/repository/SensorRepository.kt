package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.dto.sensor.TimeSeriesPoint
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class SensorRepository(
    @param:Qualifier("sensorJdbcClient") private val sensorJdbcClient: JdbcClient,
) {

    fun findLatestPoint(
        stationNumber: String,
        parameterCode: String,
    ): TimeSeriesPoint? {
        val table = safeIdentifier(stationNumber)
        val column = safeIdentifier(parameterCode)
        if (!tableAndColumnExist(table, column)) return null
        return queryLatestPoint(table, column)
    }

    fun findTimeSeries(
        stationNumber: String,
        parameterCode: String,
        startTime: Long?,
        endTime: Long?,
    ): List<TimeSeriesPoint> {
        val table = safeIdentifier(stationNumber)
        val column = safeIdentifier(parameterCode)
        if (!tableAndColumnExist(table, column)) return emptyList()
        return queryTimeSeries(table, column, startTime, endTime)
    }

    private fun queryLatestPoint(
        table: String,
        column: String,
    ): TimeSeriesPoint? = sensorJdbcClient
        .sql(latestPointSql(table, column))
        .query(POINT_ROW_MAPPER)
        .optional()
        .orElse(null)

    private fun queryTimeSeries(
        table: String,
        column: String,
        startTime: Long?,
        endTime: Long?,
    ): List<TimeSeriesPoint> {
        val spec = sensorJdbcClient.sql(timeSeriesSql(table, column, startTime, endTime))
        bindTimeRange(spec, startTime, endTime)
        return spec.query(POINT_ROW_MAPPER).list()
    }

    private fun tableAndColumnExist(
        table: String,
        column: String,
    ): Boolean = tableExists(table) && columnExists(table, column)

    private fun tableExists(table: String): Boolean = sensorJdbcClient
        .sql("SHOW TABLES LIKE ?")
        .param(table)
        .query(String::class.java)
        .optional()
        .isPresent

    private fun columnExists(
        table: String,
        column: String,
    ): Boolean = sensorJdbcClient
        .sql("SHOW COLUMNS FROM `$table` LIKE ?")
        .param(column)
        .query { _, _ -> true }
        .optional()
        .isPresent

    private fun latestPointSql(
        table: String,
        column: String,
    ): String = """
        SELECT time, `$column` AS value
        FROM `$table`
        WHERE `$column` > $VALUE_THRESHOLD
        ORDER BY time DESC
        LIMIT 1
    """.trimIndent()

    private fun timeSeriesSql(
        table: String,
        column: String,
        startTime: Long?,
        endTime: Long?,
    ): String {
        val base = "SELECT time, `$column` AS value FROM `$table` WHERE `$column` > $VALUE_THRESHOLD"
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

    private companion object {
        const val VALUE_THRESHOLD = -100

        val SAFE_IDENTIFIER = Regex("^[A-Za-z0-9_]{1,64}$")

        val POINT_ROW_MAPPER: RowMapper<TimeSeriesPoint> =
            RowMapper { rs, _ -> TimeSeriesPoint(time = rs.getLong("time"), value = rs.getDouble("value")) }
    }
}
