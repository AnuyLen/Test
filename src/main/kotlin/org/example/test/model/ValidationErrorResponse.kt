package org.example.test.model

/**
 *
 * Содержит список [Violation].
 *
 * @property errors Список ошибок.
 */
data class ValidationErrorResponse(
    val errors: List<Violation>? = null
)