package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "task")
@Schema(description = "Информация о задаче.")
class TaskEntity(

    @Schema(name = "Идентификатор")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task", nullable = false)
    val idTask: Long? = null,


    @Schema(name = "Тип")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type", nullable = false)
    var type: TypeEntity? = null,


    @Schema(name = "Описание")
    @Column(name = "description", nullable = true)
    var description: String? = null,


    @Schema(name = "Запланированная дата")
    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    var date: LocalDate? = null,


    @Schema(name = "Теги")
    @JsonIgnoreProperties("tasks")
    @ManyToMany
    @JoinTable(
        name = "tag_task",
        joinColumns = [JoinColumn(name = "id_task", referencedColumnName = "id_task")],
        inverseJoinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id_tag")]
    )
    var tags: Set<TagEntity>? = hashSetOf()
)