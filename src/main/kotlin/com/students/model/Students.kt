package com.students.model

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "students")
@XmlAccessorType(XmlAccessType.FIELD)
class Students {

    @XmlElement(name = "student")
    var students: MutableList<Student> = mutableListOf()
}
