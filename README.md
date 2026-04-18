# Students Spring Dashboard

Веб-приложение на Kotlin и Spring Boot для импорта XML-файлов со студентами, хранения данных в PostgreSQL и работы с ними через браузер.

## Что есть в проекте

- Spring Boot 3
- Spring MVC
- Spring Data JPA
- JAXB
- PostgreSQL
- Docker Compose
- pgAdmin

Приложение умеет:

- загружать XML со студентами;
- сохранять импорт в PostgreSQL;
- показывать динамическую таблицу в браузере;
- искать, фильтровать и сортировать строки;
- редактировать студентов;
- удалять студентов;
- удалять пустой импорт после удаления последнего студента;
- выгружать всех текущих студентов в один XML.

## Структура проекта

```text
.
├── src/main/kotlin/com/students
│   ├── StudentApplication.kt
│   ├── entity/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── web/
├── src/main/resources
│   ├── application.properties
│   └── templates/index.html
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── pgadmin/
```

## API

### `GET /api/imports`

Возвращает JSON со сводкой и строками таблицы:

- количество импортов;
- количество студентов;
- список строк для интерфейса.

### `POST /api/imports`

Принимает `multipart/form-data` с полем `file` и импортирует XML в БД.

### `PUT /api/imports/students/{studentId}`

Обновляет студента и его список навыков.

### `DELETE /api/imports/students/{studentId}`

Удаляет студента. Если это был последний студент импорта, сам импорт тоже удаляется.

### `GET /api/imports/export`

Возвращает единый XML-файл со всеми студентами, которые сейчас есть в БД.

## Веб-интерфейс

Главная страница:

```text
http://localhost:8081
```

На странице есть:

- форма загрузки XML;
- счётчики импортов и студентов;
- динамическая таблица;
- поиск, фильтрация и сортировка;
- редактирование и удаление студентов;
- кнопка выгрузки всех студентов в XML;
- просмотр XML после маршалинга.

## Docker

### Запуск всего стека

```bash
docker compose up --build -d
```

Эта команда поднимает сразу все сервисы:

- PostgreSQL: `localhost:5433`
- Spring Boot: `http://localhost:8081`
- pgAdmin: `http://localhost:8080`

### Остановка

```bash
docker compose down
```

### Полная очистка вместе с БД

```bash
docker compose down -v
```

Важно:

- данные PostgreSQL сохраняются в named volume `pg_data`;
- логин и пароль в compose указаны открыто, это допустимо только для dev-среды.

Параметры по умолчанию:

- БД: `students_db`
- пользователь: `admin`
- пароль: `admin`

pgAdmin:

- email: `admin@admin.com`
- password: `admin`

## Запуск приложения без Docker

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Git Bash / Linux / macOS

```bash
./mvnw spring-boot:run
```

Приложение ожидает доступный PostgreSQL и использует настройки из переменных окружения.

## Сборка JAR

```powershell
.\mvnw.cmd clean package
java -jar target/students-spring-1.0.0.jar
```

## Переменные окружения

Можно переопределить подключение к БД:

```text
DB_URL=jdbc:postgresql://localhost:5433/students_db
DB_USER=admin
DB_PASSWORD=admin
```

## Persistence

Сохранение и чтение данных реализовано через Spring Data JPA.

Основные сущности:

- `ImportEntity`
- `StudentEntity`
- `SkillEntity`

Схема:

```text
imports -> students -> skills
```

На текущем этапе Hibernate использует:

```text
spring.jpa.hibernate.ddl-auto=update
```

Для production-сценария это нужно заменить на управляемые миграции через Flyway или Liquibase.

## Формат XML

Поддерживается формат:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<students>
    <student>
        <first_name>Ivan</first_name>
        <second_name>Ivanov</second_name>
        <skills>
            <skill hard="true">Kotlin</skill>
            <skill soft="true">Communication</skill>
        </skills>
    </student>
</students>
```

## Экспорт XML

Экспорт через `GET /api/imports/export` формируется из текущего состояния БД.

Это значит, что в файл попадают:

- все актуальные студенты;
- изменения после редактирования;
- результат удаления студентов и импортов.
