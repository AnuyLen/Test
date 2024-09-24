package org.example.test.model

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
 * @property typeId Идентификатор типа задачи.
 * @property name Название задачи.
 * @property description Описание задачи.
 * @property date Запланированная дата.
 * @property tagIds Идентификаторы задач, которые принадлежат данному тегу.
 */
@Schema(description = "Информация о задаче.")
data class Task(

    @Schema(description = "Идентификатор типа задачи.")
    @field:NotNull(message = "Укажите идентификатор типа!")
    val typeId: Long? = null,

    @Schema(description = "Название задачи.")
    @field:NotNull(message = "Укажите название задачи!")
    val name: String? = null,

    @Schema(description = "Описание задачи.")
    val description: String? = null,

    @Schema(description = "Запланированная дата.")
    @field:FutureOrPresent(message = "Запланированная дата должна быть не меньше текущей!")
    @field:NotNull(message = "Укажите запланированную дату!")
    val date: LocalDate? = null,

    @Schema(description = "Идентификаторы тегов, которые принадлежат данной задаче.")
    val tagIds: List<Long?>? = null
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
