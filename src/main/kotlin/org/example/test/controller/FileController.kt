package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.test.entity.FileEntity
import org.example.test.exception.AlreadyExistsException
import org.example.test.exception.NotFoundException
import org.example.test.model.error.Response
import org.example.test.repository.FileRepository
import org.example.test.repository.TaskRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 *
 * Обрабатывает запросы связанные с задачами.
 *
 * @property fileRepository Интерфейс [FileRepository].
 * @property taskRepository Интерфейс [TaskRepository].
 */
@RestController
@RequestMapping("/api")
@Tag(
    name = "Файлы.",
    description = "Все методы для работы с файлами.",
)
class FileController(private val fileRepository: FileRepository,
                     private val taskRepository: TaskRepository
) {

    /**
     * Путь к директории с файлами.
     */
    private val fileDirectory: String = "C:\\Users\\olkov\\files\\"

    /**
     * Загрузка файла для задачи с указанным идентификатором [taskId].
     *
     * @param taskId Идентификатор задачи.
     * @param file Файл.
     * @return Загруженный файл.
     */
    @Transactional
    @PostMapping("/file/{taskId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Добавление файла для задачи.")
    fun uploadFileToFIleSystem(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "taskId") taskId: Long,
        @Parameter(description = "Файл.", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)])
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ByteArray> {
        if (fileRepository.findByTaskIdTask(taskId) != null){
            throw AlreadyExistsException("Для задачи с идентификатором $taskId файл уже добавлен!")
        }
        if (file.isEmpty){
            throw NotFoundException("Файл не найден!")
        }
        val task = taskRepository.findByIdOrNull(taskId)
            ?: throw NotFoundException("Задача с идентификатором $taskId не найдена!")
        var filePath: String = fileDirectory + taskId.toString() + "\\"
        filePath += file.originalFilename
        val fileEntity = FileEntity(null, file.contentType, filePath, file.originalFilename, task)
        fileRepository.save(fileEntity)
        Files.createDirectories(Paths.get(filePath))
        file.transferTo(File(filePath))
        val files = Files.readAllBytes(File(filePath).toPath())
        return ResponseEntity.ok().contentType(MediaType.valueOf(file.contentType ?: "image/png")).body(files)
    }

    /**
     * Изменение файла для задачи с указанным идентификатором [taskId].
     *
     * @param taskId Идентификатор задачи.
     * @param newFile Новый файл.
     * @return Новый файл.
     */
    @PutMapping("file/{taskId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Изменение файла задачи.")
    fun updateFile(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "taskId") taskId: Long,
        @Parameter(description = "Файл.", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)])
        @RequestParam("file") newFile: MultipartFile
    ): ResponseEntity<ByteArray> {
        val file = fileRepository.findByTaskIdTask(taskId) ?: FileEntity()
//            ?: throw NotFoundException("Файл для задачи с идентификатором $taskId не найден!")
        if (newFile.isEmpty) {
            throw NotFoundException("Файл не найден!")
        }
        val findTask = taskRepository.findByIdOrNull(taskId)
            ?: throw NotFoundException("Задача с идентификатором $taskId не найдена!")
        val filePath = fileDirectory + taskId.toString() + "\\" + newFile.originalFilename
        if (file.path != null){
            Files.delete(Paths.get(file.path!!))
        } else {
            Files.createDirectories(Paths.get(filePath))
//            newFile.transferTo(File(filePath))
        }
        file.apply {
            type = newFile.contentType
            path = filePath
            name = newFile.originalFilename
            task = findTask
        }
        newFile.transferTo(File(filePath))
        fileRepository.save(file)
        val files = Files.readAllBytes(File(filePath).toPath())
        return ResponseEntity.ok().contentType(MediaType.valueOf(file.type ?: "image/png")).body(files)
    }

    /**
     * Получение файла по идентификатору [taskId] задачи.
     *
     * @param taskId Идентификатор задачи.
     * @return Файл.
     */
    @GetMapping("/file/{taskId}")
    @Operation(summary = "Получение файла по id задачи.")
    fun downloadFileFromFileSystem(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "taskId") taskId: Long
    ): ResponseEntity<ByteArray> {
        val file = fileRepository.findByTaskIdTask(taskId)
            ?: throw NotFoundException("Файл для задачи с идентификатором $taskId не найден!")
        val filePath: String = file.path!!
        val files = Files.readAllBytes(File(filePath).toPath())
        return ResponseEntity.ok().contentType(MediaType.valueOf(file.type ?: "image/png")).body(files)
    }

    /**
     * Удаление файла по идентификатору [taskId] задачи.
     *
     * @param taskId Идентификатор задачи.
     * @return [Response] - Собщение, что тег был удален.
     */
    @DeleteMapping("/file/{taskId}")
    @Operation(summary = "Удаление файла по id задачи.")
    fun deleteFile(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "taskId") taskId: Long
    ): Response {
        val file = fileRepository.findByTaskIdTask(taskId)
            ?: throw NotFoundException("Файл для задачи с идентификатором $taskId не найден!")
        File(fileDirectory + taskId.toString()).deleteRecursively()
        fileRepository.delete(file)
        return Response("Файл удален.")
    }
}