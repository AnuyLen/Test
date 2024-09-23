package org.example.test.repository

import org.example.test.entity.TaskEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Определяет получение доступа к задачам.
 */
@Repository
interface TaskRepository: JpaRepository<TaskEntity, Long> {

    /**
     * Получение всех задач за заданную дату с сортировкой по приоритету в порядке возрастания.
     *
     * @param date Дата.
     * @param pageable Параметры [PageRequest.of] для разбиения на страницы.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    fun findByDateOrderByTypePriorityAsc(date: LocalDate, pageable: Pageable): Page<TaskEntity>

    /**
     * Получение всех задач за заданную дату с сортировкой по приоритету в порядке убывания.
     *
     * @param date Дата.
     * @param pageable Параметры [PageRequest.of] для разбиения на страницы.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    fun findByDateOrderByTypePriorityDesc(date: LocalDate, pageable: Pageable): Page<TaskEntity>

    /**
     * Получение всех задач с сортировкой по приоритету в порядке возрастания.
     *
     * @param pageable Параметры [PageRequest.of] для разбиения на страницы.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    fun findByOrderByTypePriorityAsc(pageable: Pageable): Page<TaskEntity>

    /**
     * Получение всех задач с сортировкой по приоритету в порядке убывания.
     *
     * @param pageable Параметры [PageRequest.of] для разбиения на страницы.
     * @return Страница, содержащая список задач - [Page]<[TaskEntity]>.
     */
    fun findByOrderByTypePriorityDesc(pageable: Pageable): Page<TaskEntity>

}