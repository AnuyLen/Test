package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.test.entity.TaskEntity
import org.example.test.exception.NotFoundException
import org.example.test.model.Response
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
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию.",
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
     * @param dateString Запланированная дата.
     * @param sortType Тип сортировки: asc - по возрастанию, desc - по убыванию. По умолчанию asc.
     * @param offset Номер страницы.
     * @param limit Количество элементов на странице.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    @GetMapping("/task/{date}")
    @Operation(summary = "Получить информацию о всех задачах, за заданную дату.")
    fun getTasksByDate(
        @Parameter(description = "Запланированная дата.")
        @PathVariable(value = "date") dateString: String,
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию.",
            example = "asc")
        @RequestParam("sort") sortType: String?,
        @Parameter(description = "Номер страницы.")
        @RequestParam(value = "offset", defaultValue = "0") offset: Int,
        @Parameter(description = "Количество элементов на странице.")
        @RequestParam(value = "limit", defaultValue = "10") limit: Int
    ): Page<TaskEntity> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date: LocalDate = LocalDate.parse(dateString, formatter)
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
        val type = task.id_type?.let { typeRepository.findByIdOrNull(it) }
            ?: throw NotFoundException("Тип задачи с идентификатором ${task.id_type} не найден!")
        val taskEntity: TaskEntity
        val tags = task.tags_id?.let { tagRepository.findAllById(it) }?.toSet()
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
        val type = newTask.id_type?.let { typeRepository.findByIdOrNull(it) }
            ?: throw NotFoundException("Тип задачи с идентификатором ${newTask.id_type} не найден!")
        val updateTaskEntity = TaskEntity(
            idTask = taskId,
            type = type,
            name = newTask.name,
            description = newTask.description,
            date = newTask.date,
            tags = newTask.tags_id?.let { tagRepository.findAllById(it) }?.toSet()
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
        /*@Valid */@RequestBody updateTask: Task
    ): TaskEntity {
        val existingTask = taskRepository.findByIdOrNull(taskId)
            ?: throw NotFoundException("Задача с идентификатором $taskId не найдена!")
        existingTask.date = updateTask.date ?: existingTask.date
        existingTask.type = updateTask.id_type?.let { typeRepository.findById(it).get() } ?: existingTask.type
        existingTask.name = updateTask.name ?: existingTask.name
        existingTask.description = updateTask.description ?: existingTask.description
        existingTask.tags = updateTask.tags_id?.let { tagRepository.findAllById(it).toSet() } ?: existingTask.tags
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