package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.config.SensorQueryProperties
import com.shestikpetr.meteoapi.dto.sensor.LatestRow
import com.shestikpetr.meteoapi.dto.sensor.TimeSeriesPoint
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.jdbc.BadSqlGrammarException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class SensorRepository(
    @param:Qualifier("sensorJdbcClient") private val sensorJdbcClient: JdbcClient,
    private val queryProperties: SensorQueryProperties,
    private val sql: SensorSqlBuilder,
) {

    // Одна последняя строка таблицы со всеми запрошенными колонками сразу
    fun findLatestRow(
        stationNumber: String,
        parameterCodes: List<Int>,
    ): LatestRow? {
        if (parameterCodes.isEmpty()) return null
        val table = sql.safeIdentifier(stationNumber)
        val columns = parameterCodes.map { sql.safeColumnForCode(it) }
        val threshold = queryProperties.invalidValueThreshold.toDouble()
        val mapper = RowMapper { rs, _ ->
            val values = columns.associate { col ->
                val raw = rs.getObject(col, Double::class.javaObjectType)
                col.toInt() to raw?.takeIf { it > threshold }
            }
            LatestRow(time = rs.getLong("time"), values = values)
        }
        return runCatchingMissingTable {
            sensorJdbcClient
                .sql(sql.latestRowSql(table, columns))
                .query(mapper)
                .optional()
                .orElse(null)
        }
    }

    fun findLatestPoint(
        stationNumber: String,
        parameterCode: Int,
    ): TimeSeriesPoint? {
        val table = sql.safeIdentifier(stationNumber)
        val column = sql.safeColumnForCode(parameterCode)
        return runCatchingMissingTable {
            sensorJdbcClient
                .sql(sql.latestPointSql(table, column))
                .query(POINT_ROW_MAPPER)
                .optional()
                .orElse(null)
        }
    }

    fun findTimeSeries(
        stationNumber: String,
        parameterCode: Int,
        startTime: Long?,
        endTime: Long?,
    ): List<TimeSeriesPoint> {
        val table = sql.safeIdentifier(stationNumber)
        val column = sql.safeColumnForCode(parameterCode)
        return runCatchingMissingTable {
            val spec = sensorJdbcClient.sql(sql.timeSeriesSql(table, column, startTime, endTime))
            bindTimeRange(spec, startTime, endTime)
            spec.query(POINT_ROW_MAPPER).list()
        } ?: emptyList()
    }

    private fun bindTimeRange(
        spec: JdbcClient.StatementSpec,
        startTime: Long?,
        endTime: Long?,
    ) {
        if (startTime != null) spec.param("startTime", startTime)
        if (endTime != null) spec.param("endTime", endTime)
    }

    // Если sensor-БД не знает таблицы/колонки, то отдаём null вместо 500
    private fun <T> runCatchingMissingTable(block: () -> T?): T? = try {
        block()
    } catch (_: BadSqlGrammarException) {
        null
    }

    private companion object {
        val POINT_ROW_MAPPER: RowMapper<TimeSeriesPoint> =
            RowMapper { rs, _ -> TimeSeriesPoint(time = rs.getLong("time"), value = rs.getDouble("value")) }
    }
}
