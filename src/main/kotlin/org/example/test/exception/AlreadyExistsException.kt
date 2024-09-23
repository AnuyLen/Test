package org.example.test.exception

import io.swagger.v3.oas.annotations.Hidden

/**
 * Ошибка - сущность существует.
 *
 * @property message Сообщение.
 */
@Hidden
class AlreadyExistsException(message: String) : RuntimeException(message)