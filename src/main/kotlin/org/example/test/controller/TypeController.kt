package org.example.test.controller

import org.example.test.entity.TypeEntity
import org.example.test.repository.TypeRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class TypeController(private val typeRepository: TypeRepository) {

    @GetMapping("/types")
    fun getAllTasks(@RequestParam("sort") sortType: String?): List<TypeEntity> =
        if (sortType == "desc") {
            typeRepository.findByOrderByPriorityDesc()
        } else {
            typeRepository.findByOrderByPriorityAsc()
        }

}