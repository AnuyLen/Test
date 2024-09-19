package org.example.test.repository

import org.example.test.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository: JpaRepository<FileEntity, Long> {
    fun findByTaskIdTask(taskId: Long): FileEntity?
}