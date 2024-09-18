package org.example.test.controller

import org.example.test.entity.TaskEntity
import org.example.test.repository.TaskRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api")
class TaskController(private val taskRepository: TaskRepository) {

    //Get all tasks
    @GetMapping("/tasks")
    fun getAllTasks(@RequestParam("sort") sortType: String?): List<TaskEntity> =
        taskRepository.findAll()

    //Get all tasks by date
    @GetMapping("/task/{date}")
    fun getTasksByDate(@PathVariable(value = "date") dateString: String): TaskEntity {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date: LocalDate = LocalDate.parse(dateString, formatter)
        return taskRepository.findByDate(date)
    }

}