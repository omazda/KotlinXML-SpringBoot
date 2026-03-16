package com.students.web

import com.students.service.StudentService
import com.students.service.XmlService
import jakarta.xml.bind.UnmarshalException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

/**
 * HTTP-контроллер: отображение таблицы импортов и загрузка новых XML-файлов.
 */
@Controller
class ImportController(
    private val xmlService: XmlService,
    private val studentService: StudentService
) {

    /**
     * Главная страница — таблица всех импортов.
     */
    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("imports", studentService.findAllImports())
        return "index"
    }

    /**
     * Обработка загрузки XML-файла: анмаршалинг → сохранение в БД → редирект на главную.
     */
    @PostMapping("/upload")
    fun upload(
        @RequestParam("file") file: MultipartFile,
        redirectAttributes: RedirectAttributes
    ): String {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("error", "Файл не выбран")
            return "redirect:/"
        }

        return try {
            val students = xmlService.unmarshal(file.inputStream)
            val importId = studentService.save(students, file.originalFilename ?: "unknown.xml")
            redirectAttributes.addFlashAttribute(
                "success",
                "Импорт #$importId завершён: ${students.students.size} студент(ов) из '${file.originalFilename}'"
            )
            "redirect:/"
        } catch (e: UnmarshalException) {
            val msg = e.linkedException?.message ?: e.message
            redirectAttributes.addFlashAttribute("error", "Ошибка парсинга XML: $msg")
            "redirect:/"
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: ${e.message}")
            "redirect:/"
        }
    }
}
