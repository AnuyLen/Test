package org.example.test.exception

/**
 * Ошибка - сущность существует.
 *
 * @property message Сообщение.
 */
class AlreadyExistsException(message: String) : RuntimeException(message)