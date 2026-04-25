package com.shestikpetr.meteoapi.admin.dto

import com.shestikpetr.meteoapi.entity.Parameter
import org.springframework.stereotype.Component

// Маппинг entity - DTO
@Component
class AdminParameterMapper {

    fun toRow(parameter: Parameter, stationCount: Long): AdminParameterRow = AdminParameterRow(
        id = parameter.id!!,
        code = parameter.code!!,
        name = parameter.name!!,
        unit = parameter.unit,
        description = parameter.description,
        stationCount = stationCount,
    )

    fun toForm(parameter: Parameter): AdminParameterForm = AdminParameterForm(
        id = parameter.id,
        code = parameter.code,
        name = parameter.name,
        unit = parameter.unit,
        description = parameter.description,
    )

    fun toNewEntity(form: AdminParameterForm): Parameter = Parameter().also {
        it.code = form.code
        it.name = form.name
        it.unit = form.unit?.takeIf { value -> value.isNotBlank() }
        it.description = form.description?.takeIf { value -> value.isNotBlank() }
    }

    // Обновляет редактируемые поля (code не трогаем - read-only после создания).
    fun applyEdit(parameter: Parameter, form: AdminParameterForm) {
        parameter.name = form.name
        parameter.unit = form.unit?.takeIf { value -> value.isNotBlank() }
        parameter.description = form.description?.takeIf { value -> value.isNotBlank() }
    }
}
