package com.shestikpetr.meteoapi.admin

// Все пути SSR-админки
object AdminRoutes {
    const val BASE = "/admin"

    const val LOGIN = "$BASE/login"
    const val LOGOUT = "$BASE/logout"

    const val PARAMETERS = "$BASE/parameters"
}
