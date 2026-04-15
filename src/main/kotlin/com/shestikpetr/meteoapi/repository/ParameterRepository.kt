package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.Parameter
import org.springframework.data.jpa.repository.JpaRepository

interface ParameterRepository : JpaRepository<Parameter, Int> {

    fun findByCode(code: String): Parameter?

    fun findAllByCodeIn(codes: Collection<String>): List<Parameter>
}
