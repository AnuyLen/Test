package org.example.test.repository

import org.example.test.entity.TagEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Определяет получение доступа к тегам.
 */
@Repository
interface TagRepository: JpaRepository<TagEntity, Long> {

    /**
     * Получение всех тегов, у которых есть задачи.
     *
     * @return Список всех тегов, у которых есть задачи, отсиртированные по идентификатору в порядке возрастания.
     */
    fun findByTasksIsNotNullOrderByIdTagAsc(): List<TagEntity>

    /**
     * Получение всех тегов, у которых есть задачи.
     *
     * @return Список всех тегов, у которых есть задачи, отсиртированные по идентификатору в порядке убывания.
     */
    fun findByTasksIsNotNullOrderByIdTagDesc(): List<TagEntity>

    /**
     * Получение тега по заголовку.
     *
     * @param title Заголовок тега.
     * @return [TagEntity] Информация о найденном теге.
     */
    fun findByTitle(title: String): TagEntity?

}