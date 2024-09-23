package org.example.test.repository

import org.example.test.entity.FileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Определяет получение доступа к задачам.
 */
@Repository
interface FileRepository: JpaRepository<FileEntity, Long> {

    /**
     * Получение файла по идентификатору задачи.
     *
     * @param taskId Идентификатор задачи.
     * @return [FileEntity] Информация о файле.
     */
    fun findByTaskIdTask(taskId: Long): FileEntity?
}