package com.students.model

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlValue

/**
 * Навык студента.
 *
 * @property name  Название навыка (текстовое содержимое XML-элемента).
 * @property hard  true, если это hard-навык (атрибут hard="true").
 * @property soft  true, если это soft-навык (атрибут soft="true").
 */
@XmlAccessorType(XmlAccessType.FIELD)
class Skill {
    @XmlValue var name: String = ""
    @XmlAttribute(name = "hard") var hard: Boolean? = null
    @XmlAttribute(name = "soft") var soft: Boolean? = null

    val isHard: Boolean get() = hard == true
}
