package xml

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import model.Students
import java.io.File
import java.io.StringWriter

/**
 * Выполняет операции маршалинга и анмаршалинга XML через JAXB.
 */
object XmlProcessor {

    private val context: JAXBContext = JAXBContext.newInstance(Students::class.java)

    /**
     * Анмаршалинг (десериализация) XML-файла в объект [Students].
     *
     * @param file  Исходный XML-файл.
     * @return      Разобранный объект [Students].
     */
    fun unmarshal(file: File): Students {
        val unmarshaller = context.createUnmarshaller()
        return unmarshaller.unmarshal(file) as Students
    }

    /**
     * Маршалинг (сериализация) объекта [Students] в XML-строку.
     *
     * @param students  Объект [Students] для сериализации.
     * @return          Форматированная XML-строка.
     */
    fun marshal(students: Students): String {
        val writer = StringWriter()
        createMarshaller().marshal(students, writer)
        return writer.toString()
    }

    /**
     * Маршалинг (сериализация) объекта [Students] с записью в файл.
     *
     * @param students  Объект [Students] для сериализации.
     * @param file      Целевой выходной файл.
     */
    fun marshalToFile(students: Students, file: File) {
        createMarshaller().marshal(students, file)
    }

    private fun createMarshaller(): Marshaller =
        context.createMarshaller().apply {
            setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
        }
}
