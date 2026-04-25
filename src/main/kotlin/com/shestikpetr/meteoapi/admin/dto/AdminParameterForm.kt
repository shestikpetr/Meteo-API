package com.shestikpetr.meteoapi.admin.dto

import com.shestikpetr.meteoapi.admin.validation.UniqueParameterCode
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

// Бэкенд формы создания/редактирования
// id == null означает создание, не-null - редактирование того же параметра
@UniqueParameterCode
class AdminParameterForm(
    var id: Int? = null,

    @field:NotNull(message = "Код обязателен")
    var code: Int? = null,

    @field:NotBlank(message = "Имя обязательно")
    @field:Size(max = 100, message = "Имя не длиннее 100 символов")
    var name: String? = null,

    @field:Size(max = 20, message = "Единица не длиннее 20 символов")
    var unit: String? = null,

    var description: String? = null,
)
