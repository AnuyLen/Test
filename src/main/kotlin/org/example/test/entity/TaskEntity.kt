package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDate

/**
 *
 * Сущность, связанная с таблицей "task". Содержит информацию о задаче.
 *
 * @property idTask Идентификатор задачи.
 * @property type Тип задачи.
 * @property name Название задачи.
 * @property description Описание задачи.
 * @property date Запланированная дата.
 * @property tags Теги, которые принадлежат задаче.
 * @constructor Создает новый объект.
 */
@Schema(description = "Информация о задаче.")
@Entity
@Table(name = "task")
class TaskEntity(

    @Schema(name = "Идентификатор задачи")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task", nullable = false)
    var idTask: Long? = null,

    @Schema(name = "Тип задачи")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type", nullable = false)
    var type: TypeEntity? = null,

    @Schema(name = "Название задачи")
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Schema(name = "Описание задачи")
    @Column(name = "description", nullable = false)
    var description: String? = null,

    @Schema(name = "Запланированная дата")
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    var date: LocalDate? = null,

    @Schema(name = "Теги, которые принадлежат задаче")
    @JsonIgnoreProperties("tasks")
    @ManyToMany
    @JoinTable(
        name = "tag_task",
        joinColumns = [JoinColumn(name = "id_task", referencedColumnName = "id_task")],
        inverseJoinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id_tag")]
    )
    var tags: MutableSet<TagEntity>? = hashSetOf()
)