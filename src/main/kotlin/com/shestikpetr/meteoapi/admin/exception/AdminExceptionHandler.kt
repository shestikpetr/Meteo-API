package com.shestikpetr.meteoapi.admin.exception

import com.shestikpetr.meteoapi.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

// HTML-обёртка над доменными исключениями для SSR-админки
@ControllerAdvice(basePackages = ["com.shestikpetr.meteoapi.admin"])
class AdminExceptionHandler {

    private val log = LoggerFactory.getLogger(AdminExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NotFoundException, model: Model): String {
        model.addAttribute("message", ex.message ?: "Объект не найден")
        return "admin/errors/not_found"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleUnexpected(ex: Exception, model: Model): String {
        log.error("Необработанное исключение в админке", ex)
        model.addAttribute("message", DEFAULT_INTERNAL_MESSAGE)
        return "admin/errors/server_error"
    }

    private companion object {
        const val DEFAULT_INTERNAL_MESSAGE = "Внутренняя ошибка сервера"
    }
}
