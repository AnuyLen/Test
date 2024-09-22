package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
@Table(name = "tag")
@Schema(description = "Информация о теге.")
class TagEntity(

    @Schema(name = "Идентификатор")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag", nullable = false)
    val idTag: Long? = null,

    @Schema(name = "Название")
    @Column(name = "title", nullable = false)
    var title: String? = null,

    @Schema(name = "Задачи")
    @JsonIgnoreProperties("tags")
    @ManyToMany
    @JoinTable(
        name = "tag_task",
        joinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id_tag")],
        inverseJoinColumns = [JoinColumn(name = "id_task", referencedColumnName = "id_task")]
    )
    var tasks: Set<TaskEntity>? = hashSetOf()
)