package org.example.test.repository

import org.example.test.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository: JpaRepository<TagEntity, Long> {
    fun findByTasksIsNotNull(): List<TagEntity>

    fun findByTitle(title: String): TagEntity?

}