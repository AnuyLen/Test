package org.example.test.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.example.test.entity.TagEntity
import org.example.test.entity.TaskEntity

/**
 *
 * Информация о теге.
 *
 * @property title Заголовок тега.
 * @property taskIds Идентификаторы задач, которые принадлежат данному тегу.
 */
@Schema(description = "Информация о теге.")
data class Tag(

    @Schema(description  = "Заголовок тега.")
    @field:NotNull(message = "Укажите заголовок тега!")
    val title: String?,

    @Schema(description  = "Идентификаторы задач, которые принадлежат данному тегу.")
    val taskIds: List<Long?>? = null
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
