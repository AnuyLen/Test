package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.test.entity.TaskEntity
import org.example.test.model.Task
import org.example.test.repository.TagRepository
import org.example.test.repository.TaskRepository
import org.example.test.repository.TypeRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    //Get all tasks
    @GetMapping("/tasks")
    @Operation(summary = "Получить информацию о всех задач.")
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
            "asc" -> taskRepository.findByOrderByTypePriorityAsc(PageRequest.of(offset, limit))
            else -> taskRepository.findAll(PageRequest.of(offset, limit))
        }

    //Get all tasks by date
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
            "asc" -> taskRepository.findByDateOrderByTypePriorityAsc(date, PageRequest.of(offset, limit))
            else -> taskRepository.findByDate(date, PageRequest.of(offset, limit))
        }
    }

    //Create new task
    @PostMapping("/task")
    @Operation(summary = "Создание новой задачи.")
    fun createNewTask(
        @Parameter(description = "Информация о задаче.")
        @Valid @RequestBody task: Task
    ): ResponseEntity<*> {
        val type = task.id_type?.let { typeRepository.findById(it) }?.orElse(null)
        return when {
            task.date == null -> ResponseEntity.badRequest().body("Введите дату!")
            task.description == null -> ResponseEntity.badRequest().body("Введите описание!")
            type == null -> ResponseEntity.badRequest().body("Такого типа не существует!")
            else -> {
                val taskEntity: TaskEntity
                val tags = task.tags_id?.let { tagRepository.findAllById(it) }?.toSet()
                taskEntity = task.toEntity(type, tags)
                return if (taskEntity.date!! >= LocalDate.now()) {
                    ResponseEntity.ok().body(taskRepository.save(taskEntity))
                } else {
                    ResponseEntity.badRequest().body("Выбранная дата меньше текущей")
                }
            }
        }
    }

    @Transactional
    @PutMapping("/task/{id}")
    @Operation(summary = "Изменение задачи.")
    fun updateTagById(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "id") taskId: Long,
        @Parameter(description = "Информация о задаче.")
        @Valid @RequestBody newTask: Task
    ): ResponseEntity<*> {
        val type = newTask.id_type?.let { typeRepository.findById(it) }?.orElse(null)
        return when {
            newTask.date == null -> ResponseEntity.badRequest().body("Введите дату!")
            newTask.date < LocalDate.now() ->
                ResponseEntity.badRequest().body("Дата должна быть не меньше текущей!")
            type == null -> ResponseEntity.badRequest().body("Такого типа не существует!")
            newTask.description.isNullOrEmpty() -> ResponseEntity.badRequest().body("Введите описание!")
            else -> {
                val updateTaskEntity = TaskEntity(
                    idTask = taskId,
                    type = type,
                    description = newTask.description,
                    date = newTask.date,
                    tags = newTask.tags_id?.let { tagRepository.findAllById(it) }?.toSet()
                )
                ResponseEntity.ok().body(taskRepository.save(updateTaskEntity))
            }
        }
    }

    @DeleteMapping("/task/{id}")
    @Operation(summary = "Удаление задачи.")
    fun deleteTaskById(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "id") taskId: Long
    ): ResponseEntity<String> {
        return taskRepository.findById(taskId).map { task ->
            taskRepository.delete(task)
            ResponseEntity.ok().body("Задача удалена")
        }.orElse(ResponseEntity.notFound().build())
    }
}