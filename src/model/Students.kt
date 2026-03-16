package model

import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

/**
 * Корневой элемент XML — список студентов.
 * Соответствует тегу <students> в XML-файле.
 *
 * @property students  Список объектов [Student].
 */
@XmlRootElement(name = "students")
@XmlAccessorType(XmlAccessType.FIELD)
class Students {

    @XmlElement(name = "student")
    var students: MutableList<Student> = mutableListOf()

    override fun toString(): String =
        "Students(students=$students)"
}
