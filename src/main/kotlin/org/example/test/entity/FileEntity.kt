package org.example.test.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
@Table(name = "file")
@Schema(description = "Информация о файле.")
class FileEntity(

    @Schema(name = "Идентификатор")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_file", nullable = false)
    val id_file: Long? = null,


    @Schema(name = "Тип файла")
    @Column(name = "type", nullable = true)
    var type: String? = null,


    @Schema(name = "Путь до файла")
    @Column(name = "path", nullable = false)
    var path: String,


    @Schema(name = "Название")
    @Column(name = "name", nullable = true)
    var name: String? = null,


    @Schema(name = "Задача")
    @OneToOne
    @JoinColumn(name = "id_task")
    var task: TaskEntity
) {
}