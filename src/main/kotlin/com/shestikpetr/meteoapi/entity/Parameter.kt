package com.shestikpetr.meteoapi.entity

import io.jmix.core.metamodel.annotation.InstanceName
import io.jmix.core.metamodel.annotation.JmixEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@JmixEntity
@Entity
@Table(name = "parameters")
class Parameter : BaseEntity() {

    @NotBlank
    @Size(max = 20)
    @Column(name = "code", nullable = false, length = 20, unique = true)
    var code: String? = null

    @InstanceName
    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    var name: String? = null

    @Size(max = 20)
    @Column(name = "unit", length = 20)
    var unit: String? = null

    @Lob
    @Column(name = "description")
    var description: String? = null

    @Size(max = 50)
    @Column(name = "category", length = 50)
    var category: String? = null
}
