package com.students.repository

import com.students.entity.ImportEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ImportRepository : JpaRepository<ImportEntity, Int>
