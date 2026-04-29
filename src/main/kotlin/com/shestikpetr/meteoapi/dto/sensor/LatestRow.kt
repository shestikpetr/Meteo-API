package com.shestikpetr.meteoapi.dto.sensor

// Последняя строка таблицы sensor-БД: общий time + значения по запрошенным колонкам
// values[code] == null означает, что в этой строке значение было NULL или ниже порога заглушки
data class LatestRow(
    val time: Long,
    val values: Map<Int, Double?>,
)
