package com.shestikpetr.meteoapi.entity

import io.jmix.core.metamodel.annotation.DependsOnProperties
import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

@JmixEntity
@Entity
@Table(
    name = "user_stations",
    uniqueConstraints = [UniqueConstraint(name = "unique_user_station", columnNames = ["user_id", "station_id"])],
)
class UserStation : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    var station: Station? = null

    @Column(name = "custom_name", length = 100)
    var customName: String? = null

    @Column(name = "is_favorite", nullable = false)
    var isFavorite: Boolean = false

    @Column(name = "created_at", updatable = false, nullable = false)
    var createdAt: Instant? = null

    @PrePersist
    fun onCreate() {
        createdAt = Instant.now()
    }

    @InstanceName
    @DependsOnProperties("user", "station", "customName")
    fun getCaption(): String {
        val userPart = user?.username ?: "?"
        val stationPart = customName?.takeIf { it.isNotBlank() } ?: station?.name ?: "?"
        return "$userPart — $stationPart"
    }
}
