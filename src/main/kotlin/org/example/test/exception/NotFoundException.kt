package org.example.test.exception

import io.swagger.v3.oas.annotations.Hidden

/**
 * Ошибка поиска сущности.
 *
 * @property message Сообщение.
 */
@Hidden
class NotFoundException(message: String) : RuntimeException(message)