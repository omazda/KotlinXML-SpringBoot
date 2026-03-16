package db

import model.Students
import java.sql.Connection

/**
 * Репозиторий для сохранения данных [Students] в PostgreSQL через JDBC.
 *
 * Схема создаётся автоматически при первом вызове [save], если таблицы не существуют.
 *
 * Таблицы:
 *  - imports  (id SERIAL PK, file_name, imported_at)
 *  - students (id SERIAL PK, import_id FK → imports.id, first_name, second_name)
 *  - skills   (id SERIAL PK, student_id FK → students.id, name, is_hard)
 *
 * Каждый вызов [save] создаёт новую запись импорта — несколько XML-файлов
 * могут сосуществовать в одной БД без перезаписи данных.
 */
object StudentRepository {

    /**
     * Создаёт необходимые таблицы, если они ещё не существуют.
     */
    private fun initSchema(connection: Connection) {
        connection.createStatement().use { stmt ->
            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS imports (
                    id          SERIAL PRIMARY KEY,
                    file_name   VARCHAR(500) NOT NULL,
                    imported_at TIMESTAMP NOT NULL DEFAULT NOW()
                )
                """.trimIndent()
            )
            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS students (
                    id          SERIAL PRIMARY KEY,
                    import_id   INTEGER NOT NULL REFERENCES imports(id) ON DELETE CASCADE,
                    first_name  VARCHAR(100) NOT NULL,
                    second_name VARCHAR(100) NOT NULL
                )
                """.trimIndent()
            )
            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS skills (
                    id         SERIAL PRIMARY KEY,
                    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                    name       VARCHAR(200) NOT NULL,
                    is_hard    BOOLEAN NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    /**
     * Сохраняет всех студентов и навыки из [students] в PostgreSQL,
     * привязывая их к новой записи импорта с именем файла [fileName].
     *
     * Вся операция выполняется в единой транзакции:
     * при любой ошибке транзакция откатывается, исключение пробрасывается выше.
     *
     * @param students  Разобранный объект [Students] после анмаршалинга JAXB.
     * @param fileName  Имя исходного XML-файла, сохраняется в таблице imports.
     */
    fun save(students: Students, fileName: String) {
        DatabaseConfig.connection().use { conn ->
            conn.autoCommit = false
            try {
                initSchema(conn)

                val importId = conn.prepareStatement(
                    "INSERT INTO imports (file_name) VALUES (?) RETURNING id"
                ).use { stmt ->
                    stmt.setString(1, fileName)
                    stmt.executeQuery().use { rs ->
                        if (!rs.next()) throw RuntimeException("Не удалось получить ID импорта")
                        rs.getInt("id")
                    }
                }

                conn.prepareStatement(
                    "INSERT INTO students (import_id, first_name, second_name) VALUES (?, ?, ?) RETURNING id"
                ).use { insertStudent ->
                    conn.prepareStatement(
                        "INSERT INTO skills (student_id, name, is_hard) VALUES (?, ?, ?)"
                    ).use { insertSkill ->

                        for (student in students.students) {
                            insertStudent.setInt(1, importId)
                            insertStudent.setString(2, student.firstName)
                            insertStudent.setString(3, student.secondName)

                            val studentId = insertStudent.executeQuery().use { rs ->
                                if (!rs.next()) throw RuntimeException("Не удалось получить ID студента")
                                rs.getInt("id")
                            }

                            for (skill in student.skills) {
                                insertSkill.setInt(1, studentId)
                                insertSkill.setString(2, skill.name)
                                insertSkill.setBoolean(3, skill.hard == true)
                                insertSkill.addBatch()
                            }
                            insertSkill.executeBatch()
                        }
                    }
                }

                conn.commit()
                println("Импорт #$importId — сохранено ${students.students.size} студент(ов) из '$fileName'.")
            } catch (e: Exception) {
                conn.rollback()
                throw e
            }
        }
    }
}
