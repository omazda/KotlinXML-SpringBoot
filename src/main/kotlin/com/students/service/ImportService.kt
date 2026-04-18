package com.students.service

import com.students.entity.ImportEntity
import com.students.entity.SkillEntity
import com.students.entity.StudentEntity
import com.students.model.Skill
import com.students.model.Student
import com.students.model.Students
import com.students.repository.ImportRepository
import com.students.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

data class ImportSummary(
    val importCount: Int,
    val studentCount: Int,
    val rows: List<ImportStudentRow>
)

data class ImportStudentRow(
    val importId: Int,
    val fileName: String,
    val importedAt: LocalDateTime,
    val studentId: Int?,
    val firstName: String?,
    val secondName: String?,
    val hardSkills: List<String>,
    val softSkills: List<String>
)

data class StudentUpdatePayload(
    val firstName: String,
    val secondName: String,
    val hardSkills: List<String>,
    val softSkills: List<String>
)

data class ImportResult(
    val importId: Int,
    val fileName: String,
    val studentCount: Int,
    val xmlPreview: String
)

@Service
class ImportService(
    private val importRepository: ImportRepository,
    private val studentRepository: StudentRepository
) {

    @Transactional
    fun save(students: Students, fileName: String, xmlPreview: String): ImportResult {
        val importEntity = ImportEntity().apply {
            this.fileName = fileName
            students.students
                .map(::toStudentEntity)
                .forEach(::addStudent)
        }

        val savedImport = importRepository.save(importEntity)

        return ImportResult(
            importId = savedImport.id!!,
            fileName = fileName,
            studentCount = savedImport.students.size,
            xmlPreview = xmlPreview
        )
    }

    @Transactional(readOnly = true)
    fun getSummary(): ImportSummary {
        val students = studentRepository.findAllForSummary()
        val rows = students.map { student -> toRow(student.importRecord, student) }

        return ImportSummary(
            importCount = rows.map { it.importId }.toSet().size,
            studentCount = rows.size,
            rows = rows
        )
    }

    @Transactional(readOnly = true)
    fun exportAllStudents(): Students {
        val allStudents = studentRepository.findAllForExport()

        return Students().apply {
            students.addAll(allStudents.map(::toXmlStudent))
        }
    }

    @Transactional
    fun updateStudent(studentId: Int, payload: StudentUpdatePayload): ImportStudentRow {
        val firstName = payload.firstName.trim()
        val secondName = payload.secondName.trim()
        require(firstName.isNotBlank()) { "Имя студента не должно быть пустым" }
        require(secondName.isNotBlank()) { "Фамилия студента не должна быть пустой" }

        val student = studentRepository.findWithImportRecordById(studentId)
            .orElseThrow { IllegalArgumentException("Студент с id=$studentId не найден") }

        student.firstName = firstName
        student.secondName = secondName
        student.replaceSkills(
            createSkillEntities(payload.hardSkills, true) +
                createSkillEntities(payload.softSkills, false)
        )

        val saved = studentRepository.save(student)
        return toRow(saved.importRecord, saved)
    }

    @Transactional
    fun deleteStudent(studentId: Int) {
        val student = studentRepository.findWithImportRecordById(studentId)
            .orElseThrow { IllegalArgumentException("Студент с id=$studentId не найден") }

        val importId = student.importRecord.id!!
        studentRepository.delete(student)
        studentRepository.flush()

        if (studentRepository.countByImportRecordId(importId) == 0L) {
            importRepository.deleteById(importId)
        }
    }

    private fun toStudentEntity(source: Student): StudentEntity =
        StudentEntity().apply {
            this.firstName = source.firstName
            this.secondName = source.secondName
            replaceSkills(source.skills.map(::toSkillEntity))
        }

    private fun toSkillEntity(source: Skill): SkillEntity =
        SkillEntity().apply {
            this.name = source.name
            this.isHard = source.hard == true
        }

    private fun toRow(importRecord: ImportEntity, student: StudentEntity): ImportStudentRow =
        ImportStudentRow(
            importId = importRecord.id!!,
            fileName = importRecord.fileName,
            importedAt = importRecord.importedAt,
            studentId = student.id!!,
            firstName = student.firstName,
            secondName = student.secondName,
            hardSkills = student.skills.filter { it.isHard }.map { it.name },
            softSkills = student.skills.filter { !it.isHard }.map { it.name }
        )

    private fun createSkillEntities(skills: List<String>, isHard: Boolean): List<SkillEntity> =
        skills.map { it.trim() }
            .filter { it.isNotBlank() }
            .map { skillName ->
                SkillEntity().apply {
                    this.name = skillName
                    this.isHard = isHard
                }
            }

    private fun toXmlStudent(entity: StudentEntity): Student =
        Student().apply {
            firstName = entity.firstName
            secondName = entity.secondName
            skills = entity.skills.map { skillEntity ->
                Skill().apply {
                    name = skillEntity.name
                    if (skillEntity.isHard) {
                        hard = true
                    } else {
                        soft = true
                    }
                }
            }.toMutableList()
        }
}
