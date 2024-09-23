package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

/**
 *
 * Сущность, связанная с таблицей "tag". Содержит информацию о теге.
 *
 * @property idTag Идентификатор тега.
 * @property title Заголовок тега.
 * @property tasks Задачи, которые принадлежат данному тегу.
 * @constructor Создает новый объект.
 */
@Schema(description = "Информация о теге.")
@Entity
@Table(name = "tag")
class TagEntity(

    @Schema(name = "Идентификатор тега")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag", nullable = false)
    var idTag: Long? = null,

    @Schema(name = "Название тега")
    @Column(name = "title", unique = true, nullable = false)
    var title: String? = null,

    @Schema(name = "Задачи, которые принадлежат данному тегу")
    @JsonIgnoreProperties("tags")
    @ManyToMany
    @JoinTable(
        name = "tag_task",
        joinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id_tag")],
        inverseJoinColumns = [JoinColumn(name = "id_task", referencedColumnName = "id_task")]
    )
    var tasks: Set<TaskEntity>? = hashSetOf()
)