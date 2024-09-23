package org.example.test.exception

/**
 * Ошибка поиска сущности.
 *
 * @property message Сообщение.
 */
class NotFoundException(message: String) : RuntimeException(message)