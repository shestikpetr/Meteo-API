package com.shestikpetr.meteoapi.admin.controller

import com.shestikpetr.meteoapi.admin.AdminRoutes
import com.shestikpetr.meteoapi.admin.dto.AdminParameterForm
import com.shestikpetr.meteoapi.admin.service.AdminParameterService
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(AdminRoutes.PARAMETERS)
class AdminParameterController(
    private val service: AdminParameterService,
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("parameters", service.listAll())
        return "admin/parameters/list"
    }

    @GetMapping("/new")
    fun newForm(model: Model): String {
        model.addAttribute("form", AdminParameterForm())
        return "admin/parameters/form"
    }

    @PostMapping
    fun create(@Valid @ModelAttribute("form") form: AdminParameterForm, binding: BindingResult): String {
        if (binding.hasErrors()) return "admin/parameters/form"
        service.create(form)
        return "redirect:${AdminRoutes.PARAMETERS}"
    }

    @GetMapping("/{id}/edit")
    fun editForm(@PathVariable id: Int, model: Model): String {
        model.addAttribute("form", service.loadForEdit(id))
        return "admin/parameters/form"
    }

    @PostMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @Valid @ModelAttribute("form") form: AdminParameterForm,
        binding: BindingResult,
    ): String {
        if (binding.hasErrors()) return "admin/parameters/form"
        service.update(id, form)
        return "redirect:${AdminRoutes.PARAMETERS}"
    }

    @GetMapping("/{id}/delete-confirm")
    fun deleteConfirm(@PathVariable id: Int, model: Model): String {
        model.addAttribute("impact", service.previewDelete(id))
        return "admin/parameters/delete_confirm"
    }

    @PostMapping("/{id}/delete")
    fun delete(@PathVariable id: Int): String {
        service.delete(id)
        return "redirect:${AdminRoutes.PARAMETERS}"
    }
}
