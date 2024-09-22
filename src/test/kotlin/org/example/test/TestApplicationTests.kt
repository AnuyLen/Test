package org.example.test

import org.example.test.controller.TypeController
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.ApplicationContext
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestApplicationTests(
	@SpyBean
	@Autowired
	private val typeController: TypeController,
	@Autowired
	private val applicationContext: ApplicationContext
) {


	@Test
	fun `Get all types`(){
		val types = typeController.getAllTypes(null)
		assertNotNull(types, "Типы не найдены")

		for (type in types.iterator()){
			println(type.name)
		}

		val cachedTypes = typeController.getAllTypes(null)
		assertNotNull(cachedTypes, "Типы не найдены")
		for (type in cachedTypes.iterator()){
			println(type.name)
		}

	}

	@Test
	fun contextLoads() {
	}

}
