package org.example.test.model.error

import io.swagger.v3.oas.annotations.Hidden

/**
 *
 * Содержит сообщение об ошибке и поле в котором обнаружена ошибка.
 *
 * @property field Поле, в котором обнаружена ошибка.
 * @property message Сообщение об ошибке.
 */
@Hidden
data class Violation(
    val field: String? = null,
    val message: String? = null
)