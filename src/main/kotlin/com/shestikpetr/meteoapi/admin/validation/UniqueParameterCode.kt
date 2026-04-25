package com.shestikpetr.meteoapi.admin.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

// Проверяет уникальность parameters.code в БД, исключая текущий редактируемый параметр
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueParameterCodeValidator::class])
annotation class UniqueParameterCode(
    val message: String = "Параметр с этим кодом уже существует",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
