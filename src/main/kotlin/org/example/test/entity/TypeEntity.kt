package org.example.test.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

/**
 *
 * Сущность, связанная с таблицей "type". Содержит информацию о типе задач.
 *
 * @property id_type Идентификатор типа.
 * @property priority Приоритет.
 * @property name Название типа.
 * @constructor Создает новый объект.
 */
@Schema(description = "Информация о типе задач.")
@Entity
@Table(name = "type")
class TypeEntity (

    @Schema(name = "Идентификатор типа")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type", nullable = false)
    var id_type: Long? = null,

    @Schema(name = "Уровень приоритета")
    @Column(name = "priority", nullable = false)
    var priority: Long? = null,

    @Schema(name = "Название типа")
    @Column(name = "name")
    var name: String? = null,

) {
}