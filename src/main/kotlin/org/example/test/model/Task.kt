package org.example.test.model

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import org.example.test.entity.TagEntity
import org.example.test.entity.TaskEntity
import org.example.test.entity.TypeEntity
import java.time.LocalDate

/**
 *
 * Информация о задаче.
 *
// * @property id Идентификатор тега.
 * @property id_type Идентификатор типа задачи.
 * @property name Название задачи.
 * @property description Описание задачи.
 * @property date Запланированная дата.
 * @property tags_id Идентификаторы задач, которые принадлежат данному тегу.
 */
@Schema(description = "Информация о задаче.")
data class Task(

    @Schema(name = "Идентификатор типа задачи.")
    @field:NotNull(message = "Укажите идентификатор типа!")
    val id_type: Long?,

    @Schema(name = "Название задачи.")
    @field:NotNull(message = "Укажите название задачи!")
    val name: String?,

    @Schema(name = "Описание задачи.")
    val description: String?,

    @Schema(name = "Запланированная дата.")
    @field:FutureOrPresent(message = "Запланированная дата должна быть не меньше текущей!")
    @field:NotNull(message = "Укажите запланированную дату!")
    val date: LocalDate?,

    @Schema(name = "Идентификаторы тегов, которые принадлежат данной задаче.")
    val tags_id: List<Long?>?
){

    /**
     *
     * Преобразование [Task] в [TaskEntity].
     *
     * @param typeEntity [TypeEntity], кторому принадлежит задача.
     * @param tagEntities Список [TaskEntity], которые принадлежат данному тегу.
     * @return [TaskEntity]
     */
    fun toEntity(typeEntity: TypeEntity, tagEntities: Set<TagEntity>? = null): TaskEntity =
        TaskEntity(
//            idTask = this.id,
            type = typeEntity,
            name = this.name,
            description = this.description,
            date = this.date,
            tags = tagEntities
        )
}
