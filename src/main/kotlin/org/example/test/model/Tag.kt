package org.example.test.model

import org.example.test.entity.TagEntity
import org.example.test.entity.TaskEntity

data class Tag(
    val id: Long? = null,
    val title: String? = null,
    val tasks_id: List<Long?>? = null
){
    fun toEntity(tasksEntity: Set<TaskEntity>? = null): TagEntity =
        TagEntity(
            idTag = this.id,
            title = this.title,
            tasks = tasksEntity
        )
}
