package org.example.test.repository

import org.example.test.entity.TypeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Определяет получение доступа к типам задач.
 */
@Repository
interface TypeRepository: JpaRepository<TypeEntity, Long> {

    /**
     * Получение всех типов задач с сортировкой по приоритету в порядке возрастания.
     *
     * @return Список типов задач.
     */
    fun findByOrderByPriorityAsc(): List<TypeEntity>

    /**
     * Получение всех типов задач с сортировкой по приоритету в порядке убывания.
     *
     * @return Список типов задач.
     */
    fun findByOrderByPriorityDesc(): List<TypeEntity>
}