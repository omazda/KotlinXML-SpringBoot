package com.students.repository

import com.students.entity.StudentEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface StudentRepository : JpaRepository<StudentEntity, Int> {

    @EntityGraph(attributePaths = ["importRecord", "skills"])
    fun findWithImportRecordById(id: Int): Optional<StudentEntity>

    fun countByImportRecordId(importId: Int): Long

    @Query(
        """
        select distinct s
        from StudentEntity s
        join fetch s.importRecord i
        left join fetch s.skills
        order by i.id desc, s.id asc
        """
    )
    fun findAllForSummary(): List<StudentEntity>

    @Query(
        """
        select distinct s
        from StudentEntity s
        join fetch s.importRecord i
        left join fetch s.skills
        order by i.id asc, s.id asc
        """
    )
    fun findAllForExport(): List<StudentEntity>
}
