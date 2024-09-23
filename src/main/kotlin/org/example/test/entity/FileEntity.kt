package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

/**
 *
 * Сущность, связанная с таблицей "file". Содержит информацию о файле.
 *
 * @property id_file Идентификатор файла.
 * @property type Тип файла.
 * @property path Путь к файлу.
 * @property name Название файла.
 * @property task Задача, которой принадлежит файл.
 * @constructor Создает новый объект.
 */
@Schema(description = "Информация о файле.")
@Entity
@Table(name = "file")
class FileEntity(

    @Schema(name = "Идентификатор файла")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_file", nullable = false)
    var id_file: Long? = null,

    @Schema(name = "Тип файла")
    @Column(name = "type", nullable = true)
    var type: String? = null,

    @Schema(name = "Путь к файлу")
    @Column(name = "path", nullable = false)
    var path: String? = null,

    @Schema(name = "Название файла")
    @Column(name = "name", nullable = false)
    var name: String? = null,

    @Schema(name = "Задача, которой принадлежит файл")
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id_task")
    var task: TaskEntity? = null
) {
}