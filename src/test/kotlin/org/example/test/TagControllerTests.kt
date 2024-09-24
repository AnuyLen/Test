package org.example.test

import org.example.test.entity.TagEntity
import org.example.test.model.error.Response
import org.example.test.model.Tag
import org.example.test.model.error.ValidationErrorResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


@SpringBootTest(
    classes = [TestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TagControllerTests(
) {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Get tag by id not found`(){
        val result = restTemplate.getForEntity("/api/tag/99", Response::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.NOT_FOUND, result.statusCode)
    }

    @Test
    fun `Get tag by id`(){
        val result = restTemplate.getForEntity("/api/tag/1", TagEntity::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
    }

    @Test
    fun `Post tag, title exists`(){
        val result = restTemplate.postForEntity("/api/tag", Tag("Отчет", listOf()), Response::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `Post tag, data not valid`(){
        val result = restTemplate.postForEntity("/api/tag", Tag(null, listOf()), ValidationErrorResponse::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.BAD_REQUEST, result.statusCode)
    }

    @Test
    fun `Post tag`(){
        val result = restTemplate.postForEntity("/api/tag", Tag("Тест", listOf()), TagEntity::class.java)
        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        restTemplate.delete("/api/tag/" + result.body?.idTag)
    }

    @Test
    fun `Put tag, title exists`(){
        val testTag = Tag(title = "Счета", listOf(68,69))
        val id = 1
        val response: ResponseEntity<Response> = restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.PUT,
            HttpEntity(testTag),
            Response::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Put tag, data not valid`(){
        val testTag = Tag(null, listOf(68,69))
        val id = 1
        val response: ResponseEntity<ValidationErrorResponse> = restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.PUT,
            HttpEntity(testTag),
            ValidationErrorResponse::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `Put tag`(){
        val oldTag = restTemplate.getForEntity("/api/tag/1", TagEntity::class.java)
        val testTag = Tag("Add test put", listOf(68,69))
        val id = 1
        val response: ResponseEntity<TagEntity> = restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.PUT,
            HttpEntity(testTag),
            TagEntity::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(response.body?.title, testTag.title)

        val taskIds: MutableList<Long> = mutableListOf()

        if (oldTag.body?.tasks != null) {
            for (i in oldTag.body?.tasks!!.iterator()) {
                taskIds += i.idTask!!
            }
        }

        restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.PUT,
            HttpEntity(Tag(oldTag.body?.title, taskIds)),
            TagEntity::class.java,
            id)
    }

    @Test
    fun `Put tag, tag not found`(){
        val testTag = Tag("Test put", listOf(68,69))
        val id = 99
        val response: ResponseEntity<TagEntity> = restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.PUT,
            HttpEntity(testTag),
            TagEntity::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(response.body?.title, testTag.title)

        restTemplate.delete("/api/tag/" + response.body?.idTag)
    }

    @Test
    fun `Delete tag`(){
        val newTag = Tag("Test delete")
        val createdTag = restTemplate.postForObject("/api/tag", newTag, TagEntity::class.java)

        restTemplate.delete("/api/tag/" + createdTag.idTag)

        val response = restTemplate.getForEntity(
            "/api/tag/" + createdTag.idTag,
            TagEntity::class.java
        )

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `Delete tag, tag not found`(){
        val testTag = Tag(title = "Счета")
        val id = 99
        val response: ResponseEntity<Response> = restTemplate.exchange(
            "/api/tag/{id}",
            HttpMethod.DELETE,
            HttpEntity(testTag),
            Response::class.java,
            id)
        assertNotNull(response)
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

}