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
    fun getTagByID(@PathVariable(value = "id") tagId: Long, @RequestParam("sort") sortType: String?): ResponseEntity<TagEntity> {
        return tagRepository.findById(tagId).map { tag ->
            tag.tasks = if (sortType == "desc") {
                tag.tasks?.sortedByDescending { it.type?.priority }?.toSet()
            } else {
                tag.tasks?.sortedBy { it.type?.priority }?.toSet()
            }
            ResponseEntity.ok(tag)
        }.orElse(ResponseEntity.notFound().build())
    }


}