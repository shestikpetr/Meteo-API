package com.shestikpetr.meteoapi.exception

import com.shestikpetr.meteoapi.dto.common.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// Единая точка перевода доменных и валидационных исключений
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ApiResponse<Unit>> = buildError(HttpStatus.NOT_FOUND, ex.message)

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ApiResponse<Unit>> = buildError(HttpStatus.CONFLICT, ex.message)

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<ApiResponse<Unit>> = buildError(HttpStatus.BAD_REQUEST, ex.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleBeanValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> = buildError(HttpStatus.BAD_REQUEST, formatFieldErrors(ex))

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(ex: AuthenticationException): ResponseEntity<ApiResponse<Unit>> = buildError(HttpStatus.UNAUTHORIZED, ex.message)

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        log.error("Необработанное исключение", ex)
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE)
    }

    private fun buildError(status: HttpStatus, message: String?): ResponseEntity<ApiResponse<Unit>> = ResponseEntity.status(status).body(ApiResponse.error(message ?: status.reasonPhrase))

    private fun formatFieldErrors(ex: MethodArgumentNotValidException): String = ex.bindingResult.fieldErrors
        .joinToString(separator = "; ") { "${it.field}: ${it.defaultMessage ?: DEFAULT_FIELD_ERROR}" }
        .ifBlank { DEFAULT_VALIDATION_ERROR }

    private companion object {
        const val INTERNAL_ERROR_MESSAGE = "Внутренняя ошибка сервера"
        const val DEFAULT_FIELD_ERROR = "невалидное значение"
        const val DEFAULT_VALIDATION_ERROR = "Ошибка валидации"
    }
}
