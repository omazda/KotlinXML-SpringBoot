package com.students.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import jakarta.persistence.OrderBy
import java.time.LocalDateTime

@Entity
@Table(name = "imports")
class ImportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "file_name", nullable = false, length = 500)
    var fileName: String = ""

    @Column(name = "imported_at", nullable = false)
    var importedAt: LocalDateTime = LocalDateTime.now()

    @OneToMany(
        mappedBy = "importRecord",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("id ASC")
    var students: MutableList<StudentEntity> = mutableListOf()

    @PrePersist
    fun prePersist() {
        if (importedAt == LocalDateTime.MIN) {
            importedAt = LocalDateTime.now()
        }
    }

    fun addStudent(student: StudentEntity) {
        student.importRecord = this
        students.add(student)
    }
}
