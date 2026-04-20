package com.shestikpetr.meteoapi.entity

import com.shestikpetr.meteoapi.dto.common.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Entity
@Table(name = "users")
class User : TimestampedEntity() {

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9_]+$")
    @Column(name = "username", nullable = false, length = 50, unique = true)
    var username: String? = null

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(name = "email", nullable = false, length = 255, unique = true)
    var email: String? = null

    @NotBlank
    @Size(max = 255)
    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @NotNull
    @Convert(converter = UserRoleConverter::class)
    @Column(name = "role", nullable = false, length = 20)
    var role: UserRole = UserRole.USER
}
