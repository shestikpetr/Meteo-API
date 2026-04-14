package com.shestikpetr.meteoapi.entity

import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal

@JmixEntity
@Entity
@Table(name = "stations")
class Station : TimestampedEntity() {

    @Column(name = "station_number", nullable = false, length = 20, unique = true)
    var stationNumber: String? = null

    @InstanceName
    @Column(name = "name", nullable = false, length = 100)
    var name: String? = null

    @Column(name = "location", length = 200)
    var location: String? = null

    @Column(name = "latitude", precision = 10, scale = 6)
    var latitude: BigDecimal? = null

    @Column(name = "longitude", precision = 10, scale = 6)
    var longitude: BigDecimal? = null

    @Column(name = "altitude", precision = 10, scale = 2)
    var altitude: BigDecimal? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
}
