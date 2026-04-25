package com.shestikpetr.meteoapi.admin.validation

import com.shestikpetr.meteoapi.admin.dto.AdminParameterForm
import com.shestikpetr.meteoapi.repository.ParameterRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UniqueParameterCodeValidator(
    private val parameterRepository: ParameterRepository,
) : ConstraintValidator<UniqueParameterCode, AdminParameterForm> {

    override fun isValid(form: AdminParameterForm?, context: ConstraintValidatorContext): Boolean {
        // Если форма пустая или код не задан - не наша забота, отработает @NotNull
        val code = form?.code ?: return true

        val existing = parameterRepository.findByCode(code) ?: return true
        if (existing.id == form.id) return true

        attachErrorToCodeField(context)
        return false
    }

    // Привязываем нарушение к полю code, чтобы Bootstrap-форма подсветила нужный input
    private fun attachErrorToCodeField(context: ConstraintValidatorContext) {
        val message = context.defaultConstraintMessageTemplate
        context.disableDefaultConstraintViolation()
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode("code")
            .addConstraintViolation()
    }
}
