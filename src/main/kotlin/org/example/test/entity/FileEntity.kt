package org.example.test.entity

import jakarta.persistence.*

@Entity
@Table(name = "file")
class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_file", nullable = false)
    val id_file: Long? = null,

    @Column(name = "type", nullable = true)
    var type: String? = null,

    @Column(name = "path", nullable = false)
    var path: String,

    @Column(name = "name", nullable = true)
    var name: String? = null,

    @OneToOne
    @JoinColumn(name = "id_task")
    var task: TaskEntity
) {
}