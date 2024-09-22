package org.example.test.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Entity
@Table(name = "type")
@Schema(description = "Информация о типе.")
class TypeEntity (

    @Schema(name = "Идентификатор")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type", nullable = false)
    val id_type: Long? = null,


    @Schema(name = "Уровень приоритета")
    @Column(name = "priority", nullable = false)
    var priority: Long? = null,


    @Schema(name = "Название")
    @Column(name = "name")
    var name: String? = null,

) {
}