package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import jakarta.validation.Valid
import org.example.test.entity.TagEntity
import org.example.test.exception.AlreadyExistsException
import org.example.test.exception.NotFoundException
import org.example.test.model.Response
import org.example.test.model.Tag
import org.example.test.repository.TagRepository
import org.example.test.repository.TaskRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.web.bind.annotation.*

/**
 *
 * Обрабатывает запросы связанные с тегами.
 *
 * @property tagRepository Интерфейс [TagRepository].
 * @property taskRepository Интерфейс [TaskRepository].
 */
@RestController
@RequestMapping("/api")
@io.swagger.v3.oas.annotations.tags.Tag(
    name = "Теги.",
    description = "Все методы для работы с тегами.",
)
class TagController(private val tagRepository: TagRepository,
                    private val taskRepository: TaskRepository
) {

    /**
     * Получение списка всех тегов, у которых есть задачи.
     *
     * @return Список тегов - [List]<[TagEntity]>.
     */
    @GetMapping("/tags")
    @Operation(summary = "Получить информацию о всех тегах, у которых есть задачи.")
    fun getAllTags(): List<TagEntity> {
        return tagRepository.findByTasksIsNotNull()
    }

    /**
     * Получение тега по идентификатору с задачами, сортированными по уровню приоритета.
     *
     * @param tagId Идентификатор тега.
     * @param sortType Тип сортировки: asc - по возрастанию, desc - по убыванию. По умолчанию asc.
     * @return Тег - [TagEntity].
     */
    @GetMapping("/tag/{id}")
    @Operation(summary = "Получить информацию о теге по id с задачами, сортированными по уровню приоритета.")
    fun getTagByID(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long,
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию.",
            example = "asc")
        @RequestParam("sort") sortType: String?
    ): TagEntity {
        val tag = tagRepository.findByIdOrNull(tagId)
            ?: throw NotFoundException("Тег с идентификатором $tagId не найден!")
        when (sortType){
            "desc" -> tag.tasks = tag.tasks?.sortedByDescending { it.type?.priority }?.toSet()
            else -> tag.tasks = tag.tasks?.sortedBy { it.type?.priority }?.toSet()
        }
        return tag
    }

    /**
     * Создание нового тега.
     *
     * @param tag Информация о теге.
     * @return [TagEntity] - Созданный тег.
     */
    @PostMapping("/tag")
    @Operation(summary = "Создание нового тега.")
    fun createNewTag(
        @Parameter(description = "Информация о теге.")
        @Valid @RequestBody tag: Tag
    ): TagEntity {
        if (tag.title?.let { tagRepository.findByTitle(it) } != null) {
            throw AlreadyExistsException("Тег с названием ${tag.title} уже существует!")
        }
        val tasks = tag.tasks_id?.let { taskRepository.findAllById(it) }?.toSet()
        val tagEntity = tag.toEntity(tasks)
        return tagRepository.save(tagEntity)
    }

    /**
     * Изменение тега по идентификатору или создание нового тега, если он не был найден.
     *
     * @param tagId Идентификатор тега.
     * @param newTag Информация о теге.
     * @return [TagEntity] - Измененный или созданный тег.
     */
    @PutMapping("/tag/{id}")
    @Operation(summary = "Изменение информации о теге или создание нового тега, если он не найден.")
    fun updateByIdOrCreateTag(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long,
        @Parameter(description = "Информация о теге.")
        @Valid @RequestBody newTag: Tag
    ): TagEntity {
        if (newTag.title?.let { tagRepository.findByTitle(it) } != null) {
            throw AlreadyExistsException("Тег с названием ${newTag.title} уже существует!")
        }
        val updateTagEntity = TagEntity(
            idTag = tagId,
            title = newTag.title,
            tasks = newTag.tasks_id?.let { taskRepository.findAllById(it).toSet() }
        )
        return tagRepository.save(updateTagEntity)
    }

    /**
     * Изменение тега по идентификатору.
     *
     * @param tagId Идентификатор тега.
     * @param updateTag Информация о теге.
     * @return [TagEntity] - Измененный тег.
     */
    @PatchMapping("/tag/{id}")
    @Operation(summary = "Изменение информации о теге.")
    fun updateTagById(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long,
        @Parameter(description = "Информация о теге.")
        @RequestBody updateTag: Tag
    ): TagEntity {
        val existingTag = tagRepository.findByIdOrNull(tagId)
            ?: throw NotFoundException("Тег с идентификатором $tagId не найден!")
        val findTagByTitle = updateTag.title?.let { tagRepository.findByTitle(it) }
        if (findTagByTitle != null && findTagByTitle.idTag != existingTag.idTag) {
            throw AlreadyExistsException("Тег с названием ${updateTag.title} уже существует!")
        }
        existingTag.title = updateTag.title ?: existingTag.title
        existingTag.tasks = updateTag.tasks_id?.let { taskRepository.findAllById(it).toSet() } ?: existingTag.tasks
        return tagRepository.save(existingTag)
    }

    /**
     * Удаление тега по идентификатору.
     *
     * @param tagId Идентификатор тега.
     * @return [Response] - Собщение, что тег был удален.
     */
    @DeleteMapping("/tag/{id}")
    @Operation(summary = "Удаление тега.")
    fun deleteTagById(
        @Parameter(description = "id тега")
        @PathVariable(value = "id") tagId: Long
    ): Response {
        val tag = tagRepository.findByIdOrNull(tagId)
            ?: throw NotFoundException("Тег с идентификатором $tagId не найден!")
        tagRepository.delete(tag)
        return Response("Тег удален")
    }

}