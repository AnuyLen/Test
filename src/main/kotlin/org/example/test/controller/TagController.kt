package org.example.test.controller

import jakarta.validation.Valid
import org.example.test.entity.TagEntity
import org.example.test.model.Tag
import org.example.test.repository.TagRepository
import org.example.test.repository.TaskRepository
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TagController(private val tagRepository: TagRepository,
                    private val taskRepository: TaskRepository
) {

    //Get all tags
    @GetMapping("/tags")
    fun getAllTags(): List<TagEntity> {
        return tagRepository.findByTasksIsNotNull()
    }

    //Get tag by id
    @GetMapping("/tag/{id}")
    fun getTagByID(
        @PathVariable(value = "id") tagId: Long,
        @RequestParam("sort") sortType: String?
    ): ResponseEntity<TagEntity> {
        return tagRepository.findById(tagId).map { tag ->
            when (sortType){
                "desc" -> tag.tasks = tag.tasks?.sortedByDescending { it.type?.priority }?.toSet()
                "asc" -> tag.tasks = tag.tasks?.sortedBy { it.type?.priority }?.toSet()
            }
            ResponseEntity.ok(tag)
        }.orElse(ResponseEntity.notFound().build())
    }

    //Create new tag
    @PostMapping("/tag")
    fun createNewTag(@Valid @RequestBody tag: Tag): ResponseEntity<*> {
        return when {
            tag.title.isNullOrEmpty() -> ResponseEntity.badRequest().body("Проверьте введенные данные: название")
            else -> {
                if (tagRepository.findByTitle(tag.title) != null){
                    ResponseEntity.badRequest().body("Тег с таким названием уже существует!")
                } else {
                    val tasks = tag.tasks_id?.let { taskRepository.findAllById(it) }?.toSet()
                    val tagEntity = tag.toEntity(tasks)
                    ResponseEntity.ok().body(tagRepository.save(tagEntity))
                }
            }
        }
    }

    //Update tag
    @PutMapping("/tag/{id}")
    fun updateTagById(@PathVariable(value = "id") tagId: Long,
                      @Valid @RequestBody newTag: Tag
    ): ResponseEntity<*> {
        return if (newTag.title?.let { tagRepository.findByTitle(it) } != null) {
            ResponseEntity.badRequest().body("Тег с таким названием уже существует!")
        } else {
            tagRepository.findById(tagId).map { existingTag ->
                val updateTagEntity: TagEntity = existingTag.apply {
                    title = newTag.title ?: existingTag.title
                    tasks = newTag.tasks_id?.let { taskRepository.findAllById(it).toSet() } ?: existingTag.tasks
                }
                ResponseEntity.ok().body(tagRepository.save(updateTagEntity))
            }.orElse(ResponseEntity.notFound().build())
        }
    }

    @DeleteMapping("/tag/{id}")
    fun deleteTagById(@PathVariable(value = "id") tagId: Long): ResponseEntity<String> {
        return tagRepository.findById(tagId).map { tag ->
            tagRepository.delete(tag)
            ResponseEntity.ok().body("Тег удален")
        }.orElse(ResponseEntity.notFound().build())
    }

}