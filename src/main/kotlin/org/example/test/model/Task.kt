package org.example.test.model

import org.example.test.entity.TagEntity
import org.example.test.entity.TaskEntity
import org.example.test.entity.TypeEntity
import java.time.LocalDate

data class Task(
    val id: Long? = null,
    val id_type: Long? = null,
    val description: String? = null,
    val date: LocalDate? = null,
    val file: Long? = null,
    val tags_id: List<Long?>? = null
){
    fun toEntity(type: TypeEntity, tags: Set<TagEntity>? = null): TaskEntity =
        TaskEntity(
            idTask = this.id,
            type = type,
            description = this.description,
            date = this.date,
            tag = tags
        )
}
