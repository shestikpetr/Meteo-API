package com.shestikpetr.meteoapi.entity

import com.shestikpetr.meteoapi.dto.common.UserRole
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class UserRoleConverter : AttributeConverter<UserRole, String> {

    override fun convertToDatabaseColumn(attribute: UserRole?): String? = attribute?.value

    override fun convertToEntityAttribute(dbData: String?): UserRole? = dbData?.let(UserRole::fromValue)
}
