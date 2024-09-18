package org.example.test.controller

import jakarta.validation.Valid
import org.example.test.entity.TaskEntity
import org.example.test.model.Task
import org.example.test.repository.TagRepository
import org.example.test.repository.TaskRepository
import org.example.test.repository.TypeRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class TaskController(private val taskRepository: TaskRepository,
                     private val tagRepository: TagRepository,
                     private val typeRepository: TypeRepository
) {

    //Get all tasks
    @GetMapping("/tasks")
    fun getAllTasks(@RequestParam("sort") sortType: String?): List<TaskEntity> =
        when (sortType){
            "desc" -> taskRepository.findByOrderByTypePriorityDesc()
            "asc" -> taskRepository.findByOrderByTypePriorityAsc()
            else -> taskRepository.findAll()
        }

    //Get all tasks by date
    @GetMapping("/task/{date}")
    fun getTasksByDate(@PathVariable(value = "date") dateString: String,
                       @RequestParam("sort") sortType: String?): List<TaskEntity> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date: LocalDate = LocalDate.parse(dateString, formatter)
        return when (sortType){
            "desc" -> taskRepository.findByDateOrderByTypePriorityDesc(date)
            else -> taskRepository.findByDateOrderByTypePriorityAsc(date)
        }
    }

    //Create new task
    @PostMapping("/task")
    fun createNewTask(@Valid @RequestBody task: Task): ResponseEntity<*> {
        val typeNotNull = task.id_type == null
        val descriptionNotNull = task.description == null
        val dateNotNull = task.date == null
        return when {
            typeNotNull || descriptionNotNull || dateNotNull -> {
                ResponseEntity.badRequest().body("Проверьте введенные данные: тип, описание и дату")
            }
            else -> {
                val taskEntity: TaskEntity
                val tags = task.tags_id?.let { tagRepository.findAllById(it) }?.toSet()
                val type = typeRepository.findById(task.id_type!!)
                if (type.isEmpty) {
                    ResponseEntity.badRequest().body("Тип не найден")
                } else {
                    taskEntity = task.toEntity(type.get(), tags)
                    return if (taskEntity.date!! >= LocalDate.now()) {
                        ResponseEntity.ok().body(taskRepository.save(taskEntity))
                    } else {
                        ResponseEntity.badRequest().body("Выбранная дата меньше текущей")
                    }
                }
            }
        }
    }

    @PutMapping("/task/{id}")
    fun updateTagById(@PathVariable(value = "id") taskId: Long,
                      @Valid @RequestBody newTask: Task
    ): ResponseEntity<*> {
        return if (newTask.date != null && newTask.date < LocalDate.now()) {
            ResponseEntity.badRequest().body("Дата должна быть не меньше текущей!")
        } else {
            taskRepository.findById(taskId).map { existingTask ->
                val updateTaskEntity: TaskEntity = existingTask.apply {
                    type = newTask.id_type?.let { typeRepository.findById(it) }?.get() ?: existingTask.type
                    description = newTask.description ?: existingTask.description
                    date = newTask.date ?: existingTask.date
                    tag = newTask.tags_id?.let { tagRepository.findAllById(it) }?.toSet() ?: existingTask.tag
                }
                ResponseEntity.ok().body(taskRepository.save(updateTaskEntity))
            }.orElse(ResponseEntity.notFound().build())
        }
    }

    @DeleteMapping("/task/{id}")
    fun deleteTaskById(@PathVariable(value = "id") taskId: Long): ResponseEntity<String> {
        return taskRepository.findById(taskId).map { task ->
            taskRepository.delete(task)
            ResponseEntity.ok().body("Задача удалена")
        }.orElse(ResponseEntity.notFound().build())
    }
}