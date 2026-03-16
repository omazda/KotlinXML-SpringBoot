import db.StudentRepository
import jakarta.xml.bind.UnmarshalException
import xml.XmlProcessor
import java.io.File
import kotlin.system.exitProcess

/**
 * Точка входа приложения.
 *
 * Использование: java -classpath "app.jar;<libs>" MainKt <input-xml> [output-xml]
 *
 * @param args args[0] — путь к входному XML-файлу.
 *             args[1] — (необязательно) путь к выходному XML-файлу.
 *                       По умолчанию: <имя>_output.xml рядом с входным файлом.
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        System.err.println("Использование: MainKt <input-xml> [output-xml]")
        exitProcess(1)
    }

    val xmlFile = File(args[0])
    if (!xmlFile.exists()) {
        System.err.println("Ошибка: файл не найден: ${xmlFile.absolutePath}")
        exitProcess(1)
    }
    if (!xmlFile.isFile) {
        System.err.println("Ошибка: '${xmlFile.absolutePath}' не является файлом.")
        exitProcess(1)
    }

    // --- Анмаршалинг ---
    println("=== АНМАРШАЛИНГ ===")
    val students = try {
        XmlProcessor.unmarshal(xmlFile)
    } catch (e: UnmarshalException) {
        val message = e.linkedException?.message ?: e.message
        System.err.println("Ошибка разбора XML: $message")
        exitProcess(1)
    }

    if (students.students.isEmpty()) {
        println("В XML не найдено студентов.")
    } else {
        students.students.forEach { println(" - $it") }
    }

    // --- Маршалинг в строку ---
    println("\n=== МАРШАЛИНГ (в строку) ===")
    val xml = XmlProcessor.marshal(students)
    println(xml)

    // --- Маршалинг в файл ---
    val outputFile = if (args.size >= 2) {
        File(args[1]).also { it.parentFile?.mkdirs() }
    } else {
        File(xmlFile.parent ?: ".", xmlFile.nameWithoutExtension + "_output.xml")
    }
    try {
        XmlProcessor.marshalToFile(students, outputFile)
        println("=== Сохранено в: ${outputFile.absolutePath} ===")
    } catch (e: Exception) {
        System.err.println("Ошибка сохранения XML: ${e.message}")
        exitProcess(1)
    }

    // --- Сохранение в PostgreSQL ---
    println("\n=== СОХРАНЕНИЕ В БАЗУ ДАННЫХ ===")
    try {
        StudentRepository.save(students, xmlFile.name)
    } catch (e: Exception) {
        System.err.println("Ошибка базы данных: ${e.message}")
        exitProcess(1)
    }
}
