package com.shestikpetr.meteoapi.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "parameters")
class Parameter : BaseEntity() {

    @NotBlank
    @Size(max = 20)
    @Column(name = "code", nullable = false, length = 20, unique = true)
    var code: String? = null

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
