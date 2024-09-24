package org.example.test

import org.example.test.entity.TypeEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest(
    classes = [TestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TypeControllerTests {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Get all types`() {
        val requestEntity: HttpEntity<*> = HttpEntity<Any>(null, null)
        val response: ResponseEntity<List<TypeEntity>> = restTemplate.exchange(
            "/api/types",
            HttpMethod.GET,
            requestEntity,
            object : ParameterizedTypeReference<List<TypeEntity>>() {}
        )
        assertEquals(HttpStatus.OK, response.statusCode)
    }
}