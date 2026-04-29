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
) {

    // Одна последняя строка таблицы со всеми запрошенными колонками сразу
    fun findLatestRow(
        stationNumber: String,
        parameterCodes: List<Int>,
    ): LatestRow? {
        if (parameterCodes.isEmpty()) return null
        val table = safeIdentifier(stationNumber)
        val columns = parameterCodes.map { safeColumnForCode(it) }
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
                .sql(latestRowSql(table, columns))
                .query(mapper)
                .optional()
                .orElse(null)
        }
    }

    fun findLatestPoint(
        stationNumber: String,
        parameterCode: Int,
    ): TimeSeriesPoint? {
        val table = safeIdentifier(stationNumber)
        val column = safeColumnForCode(parameterCode)
        return runCatchingMissingTable {
            sensorJdbcClient
                .sql(latestPointSql(table, column))
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
        val table = safeIdentifier(stationNumber)
        val column = safeColumnForCode(parameterCode)
        return runCatchingMissingTable {
            val spec = sensorJdbcClient.sql(timeSeriesSql(table, column, startTime, endTime))
            bindTimeRange(spec, startTime, endTime)
            spec.query(POINT_ROW_MAPPER).list()
        } ?: emptyList()
    }

    private fun latestRowSql(
        table: String,
        columns: List<String>,
    ): String {
        val cols = columns.joinToString(", ") { "`$it`" }
        return "SELECT time, $cols FROM `$table` ORDER BY time DESC LIMIT 1"
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

    // Если sensor-БД не знает таблицы/колонки, то отдаём null вместо 500
    private fun <T> runCatchingMissingTable(block: () -> T?): T? = try {
        block()
    } catch (_: BadSqlGrammarException) {
        null
    }

    private fun safeIdentifier(raw: String): String {
        require(raw.matches(SAFE_IDENTIFIER)) { "Недопустимый идентификатор: '$raw'" }
        return raw
    }

    // Числовой код параметра используется как имя колонки в sensor-БД
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
