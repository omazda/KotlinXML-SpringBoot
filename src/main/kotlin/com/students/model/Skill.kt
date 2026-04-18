package com.students.model

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlValue

@XmlAccessorType(XmlAccessType.FIELD)
class Skill {

    @XmlValue
    var name: String = ""

    @XmlAttribute(name = "hard")
    var hard: Boolean? = null

    @XmlAttribute(name = "soft")
    var soft: Boolean? = null
}
