package com.shestikpetr.meteoapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotNull

@Entity
@Table(
    name = "station_parameters",
    uniqueConstraints = [
        UniqueConstraint(
            name = "unique_station_parameter",
            columnNames = ["station_id", "parameter_code"],
        ),
    ],
)
class StationParameter : BaseEntity() {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    var station: Station? = null

    @NotNull
    @Column(name = "parameter_code", nullable = false)
    var parameterCode: Int? = null

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true
}
