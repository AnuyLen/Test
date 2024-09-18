package org.example.test.controller

import org.example.test.entity.TaskEntity
import org.example.test.repository.TaskRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class TaskController(private val taskRepository: TaskRepository) {

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



    @DeleteMapping("/task/{id}")
    fun deleteTaskById(@PathVariable(value = "id") taskId: Long): ResponseEntity<String> {
        return taskRepository.findById(taskId).map { task ->
            taskRepository.delete(task)
            ResponseEntity.ok().body("Задача удалена")
        }.orElse(ResponseEntity.notFound().build())
    }
}