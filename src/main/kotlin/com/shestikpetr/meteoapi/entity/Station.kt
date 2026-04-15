package com.shestikpetr.meteoapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.math.BigDecimal

@Entity
@Table(name = "stations")
class Station : TimestampedEntity() {

    @NotBlank
    @Size(max = 20)
    @Column(name = "station_number", nullable = false, length = 20, unique = true)
    var stationNumber: String? = null

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    var name: String? = null

    @Size(max = 200)
    @Column(name = "location", length = 200)
    var location: String? = null

    @DecimalMin("-90")
    @DecimalMax("90")
    @Column(name = "latitude", precision = 10, scale = 6)
    var latitude: BigDecimal? = null

    @DecimalMin("-180")
    @DecimalMax("180")
    @Column(name = "longitude", precision = 10, scale = 6)
    var longitude: BigDecimal? = null

    @Column(name = "altitude", precision = 10, scale = 2)
    var altitude: BigDecimal? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
}
