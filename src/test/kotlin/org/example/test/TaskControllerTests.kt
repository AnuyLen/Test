package org.example.test

import org.example.test.entity.TaskEntity
import org.example.test.model.Task
import org.example.test.model.error.Response
import org.example.test.model.error.ValidationErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest(
    classes = [TestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerTests {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Post task, type not found`(){
        val newTask = Task(88,"Test name", "Test description", LocalDate.parse("2024-12-19"))
        val result = restTemplate.postForEntity(
            "/api/task",
            newTask,
            Response::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `Post task, name, date, and type are null`(){
        val newTask = Task(null,null, "Test description", null)
        val result = restTemplate.postForEntity(
            "/api/task",
            newTask,
            ValidationErrorResponse::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `Post task, date is less than current`(){
        val newTask = Task(1,"Test", "Test description", LocalDate.parse("2023-05-05"))
        val result = restTemplate.postForEntity(
            "/api/task",
            newTask,
            ValidationErrorResponse::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `Post task`(){
        val newTask = Task(1,"Test post", "Test description", LocalDate.parse("2025-05-05"))
        val result = restTemplate.postForEntity(
            "/api/task",
            newTask,
            TaskEntity::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)

        restTemplate.delete("/api/task/" + result.body?.idTask)
    }

    @Test
    fun `Put tag, type not found`(){
        val testTask = Task(88,"Put test name", "Test description", LocalDate.parse("2024-12-19"))
        val id = 1
        val response: ResponseEntity<Response> = restTemplate.exchange(
            "/api/task/{id}",
            HttpMethod.PUT,
            HttpEntity(testTask),
            Response::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Put tag, name, date, and type are null`(){
        val testTask = Task(null,null, null, null)
        val id = 1
        val response: ResponseEntity<ValidationErrorResponse> = restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.PUT,
            HttpEntity(testTask),
            ValidationErrorResponse::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Put tasks, date is less than current`(){
        val testTask = Task(1,"Test", "Test description", LocalDate.parse("2023-05-05"))
        val id = 1
        val response: ResponseEntity<ValidationErrorResponse> = restTemplate.exchange(
            "/api/task/{id}",
            HttpMethod.PUT,
            HttpEntity(testTask),
            ValidationErrorResponse::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Put tag`(){
        val testTask = Task(1,"Test", "Test description", LocalDate.parse("2025-05-05"))
        val id = 81
        val response: ResponseEntity<TaskEntity> = restTemplate.exchange(
            "/api/task/{id}",
            HttpMethod.PUT,
            HttpEntity(testTask),
            TaskEntity::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(response.body?.name, testTask.name)
    }

    @Test
    fun `Put tag, tag not found`(){
        val testTask = Task(1,"Test put", "Test description", LocalDate.parse("2025-05-05"))
        val id = 999
        val response: ResponseEntity<TaskEntity> = restTemplate.exchange(
            "/api/task/{id}",
            HttpMethod.PUT,
            HttpEntity(testTask),
            TaskEntity::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(response.body?.name, testTask.name)
        restTemplate.delete("/api/task/" + response.body?.idTask)
    }

    @Test
    fun `Delete tag`(){
        val testTask = Task(1,"Test put", "Test description", LocalDate.parse("2025-05-05"))
        val createdTask = restTemplate.postForObject("/api/task", testTask, TaskEntity::class.java)
        val response: ResponseEntity<Response> = restTemplate.exchange(
            "/api/task/{id}",
            HttpMethod.DELETE,
            HttpEntity(Task()),
            Response::class.java,
            createdTask.idTask)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Delete tag, tag not found`(){
        val testTask = Task(1,"Test put", "Test description", LocalDate.parse("2025-05-05"))
        val id = 99
        val response: ResponseEntity<Response> = restTemplate.exchange(
            "/api/task/{id}",
            HttpMethod.DELETE,
            HttpEntity(testTask),
            Response::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}