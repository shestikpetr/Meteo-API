package com.shestikpetr.meteoapi.admin.controller

import com.shestikpetr.meteoapi.admin.AdminRoutes
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

// Страница form-login админки
@Controller
class AdminLoginController {

    @GetMapping(AdminRoutes.LOGIN)
    fun loginPage(): String = "admin/login"
}
