package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.config.SensorQueryProperties
import com.shestikpetr.meteoapi.dto.sensor.LatestRow
import com.shestikpetr.meteoapi.dto.sensor.TimeSeriesPoint
import org.slf4j.LoggerFactory
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

    private val log = LoggerFactory.getLogger(SensorRepository::class.java)

    // Последняя строка таблицы по запрошенным кодам параметров
    fun findLatestRow(
        stationNumber: String,
        parameterCodes: List<Int>,
    ): LatestRow? {
        if (parameterCodes.isEmpty()) return null
        val table = sql.safeIdentifier(stationNumber)
        val requested = parameterCodes.map { sql.safeColumnForCode(it) }
        val threshold = queryProperties.invalidValueThreshold.toDouble()
        val mapper = RowMapper { rs, _ ->
            val available = (1..rs.metaData.columnCount)
                .map { rs.metaData.getColumnLabel(it) }
                .toSet()
            val values = requested.associate { col ->
                val raw = if (col in available) rs.getObject(col, Double::class.javaObjectType) else null
                col.toInt() to raw?.takeIf { it > threshold }
            }
            LatestRow(time = rs.getLong("time"), values = values)
        }
        return runCatchingMissingTable("latestRow", table) {
            sensorJdbcClient
                .sql(sql.latestRowSql(table))
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
        return runCatchingMissingTable("latestPoint", table) {
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
        return runCatchingMissingTable("timeSeries", table) {
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
    private fun <T> runCatchingMissingTable(operation: String, table: String, block: () -> T?): T? = try {
        block()
    } catch (ex: BadSqlGrammarException) {
        log.warn("BadSqlGrammar в {} для таблицы `{}`: {}", operation, table, ex.message)
        null
    }

    private companion object {
        val POINT_ROW_MAPPER: RowMapper<TimeSeriesPoint> =
            RowMapper { rs, _ -> TimeSeriesPoint(time = rs.getLong("time"), value = rs.getDouble("value")) }
    }
}
