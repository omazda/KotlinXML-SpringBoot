package com.students.service

import com.students.model.Students
import jakarta.annotation.PostConstruct
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Запись об импорте XML-файла.
 *
 * @property id          Идентификатор импорта.
 * @property fileName    Имя загруженного файла.
 * @property importedAt  Дата и время импорта.
 * @property students    Список студентов в этом импорте.
 */
data class ImportRecord(
    val id: Int,
    val fileName: String,
    val importedAt: LocalDateTime,
    val students: List<StudentRecord>
)

/**
 * Запись о студенте.
 *
 * @property id          Идентификатор студента.
 * @property firstName   Имя.
 * @property secondName  Фамилия.
 * @property skills      Список навыков студента.
 */
data class StudentRecord(
    val id: Int,
    val firstName: String,
    val secondName: String,
    val skills: List<SkillRecord>
) {
    /** Строка hard-навыков через запятую. */
    val hardSkills: String get() = skills.filter { it.isHard }.joinToString(", ") { it.name }

    /** Строка soft-навыков через запятую. */
    val softSkills: String get() = skills.filter { !it.isHard }.joinToString(", ") { it.name }
}

/**
 * Запись о навыке.
 *
 * @property name    Название навыка.
 * @property isHard  true — hard-навык, false — soft-навык.
 */
data class SkillRecord(
    val name: String,
    val isHard: Boolean
)

/**
 * Сервис для работы с БД: инициализация схемы, сохранение и чтение данных.
 */
@Service
class StudentService(private val jdbc: JdbcTemplate) {

    /**
     * Создаёт таблицы imports, students, skills при старте приложения,
     * если они ещё не существуют.
     */
    @PostConstruct
    fun initSchema() {
        jdbc.execute(
            """CREATE TABLE IF NOT EXISTS imports (
                id SERIAL PRIMARY KEY,
                file_name VARCHAR(500) NOT NULL,
                imported_at TIMESTAMP NOT NULL DEFAULT NOW()
            )"""
        )
        jdbc.execute(
            """CREATE TABLE IF NOT EXISTS students (
                id SERIAL PRIMARY KEY,
                import_id INTEGER NOT NULL REFERENCES imports(id) ON DELETE CASCADE,
                first_name VARCHAR(100) NOT NULL,
                second_name VARCHAR(100) NOT NULL
            )"""
        )
        jdbc.execute(
            """CREATE TABLE IF NOT EXISTS skills (
                id SERIAL PRIMARY KEY,
                student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                name VARCHAR(200) NOT NULL,
                is_hard BOOLEAN NOT NULL
            )"""
        )
    }

    /**
     * Сохраняет студентов и навыки в БД в рамках одной транзакции.
     *
     * @param students  Разобранный объект [Students] после анмаршалинга JAXB.
     * @param fileName  Имя исходного XML-файла.
     * @return          ID созданной записи импорта.
     */
    @Transactional
    fun save(students: Students, fileName: String): Int {
        val importId = jdbc.queryForObject(
            "INSERT INTO imports (file_name) VALUES (?) RETURNING id",
            Int::class.java, fileName
        )!!

        for (student in students.students) {
            val studentId = jdbc.queryForObject(
                "INSERT INTO students (import_id, first_name, second_name) VALUES (?, ?, ?) RETURNING id",
                Int::class.java, importId, student.firstName, student.secondName
            )!!

            for (skill in student.skills) {
                jdbc.update(
                    "INSERT INTO skills (student_id, name, is_hard) VALUES (?, ?, ?)",
                    studentId, skill.name, skill.hard == true
                )
            }
        }

        return importId
    }

    /**
     * Возвращает все импорты со студентами и навыками, отсортированные по убыванию ID.
     */
    fun findAllImports(): List<ImportRecord> {
        val imports = jdbc.query(
            "SELECT id, file_name, imported_at FROM imports ORDER BY id DESC"
        ) { rs, _ ->
            ImportRecord(
                id = rs.getInt("id"),
                fileName = rs.getString("file_name"),
                importedAt = rs.getTimestamp("imported_at").toLocalDateTime(),
                students = emptyList()
            )
        }

        return imports.map { imp ->
            val students = jdbc.query(
                "SELECT id, first_name, second_name FROM students WHERE import_id = ? ORDER BY id",
                { rs, _ ->
                    StudentRecord(
                        id = rs.getInt("id"),
                        firstName = rs.getString("first_name"),
                        secondName = rs.getString("second_name"),
                        skills = emptyList()
                    )
                },
                imp.id
            )

            imp.copy(students = students.map { student ->
                val skills = jdbc.query(
                    "SELECT name, is_hard FROM skills WHERE student_id = ? ORDER BY id",
                    { rs, _ ->
                        SkillRecord(
                            name = rs.getString("name"),
                            isHard = rs.getBoolean("is_hard")
                        )
                    },
                    student.id
                )
                student.copy(skills = skills)
            })
        }
    }
}
