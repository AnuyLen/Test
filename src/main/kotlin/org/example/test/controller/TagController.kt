package org.example.test.controller

import org.example.test.entity.TagEntity
import org.example.test.repository.TagRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class TagController(private val tagRepository: TagRepository) {

    //Get all tags
    @GetMapping("/tags")
    fun getAllTags(): List<TagEntity> {
        return tagRepository.findByTasksIsNotNull()
    }

    //Get tag by id
    @GetMapping("/tag/{id}")
    fun getTagByID(@PathVariable(value = "id") tagId: Long): ResponseEntity<TagEntity> {
        return tagRepository.findById(tagId).map { tag ->
            ResponseEntity.ok(tag)
        }.orElse(ResponseEntity.notFound().build())
    }


}