package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "task")
class TaskEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_task", nullable = false)
    val idTask: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_type", nullable = false)
    var type: TypeEntity? = null,

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @Column(name = "date", nullable = false)
    @Temporal(TemporalType.DATE)
    var date: LocalDate? = null,

    @JsonIgnoreProperties("tasks")
    @ManyToMany
    @JoinTable(
        name = "tag_task",
        joinColumns = [JoinColumn(name = "id_task", referencedColumnName = "id_task")],
        inverseJoinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id_tag")]
    )
    var tag: Set<TagEntity>? = hashSetOf()
)