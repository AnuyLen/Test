package org.example.test.repository

import org.example.test.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TaskRepository: JpaRepository<TaskEntity, Long> {

    fun findByDate(date: LocalDate): TaskEntity

}