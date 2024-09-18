package org.example.test.repository

import org.example.test.entity.TypeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TypeRepository: JpaRepository<TypeEntity, Long> {
    fun findByOrderByPriorityAsc(): List<TypeEntity>

    fun findByOrderByPriorityDesc(): List<TypeEntity>
}