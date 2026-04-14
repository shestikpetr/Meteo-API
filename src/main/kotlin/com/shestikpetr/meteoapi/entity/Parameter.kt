package com.shestikpetr.meteoapi.entity

import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.persistence.Table

@JmixEntity
@Entity
@Table(name = "parameters")
class Parameter : BaseEntity() {

    @Column(name = "code", nullable = false, length = 20, unique = true)
    var code: String? = null

    @InstanceName
    @Column(name = "name", nullable = false, length = 100)
    var name: String? = null

    @Column(name = "unit", length = 20)
    var unit: String? = null

    @Lob
    @Column(name = "description")
    var description: String? = null

    @Column(name = "category", length = 50)
    var category: String? = null
}
