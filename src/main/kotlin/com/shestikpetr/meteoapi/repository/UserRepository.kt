package com.shestikpetr.meteoapi.repository

import com.shestikpetr.meteoapi.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Int> {

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}
