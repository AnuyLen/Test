package org.example.test.controller

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
class FileController(private val fileRepository: FileRepository,
                     private val taskRepository: TaskRepository
) {

    private val fileDirectory: String = "C:\\Users\\olkov\\files\\"

    @PostMapping("/file/{taskId}")
    fun uploadFileToFIleSystem(@PathVariable taskId: Long, @RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        var message = ""
        if (fileRepository.findByTaskIdTask(taskId) == null) {
            if (!file.isEmpty /*!= null*/) {
                try {
                    taskRepository.findById(taskId).map { task ->
                        var filePath: String = fileDirectory + taskId.toString() + "\\"
                        Files.createDirectories(Paths.get(filePath))
                        filePath += file.originalFilename
                        val fileEntity = FileEntity(null, file.contentType, filePath, file.originalFilename, task)
                        fileRepository.save(fileEntity)
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

    @PutMapping("file/{taskId}")
    fun updateFile(@PathVariable(value = "taskId") taskId: Long,
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
    fun downloadFileFromFileSystem(@PathVariable taskId: Long): ResponseEntity<ByteArray> {
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
    fun deleteFile(@PathVariable(value = "taskId") taskId: Long): ResponseEntity<Void> {
        return fileRepository.findByTaskIdTask(taskId)?.run {
            File(fileDirectory + taskId.toString()).deleteRecursively()
            fileRepository.delete(this)
            ResponseEntity.ok().build()
        } ?: ResponseEntity.notFound().build()
    }
}