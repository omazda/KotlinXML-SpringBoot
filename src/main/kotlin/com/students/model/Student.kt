package com.students.model

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlElementWrapper

/**
 * Студент с личными данными и списком навыков.
 *
 * @property firstName   Имя студента.
 * @property secondName  Фамилия студента.
 * @property skills      Список навыков (hard и soft).
 */
@XmlAccessorType(XmlAccessType.FIELD)
class Student {
    @XmlElement(name = "first_name") var firstName: String = ""
    @XmlElement(name = "second_name") var secondName: String = ""

    @XmlElementWrapper(name = "skills")
    @XmlElement(name = "skill")
    var skills: MutableList<Skill> = mutableListOf()
}
