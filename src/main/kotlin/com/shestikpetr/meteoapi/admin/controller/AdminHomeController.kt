package com.shestikpetr.meteoapi.admin.controller

import com.shestikpetr.meteoapi.admin.AdminRoutes
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

// Главная страница админки: меню разделов.
@Controller
class AdminHomeController {

    @GetMapping(AdminRoutes.BASE)
    fun home(): String = "admin/index"
}
