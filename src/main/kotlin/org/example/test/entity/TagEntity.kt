package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*

@Entity
@Table(name = "tag")
class TagEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tag", nullable = false)
    val idTag: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String? = null,

    @JsonIgnoreProperties("tag")
    @ManyToMany
    @JoinTable(
        name = "tag_task",
        joinColumns = [JoinColumn(name = "id_tag", referencedColumnName = "id_tag")],
        inverseJoinColumns = [JoinColumn(name = "id_task", referencedColumnName = "id_task")]
    )
    var tasks: Set<TaskEntity>? = hashSetOf()
)