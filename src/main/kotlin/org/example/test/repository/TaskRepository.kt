package org.example.test.repository

import org.example.test.entity.TaskEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TaskRepository: JpaRepository<TaskEntity, Long> {

    fun findByDateOrderByTypePriorityAsc(date: LocalDate, pageable: Pageable): Page<TaskEntity>

    fun findByDateOrderByTypePriorityDesc(date: LocalDate, pageable: Pageable): Page<TaskEntity>


    fun findByOrderByTypePriorityAsc(pageable: Pageable): Page<TaskEntity>

    fun findByOrderByTypePriorityDesc(pageable: Pageable): Page<TaskEntity>

}