package org.example.test.advice

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import org.example.test.exception.AlreadyExistsException
import org.example.test.exception.NotFoundException
import org.example.test.model.Response
import org.example.test.model.ValidationErrorResponse
import org.example.test.model.Violation
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.io.IOException

/**
 * Обработчик ошибок.
 */
@Hidden
@ControllerAdvice
class GlobalControllerExceptionHandler{

    /**
     * Обработка ошибок.
     *
     * @param ex Исключение.
     */
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleAll(ex: Exception): Response {
        return Response(ex.toString())
    }

    /**
     * Обработка ошибкок, возникающих при выполнении операций ввода или вывода.
     *
     * @param ex Исключение.
     */
    @ExceptionHandler(IOException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleIOException(ex: IOException): Response {
        return Response(ex.toString())
    }

    /**
     * Обаботка исключений, возникающих при проверке входящих данных.
     *
     * @param ex Исключение, которое генерируется при сбое проверки аргумента.
     * @return [ValidationErrorResponse].
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException
    ): ValidationErrorResponse {
        val violations: List<Violation> = ex.bindingResult.fieldErrors.stream().map { error ->
            Violation(error.field, error.defaultMessage)
        }.toList()
        val errors = ValidationErrorResponse(violations)
        return errors
    }

    /**
     * Обаботка исключения [NotFoundException].
     *
     * @param ex Исключение.
     * @return [Response] с сообщением об ошибке.
     */
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun notFound(ex: NotFoundException): Response {
        return Response(ex.localizedMessage)
    }

    /**
     * Обаботка исключения [AlreadyExistsException].
     *
     * @param ex Исключение.
     * @return [Response] с сообщением об ошибке.
     */
    @ExceptionHandler(AlreadyExistsException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun alreadyExists(ex: AlreadyExistsException): Response {
        return Response(ex.localizedMessage)
    }

}