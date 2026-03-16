# Students Spring Boot

Spring Boot веб-приложение на Kotlin для загрузки XML-файлов студентов с отображением данных в виде таблицы.

## Как работает

```
Загрузка XML (браузер)
    │
    ▼
Spring Boot (порт 8081)
    ├── JAXB анмаршалинг → объекты Kotlin
    ├── JdbcTemplate → PostgreSQL (сохранение)
    └── Thymeleaf → HTML-таблица (отображение)
```

Каждая загрузка XML создаёт новую запись импорта — данные не перезаписываются.

## Технологии

| | |
|---|---|
| Язык | Kotlin 2.1.20 |
| Платформа | Java 21 (внутри Docker) |
| Фреймворк | Spring Boot 3.4.3 |
| Сборка | Maven 3.9 (через Docker) |
| XML | JAXB 4.0.x (Jakarta) |
| БД | PostgreSQL 16 (Docker) |
| JDBC | Spring JdbcTemplate + postgresql-42.x |
| Шаблоны | Thymeleaf 3 + Bootstrap 5 |

## Структура проекта

```
.
├── src/main/kotlin/com/students/
│   ├── StudentApplication.kt      # Точка входа
│   ├── model/                     # JAXB-модели: Skill, Student, Students
│   ├── service/
│   │   ├── XmlService.kt          # JAXB маршалинг / анмаршалинг
│   │   └── StudentService.kt      # БД: инициализация схемы, сохранение, чтение
│   └── web/
│       └── ImportController.kt    # HTTP: GET / и POST /upload
├── src/main/resources/
│   ├── application.properties
│   └── templates/index.html       # Thymeleaf-шаблон с таблицей
├── pom.xml
├── Dockerfile                     # Многоэтапная сборка (Maven → JRE)
└── docker-compose.yml             # PostgreSQL + Spring Boot
```

## Схема БД

```
imports  (id, file_name, imported_at)
    │
students (id, import_id, first_name, second_name)
    │
skills   (id, student_id, name, is_hard)
```

Таблицы создаются автоматически при старте приложения.

## Запуск

```bash
docker compose up --build
```

При первом запуске Docker скачивает образы и компилирует проект (~2-3 минуты).
Повторные запуски быстрые — слои кешируются:

```bash
docker compose up
```

## Веб-интерфейс

**http://localhost:8081**

- Форма загрузки XML-файла
- Таблица всех импортов с именами студентов и их навыками (hard / soft)

## Пример XML

```xml
<?xml version="1.0" encoding="UTF-8" ?>
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

## Настройка подключения к БД

Переопределяется через переменные окружения:

```bash
DB_URL=jdbc:postgresql://localhost:5434/students_db
DB_USER=admin
DB_PASSWORD=admin
```

Порт PostgreSQL по умолчанию: **5434** (не конфликтует с этапом 1–2, который использует 5433).

## Авторы

| Роль | |
|---|---|
| Автор | Мазняк Олег Владимирович |
| Код-агент | Claude (Anthropic) |
