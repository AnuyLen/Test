package org.example.test.model

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.example.test.entity.TagEntity
import org.example.test.entity.TaskEntity

/**
 *
 * Информация о теге.
 *
// * @property id Идентификатор тега.
 * @property title Заголовок тега.
 * @property tasks_id Идентификаторы задач, которые принадлежат данному тегу.
 */

@Schema(description = "Информация о теге.")
data class Tag(

    @Schema(name  = "Заголовок тега.")
    @field:NotNull(message = "Укажите заголовок тега!")
    val title: String?,

    @Schema(name  = "Идентификаторы задач, которые принадлежат данному тегу.")
    val tasks_id: List<Long?>? = null
){

    /**
     *
     * Преобразование [Tag] в [TagEntity].
     *
     * @param taskEntities Список [TaskEntity], которые принадлежат данному тегу.
     * @return [TagEntity]
     */
    fun toEntity(taskEntities: Set<TaskEntity>? = null): TagEntity =
        TagEntity(
//            idTag = this.id,
            title = this.title,
            tasks = taskEntities
        )
}
