package com.shestikpetr.meteoapi.entity

import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@JmixEntity
@Entity
@Table(name = "users")
class User : TimestampedEntity() {

    @InstanceName
    @Column(name = "username", nullable = false, length = 50, unique = true)
    var username: String? = null

    @Column(name = "email", nullable = false, length = 255, unique = true)
    var email: String? = null

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true

    @Column(name = "role", nullable = false, length = 20)
    var role: String = "user"
}
