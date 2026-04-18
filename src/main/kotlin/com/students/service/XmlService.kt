package com.students.service

import com.students.model.Students
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Marshaller
import org.springframework.stereotype.Service
import java.io.InputStream
import java.io.StringWriter

@Service
class XmlService {

    private val context: JAXBContext = JAXBContext.newInstance(Students::class.java)

    fun unmarshal(inputStream: InputStream): Students =
        context.createUnmarshaller().unmarshal(inputStream) as Students

    fun marshal(students: Students): String {
        val writer = StringWriter()
        context.createMarshaller().apply {
            setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
            setProperty(Marshaller.JAXB_ENCODING, "UTF-8")
        }.marshal(students, writer)
        return writer.toString()
    }
}
