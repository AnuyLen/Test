package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.test.entity.TypeEntity
import org.example.test.repository.TypeRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@Tag(
    name = "Типы задач.",
    description = "Все методы для работы с типами задач.",
)
class TypeController(private val typeRepository: TypeRepository) {

    @Cacheable("types")
    @GetMapping("/types")
    @Operation(summary = "Получить информацию о всех типах задач.")
    fun getAllTypes(
        @Parameter(description = "Параметр сортировки по приоритету: asc - по возрастанию, desc - по убыванию.",
            example = "asc")
        @RequestParam("sort") sortType: String?
    ): List<TypeEntity> =
        when (sortType){
            "desc" -> typeRepository.findByOrderByPriorityDesc()
            "asc" -> typeRepository.findByOrderByPriorityAsc()
            else -> typeRepository.findAll()
        }
}