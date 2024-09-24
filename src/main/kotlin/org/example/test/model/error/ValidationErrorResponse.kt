package org.example.test.model.error

import io.swagger.v3.oas.annotations.Hidden

/**
 *
 * Содержит список [Violation].
 *
 * @property errors Список ошибок.
 */
@Hidden
data class ValidationErrorResponse(
    val errors: List<Violation>? = null
)