package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.example.test.entity.TagEntity
import org.example.test.model.Tag
import org.example.test.repository.TagRepository
import org.example.test.repository.TaskRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")
@io.swagger.v3.oas.annotations.tags.Tag(
    name = "Теги.",
    description = "Все методы для работы с тегами.",
)
class TagController(private val tagRepository: TagRepository,
                    private val taskRepository: TaskRepository
) {

    //Get all tags
    @GetMapping("/tags")
    @Operation(summary = "Получить информацию о всех тегах, у которых есть задачи.")
    fun getAllTags(): List<TagEntity> {
        return tagRepository.findByTasksIsNotNull()
    }

    //Get tag by id
    @GetMapping("/tag/{id}")
    @Operation(summary = "Получить информацию о теге и его задачах по id.")
    fun getTagByID(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long,
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию.",
            example = "asc")
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
    @Operation(summary = "Создание нового тега.")
    fun createNewTag(
        @Parameter(description = "Информация о теге.")
        @Valid @RequestBody tag: Tag
    ): ResponseEntity<*> {
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
    @Operation(summary = "Изменение информации о теге или создание нового тега, если он не найден.")
    fun updateByIdOrCreateTag(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long,
        @Parameter(description = "Информация о теге.")
        @Valid @RequestBody newTag: Tag
    ): ResponseEntity<*> {
        return when {
            newTag.title.isNullOrEmpty() -> ResponseEntity.badRequest().body("Проверьте введенные данные: название")
            newTag.title.let { tagRepository.findByTitle(it) } != null -> ResponseEntity.badRequest()
                .body("Тег с таким названием уже существует!")
            else -> {
                val updateTagEntity = TagEntity(
                    idTag = tagId,
                    title = newTag.title,
                    tasks = newTag.tasks_id?.let { taskRepository.findAllById(it).toSet() }
                )
                ResponseEntity.ok().body(tagRepository.save(updateTagEntity))
            }
        }
    }

    @PatchMapping("/tag/{id}")
    @Operation(summary = "Изменение информации о теге.")
    fun updateTagById(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long,
        @Parameter(description = "Информация о теге.")
        @Valid @RequestBody updateTag: Tag
    ): ResponseEntity<TagEntity> {
        return tagRepository.findById(tagId).map { existingTag ->
            existingTag.title = updateTag.title ?: existingTag.title
            existingTag.tasks = updateTag.tasks_id?.let { taskRepository.findAllById(it).toSet() } ?: existingTag.tasks
            ResponseEntity.ok().body(tagRepository.save(existingTag))
        }.orElse(
            ResponseEntity.notFound().build()
        )
    }

    @DeleteMapping("/tag/{id}")
    @Operation(summary = "Удаление тега.")
    fun deleteTagById(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long
    ): ResponseEntity<String> {
        return tagRepository.findById(tagId).map { tag ->
            tagRepository.delete(tag)
            ResponseEntity.ok().body("Тег удален")
        }.orElse(ResponseEntity.notFound().build())
    }

}