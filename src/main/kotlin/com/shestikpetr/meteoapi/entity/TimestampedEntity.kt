package com.shestikpetr.meteoapi.entity

import io.jmix.core.entity.annotation.SystemLevel
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import java.time.Instant

@MappedSuperclass
abstract class TimestampedEntity : BaseEntity() {

    @SystemLevel
    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant? = null

    @SystemLevel
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null

    @PrePersist
    fun onCreate() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}
