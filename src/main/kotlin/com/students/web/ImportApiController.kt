package com.students.web

import com.students.service.ImportResult
import com.students.service.ImportService
import com.students.service.ImportStudentRow
import com.students.service.ImportSummary
import com.students.service.StudentUpdatePayload
import com.students.service.XmlService
import jakarta.xml.bind.UnmarshalException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

data class ApiError(val message: String)

@RestController
@RequestMapping("/api/imports")
class ImportApiController(
    private val xmlService: XmlService,
    private val importService: ImportService
) {

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun listImports(): ImportSummary = importService.getSummary()

    @GetMapping(
        path = ["/export"],
        produces = [MediaType.APPLICATION_XML_VALUE]
    )
    fun exportAllStudents(): ResponseEntity<String> {
        val xml = xmlService.marshal(importService.exportAllStudents())
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students_export.xml\"")
            .contentType(MediaType.APPLICATION_XML)
            .body(xml)
    }

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun upload(@RequestParam("file") file: MultipartFile): ResponseEntity<Any> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(ApiError("Файл не выбран"))
        }

        return try {
            val students = xmlService.unmarshal(file.inputStream)
            val xmlPreview = xmlService.marshal(students)
            val result: ImportResult = importService.save(
                students = students,
                fileName = file.originalFilename ?: "unknown.xml",
                xmlPreview = xmlPreview
            )
            ResponseEntity.status(HttpStatus.CREATED).body(result)
        } catch (e: UnmarshalException) {
            val message = e.linkedException?.message ?: e.message ?: "Некорректный XML"
            ResponseEntity.badRequest().body(ApiError("Ошибка парсинга XML: $message"))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiError(e.message ?: "Некорректные данные"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError("Ошибка обработки импорта: ${e.message}"))
        }
    }

    @PutMapping(
        path = ["/students/{studentId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun updateStudent(
        @PathVariable studentId: Int,
        @RequestBody payload: StudentUpdatePayload
    ): ResponseEntity<Any> {
        return try {
            val updated: ImportStudentRow = importService.updateStudent(studentId, payload)
            ResponseEntity.ok(updated)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiError(e.message ?: "Некорректные данные"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError("Ошибка сохранения изменений: ${e.message}"))
        }
    }

    @DeleteMapping(path = ["/students/{studentId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteStudent(@PathVariable studentId: Int): ResponseEntity<Any> {
        return try {
            importService.deleteStudent(studentId)
            ResponseEntity.ok(mapOf("deleted" to true, "studentId" to studentId))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(ApiError(e.message ?: "Некорректные данные"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError("Ошибка удаления студента: ${e.message}"))
        }
    }
}
