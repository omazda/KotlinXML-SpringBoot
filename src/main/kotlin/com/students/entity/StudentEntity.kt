package com.students.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.Table

@Entity
@Table(name = "students")
class StudentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "import_id", nullable = false)
    lateinit var importRecord: ImportEntity

    @Column(name = "first_name", nullable = false, length = 100)
    var firstName: String = ""

    @Column(name = "second_name", nullable = false, length = 100)
    var secondName: String = ""

    @OneToMany(
        mappedBy = "student",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("id ASC")
    var skills: MutableList<SkillEntity> = mutableListOf()

    fun addSkill(skill: SkillEntity) {
        skill.student = this
        skills.add(skill)
    }

    fun replaceSkills(newSkills: List<SkillEntity>) {
        skills.clear()
        newSkills.forEach(::addSkill)
    }
}
