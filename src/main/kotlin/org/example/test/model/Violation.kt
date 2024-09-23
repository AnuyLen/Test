package org.example.test.model

/**
 *
 * Содержит сообщение об ошибке и поле в котором обнаружена ошибка.
 *
 * @property field Поле, в котором обнаружена ошибка.
 * @property message Сообщение об ошибке.
 */
data class Violation(
    val field: String? = null,
    val message: String? = null
)