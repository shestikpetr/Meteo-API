package com.shestikpetr.meteoapi.admin.service

import com.shestikpetr.meteoapi.admin.dto.AdminParameterDeleteImpact
import com.shestikpetr.meteoapi.admin.dto.AdminParameterForm
import com.shestikpetr.meteoapi.admin.dto.AdminParameterMapper
import com.shestikpetr.meteoapi.admin.dto.AdminParameterRow
import com.shestikpetr.meteoapi.admin.dto.AffectedStation
import com.shestikpetr.meteoapi.entity.Parameter
import com.shestikpetr.meteoapi.exception.NotFoundException
import com.shestikpetr.meteoapi.repository.ParameterRepository
import com.shestikpetr.meteoapi.repository.StationParameterRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminParameterService(
    private val parameterRepository: ParameterRepository,
    private val stationParameterRepository: StationParameterRepository,
    private val mapper: AdminParameterMapper,
) {

    @Transactional(readOnly = true)
    fun listAll(): List<AdminParameterRow> {
        val usage = stationParameterRepository.countStationsGroupedByParameter()
            .associate { it.parameterId to it.stationCount }
        return parameterRepository.findAll()
            .sortedBy { it.code }
            .map { mapper.toRow(it, stationCount = usage[it.id!!] ?: 0L) }
    }

    @Transactional(readOnly = true)
    fun loadForEdit(id: Int): AdminParameterForm = mapper.toForm(findOrThrow(id))

    fun create(form: AdminParameterForm) {
        parameterRepository.save(mapper.toNewEntity(form))
    }

    fun update(id: Int, form: AdminParameterForm) {
        val parameter = findOrThrow(id)
        mapper.applyEdit(parameter, form)
        parameterRepository.save(parameter)
    }

    @Transactional(readOnly = true)
    fun previewDelete(id: Int): AdminParameterDeleteImpact {
        val parameter = findOrThrow(id)
        val affected = stationParameterRepository.findStationsLinkedToParameter(id)
            .map { AffectedStation(it.stationNumber, it.name) }
        return AdminParameterDeleteImpact(
            parameter = mapper.toRow(parameter, stationCount = affected.size.toLong()),
            affectedStations = affected,
        )
    }

    fun delete(id: Int) {
        if (!parameterRepository.existsById(id)) {
            throw NotFoundException("Параметр не найден: id=$id")
        }
        parameterRepository.deleteById(id)
    }

    private fun findOrThrow(id: Int): Parameter = parameterRepository.findById(id)
        .orElseThrow { NotFoundException("Параметр не найден: id=$id") }
}
