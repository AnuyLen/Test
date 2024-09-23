package org.example.test.model

import jakarta.validation.constraints.NotNull
import org.example.test.entity.TagEntity
import org.example.test.entity.TaskEntity

/**
 *
 * Информация о теге.
 *
 * @property id Идентификатор тега.
 * @property title Заголовок тега.
 * @property tasks_id Идентификаторы задач, которые принадлежат данному тегу.
 */
data class Tag(

    val id: Long? = null,

    @field:NotNull(message = "Укажите заголовок тега!")
    val title: String?,

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
            idTag = this.id,
            title = this.title,
            tasks = taskEntities
        )
}
