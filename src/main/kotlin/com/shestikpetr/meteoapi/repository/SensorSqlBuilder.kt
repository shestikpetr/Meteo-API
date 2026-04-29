package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.config.SensorQueryProperties
import org.springframework.stereotype.Component

// Сборка SQL-запросов к sensor-БД и валидация идентификаторов
@Component
class SensorSqlBuilder(
    private val queryProperties: SensorQueryProperties,
) {

    fun safeIdentifier(raw: String): String {
        require(raw.matches(SAFE_IDENTIFIER)) { "Недопустимый идентификатор: '$raw'" }
        return raw
    }

    // Числовой код параметра используется как имя колонки в sensor-БД
    fun safeColumnForCode(code: Int): String {
        require(code >= 0) { "Код параметра должен быть неотрицательным: $code" }
        return code.toString()
    }

    // SELECT * — клиент по метаданным ResultSet сам разберёт, какие колонки пришли
    fun latestRowSql(table: String): String = "SELECT * FROM `$table` ORDER BY time DESC LIMIT 1"

    fun latestPointSql(table: String, column: String): String =
        """
        SELECT time, `$column` AS value
        FROM `$table`
        WHERE `$column` > ${queryProperties.invalidValueThreshold}
        ORDER BY time DESC
        LIMIT 1
        """.trimIndent()

    fun timeSeriesSql(
        table: String,
        column: String,
        startTime: Long?,
        endTime: Long?,
    ): String {
        val base = "SELECT time, `$column` AS value FROM `$table` " +
            "WHERE `$column` > ${queryProperties.invalidValueThreshold}"
        return base + timeRangeClause(startTime, endTime) + " ORDER BY time ASC"
    }

    private fun timeRangeClause(startTime: Long?, endTime: Long?): String {
        val filters = buildList {
            if (startTime != null) add("time >= :startTime")
            if (endTime != null) add("time <= :endTime")
        }
        return if (filters.isEmpty()) "" else " AND " + filters.joinToString(" AND ")
    }

    private companion object {
        val SAFE_IDENTIFIER = Regex("^[A-Za-z0-9_]{1,64}$")
    }
}
