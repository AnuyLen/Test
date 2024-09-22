package org.example.test.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.tags.Tag
import org.example.test.entity.FileEntity
import org.example.test.repository.FileRepository
import org.example.test.repository.TaskRepository
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

@RestController
@RequestMapping("/api")
@Tag(
    name = "Файлы.",
    description = "Все методы для работы с файлами.",
)
class FileController(private val fileRepository: FileRepository,
                     private val taskRepository: TaskRepository
) {

    private val fileDirectory: String = "C:\\Users\\olkov\\files\\"

    @Transactional
    @PostMapping("/file/{taskId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Добавление файла для задачи.")
    fun uploadFileToFIleSystem(
        @Parameter(description = "id задачи.")
        @PathVariable taskId: Long,
        @Parameter(description = "Файл.", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)])
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<String> {
        var message = ""
        if (fileRepository.findByTaskIdTask(taskId) == null) {
            if (!file.isEmpty /*!= null*/) {
                try {
                    taskRepository.findById(taskId).map { task ->
                        var filePath: String = fileDirectory + taskId.toString() + "\\"
                        filePath += file.originalFilename
                        val fileEntity = FileEntity(null, file.contentType, filePath, file.originalFilename, task)
                        fileRepository.save(fileEntity)
                        Files.createDirectories(Paths.get(filePath))
                        file.transferTo(File(filePath))
                        message = "Файл загружен"
                    }.orElseGet {
                        message = "Задача не найдена"
                    }
                } catch (e: IOException) {
                    message = "Ошибка: " + e.localizedMessage
                }
                return ResponseEntity.ok().body(message)
            } else {
                message = "Выберите файл для загрузки"
                return ResponseEntity.badRequest().body(message)
            }
        } else{
            message = "Для этой задачи файл уже добавлен"
            return ResponseEntity.badRequest().body(message)
        }
    }

    @PutMapping("file/{taskId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Изменение файла задачи.")
    fun updateFile(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "taskId") taskId: Long,
        @Parameter(description = "Файл.", content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)])
        @RequestParam("file") newFile: MultipartFile
    ): ResponseEntity<String> {
        val file = fileRepository.findByTaskIdTask(taskId)
        if (file != null && !newFile.isEmpty) {
            var message: String
            try {
                Files.delete(Paths.get(file.path))
                val filePath = fileDirectory + taskId.toString() + "\\" + newFile.originalFilename
                file.apply {
                    type = newFile.contentType
                    path = filePath
                    name = newFile.originalFilename
                }
                newFile.transferTo(File(filePath))
                fileRepository.save(file)
                message = "Новый файл загружен"
            } catch (e: IOException) {
                message = "Ошибка: " + e.localizedMessage
            }
            return ResponseEntity.ok().body(message)
        } else {
            return ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/file/{taskId}")
    @Operation(summary = "Получение файла по id задачи.")
    fun downloadFileFromFileSystem(
        @Parameter(description = "id задачи.")
        @PathVariable taskId: Long
    ): ResponseEntity<ByteArray> {
        val file = fileRepository.findByTaskIdTask(taskId)
        return if (file != null) {
            val filePath: String = file.path
            val files = Files.readAllBytes(File(filePath).toPath())
            return ResponseEntity.ok().contentType(MediaType.valueOf(file.type ?: "image/png")).body(files)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/file/{taskId}")
    @Operation(summary = "Удаление файла по id задачи.")
    fun deleteFile(
        @Parameter(description = "id задачи.")
        @PathVariable(value = "taskId") taskId: Long
    ): ResponseEntity<String> {
        return fileRepository.findByTaskIdTask(taskId)?.run {
            File(fileDirectory + taskId.toString()).deleteRecursively()
            fileRepository.delete(this)
            ResponseEntity.ok().body("Файл удален")
        } ?: ResponseEntity.notFound().build()
    }
}