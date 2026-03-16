package db

import java.sql.Connection
import java.sql.DriverManager

/**
 * Конфигурация подключения к базе данных.
 * Читает параметры из переменных окружения и предоставляет JDBC-соединение [Connection].
 */
object DatabaseConfig {

    private val URL      = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5433/students_db"
    private val USER     = System.getenv("DB_USER") ?: "admin"
    private val PASSWORD = System.getenv("DB_PASSWORD") ?: "admin"

    /**
     * Открывает и возвращает новое JDBC-соединение с PostgreSQL.
     * Вызывающий код обязан закрыть соединение (например, через .use {}).
     */
    fun connection(): Connection {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: Exception) {
            throw RuntimeException("Не удалось подключиться к БД по адресу $URL. Проверьте учётные данные и доступность.", e)
        }
    }
}
