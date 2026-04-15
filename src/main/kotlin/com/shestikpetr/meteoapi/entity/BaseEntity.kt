package com.shestikpetr.meteoapi.entity

import io.jmix.core.entity.annotation.JmixGeneratedValue
import io.jmix.core.entity.annotation.SystemLevel
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity {

    @SystemLevel
    @Id
    @JmixGeneratedValue
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null
}
