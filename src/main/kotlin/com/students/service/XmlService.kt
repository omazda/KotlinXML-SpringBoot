package com.students.service

import com.students.model.Students
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.StringWriter

/**
 * Сервис для маршалинга и анмаршалинга XML через JAXB.
 */
@Service
class XmlService {

    private val context: JAXBContext = JAXBContext.newInstance(Students::class.java)

    /**
     * Анмаршалинг (десериализация) входного потока в объект [Students].
     *
     * @param inputStream  Поток с содержимым XML-файла.
     * @return             Разобранный объект [Students].
     */
    fun unmarshal(inputStream: InputStream): Students =
        context.createUnmarshaller().unmarshal(inputStream) as Students

    /**
     * Маршалинг (сериализация) объекта [Students] в XML-строку.
     *
     * @param students  Объект [Students] для сериализации.
     * @return          Форматированная XML-строка.
     */
    fun marshal(students: Students): String {
        val writer = StringWriter()
        context.createMarshaller().apply {
            setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
        }.marshal(students, writer)
        return writer.toString()
    }
}
