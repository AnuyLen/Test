package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.test.entity.TaskEntity
import org.example.test.exception.NotFoundException
import org.example.test.model.error.Response
import org.example.test.model.Task
import org.example.test.repository.TagRepository
import org.example.test.repository.TaskRepository
import org.example.test.repository.TypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 *
 * Обрабатывает запросы связанные с задачами.
 *
 * @property taskRepository Интерфейс [TaskRepository].
 * @property tagRepository Интерфейс [TagRepository].
 * @property typeRepository Интерфейс [TypeController].
 */
@RestController
@RequestMapping("/api")
@Tag(
    name = "Задачи.",
    description = "Все методы для работы с задачами.",
)
class TaskController(private val taskRepository: TaskRepository,
                     private val tagRepository: TagRepository,
                     private val typeRepository: TypeRepository
) {

    /**
     * Получение списка всех задач.
     *
     * @param sortType Тип сортировки: asc - по возрастанию, desc - по убыванию. По умолчанию asc.
     * @param offset Номер страницы.
     * @param limit Количество элементов на странице.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    @GetMapping("/tasks")
    @Operation(summary = "Получить информацию о всех задачах.")
    fun getAllTasks(
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию. По умолчанию asc.",
            example = "asc")
        @RequestParam("sort") sortType: String?,
        @Parameter(description = "Номер страницы.")
        @RequestParam(value = "offset", defaultValue = "0") offset: Int,
        @Parameter(description = "Количество элементов на странице.")
        @RequestParam(value = "limit", defaultValue = "10") limit: Int
    ): Page<TaskEntity> =
        when (sortType) {
            "desc" -> taskRepository.findByOrderByTypePriorityDesc(PageRequest.of(offset, limit))
            else -> taskRepository.findByOrderByTypePriorityAsc(PageRequest.of(offset, limit))
        }

    /**
     * Получение списка всех задач за заданную дату.
     *
     * @param dateTask Запланированная дата.
     * @param sortType Тип сортировки: asc - по возрастанию, desc - по убыванию. По умолчанию asc.
     * @param offset Номер страницы.
     * @param limit Количество элементов на странице.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    @GetMapping("/tasks/{date}")
    @Operation(summary = "Получить информацию о всех задачах, за заданную дату.")
    fun getTasksByDate(
        @Parameter(description = "Запланированная дата.")
        @PathVariable(value = "date") dateTask: String,
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию. По умолчанию asc.",
            example = "asc")
        @RequestParam("sort") sortType: String?,
        @Parameter(description = "Номер страницы.")
        @RequestParam(value = "offset", defaultValue = "0") offset: Int,
        @Parameter(description = "Количество элементов на странице.")
        @RequestParam(value = "limit", defaultValue = "10") limit: Int
    ): Page<TaskEntity> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date: LocalDate = LocalDate.parse(dateTask, formatter)
        return when (sortType){
            "desc" -> taskRepository.findByDateOrderByTypePriorityDesc(date, PageRequest.of(offset, limit))
            else -> taskRepository.findByDateOrderByTypePriorityAsc(date, PageRequest.of(offset, limit))
        }
    }

    /**
     * Создание новой задачи.
     *
     * @param task Информация о задаче.
     * @return [TaskEntity] - Созданная задача.
     */
    @PostMapping("/task")
    @Operation(summary = "Создание новой задачи.")
    fun createNewTask(
        @Parameter(description = "Информация о задаче.")
        @Valid @RequestBody task: Task
    ): TaskEntity {
        val type = task.typeId?.let { typeRepository.findByIdOrNull(it) }
            ?: throw NotFoundException("Тип задачи с идентификатором ${task.typeId} не найден!")
        val taskEntity: TaskEntity
        val tags = task.tagIds?.let { tagRepository.findAllById(it) }?.toMutableSet()
        taskEntity = task.toEntity(type, tags)
        return taskRepository.save(taskEntity)
    }

    /**
     * Изменение информации о задаче по идентификатору или создание новой задачи, если она не найдена.
     *
     * @param taskId Идентификатор задачи.
     * @param newTask Информация о задаче.
     * @return [TaskEntity] - Измененная или созданная задача.
     */
    @Transactional
    @PutMapping("/task/{id}")
    @Operation(summary = "Изменение информации о задаче или создание новой задачи, если она не найдена.")
    fun updateByIdOrCreateTask(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "id") taskId: Long,
        @Parameter(description = "Информация о задаче.")
        @Valid @RequestBody newTask: Task
    ): TaskEntity {
        val type = newTask.typeId?.let { typeRepository.findByIdOrNull(it) }
            ?: throw NotFoundException("Тип задачи с идентификатором ${newTask.typeId} не найден!")
        val updateTaskEntity = TaskEntity(
            idTask = taskId,
            type = type,
            name = newTask.name,
            description = newTask.description,
            date = newTask.date,
            tags = newTask.tagIds?.let { tagRepository.findAllById(it) }?.toMutableSet() ?: mutableSetOf()
        )
        return taskRepository.save(updateTaskEntity)
    }

    /**
     * Изменение задачи по идентификатору.
     *
     * @param taskId Идентификатор задачи.
     * @param updateTask Информация о задаче.
     * @return [TaskEntity] - Измененная задача.
     */
    @PatchMapping("/task/{id}")
    @Operation(summary = "Изменение информации о задаче.")
    fun updateTaskById(
        @Parameter(description = "id задачи")
        @PathVariable(value = "id") taskId: Long,
        @Parameter(description = "Информация о задаче.")
        @RequestBody updateTask: Task
    ): TaskEntity {
        val existingTask = taskRepository.findByIdOrNull(taskId)
            ?: throw NotFoundException("Задача с идентификатором $taskId не найдена!")
        existingTask.date = updateTask.date ?: existingTask.date
        existingTask.type = updateTask.typeId?.let { typeRepository.findById(it).get() } ?: existingTask.type
        existingTask.name = updateTask.name ?: existingTask.name
        existingTask.description = updateTask.description ?: existingTask.description
        existingTask.tags = updateTask.tagIds?.let { tagRepository.findAllById(it).toMutableSet() } ?: existingTask.tags
        return taskRepository.save(existingTask)
    }

    /**
     * Удаление задачи по идентификатору.
     *
     * @param taskId Идентификатор задачи.
     * @return [Response] - Собщение, что задача была удалена.
     */
    @DeleteMapping("/task/{id}")
    @Operation(summary = "Удаление задачи.")
    fun deleteTaskById(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "id") taskId: Long
    ): Response {
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw NotFoundException("Задача с идентификатором $taskId не найдена!")
        taskRepository.delete(task)
        return Response("Задача удалена")
    }
}