package com.shestikpetr.meteoapi.dto.common

import com.fasterxml.jackson.annotation.JsonValue

enum class UserRole(
    @get:JsonValue val value: String,
) {
    USER("user"),
    ADMIN("admin"),
    ;

    companion object {
        fun fromValue(raw: String): UserRole = entries.firstOrNull { it.value.equals(raw, ignoreCase = true) }
            ?: throw IllegalArgumentException("Неизвестная роль: $raw")
    }
}
