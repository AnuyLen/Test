package org.example.test.model

import io.swagger.v3.oas.annotations.Hidden

/**
 *
 * Содержит сообщение для вывода пользователю.
 *
 * @property message Сообщение.
 */
@Hidden
data class Response(
    val message: String = ""
)
