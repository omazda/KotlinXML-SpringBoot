# Kotlin XML Processor

Консольное приложение на Kotlin для парсинга XML-файлов с сохранением данных в PostgreSQL.

## Как работает

```
XML файл
    │
    ▼
АНМАРШАЛИНГ (XML → объекты Kotlin)
    │
    ├──► МАРШАЛИНГ → вывод в консоль + файл _output.xml
    │
    └──► StudentRepository → PostgreSQL
```

Каждый запуск с любым XML создаёт новую запись импорта в БД — данные не перезаписываются.

## Технологии

| | |
|---|---|
| Язык | Kotlin 2.3.10 |
| Платформа | Java 25 |
| XML | JAXB 4.0.5 (Jakarta) |
| БД | PostgreSQL 16 (Docker) |
| JDBC | postgresql-42.7.5 |
| Веб-интерфейс БД | pgAdmin 4 (Docker) |

## Структура проекта

```
.
├── src/
│   ├── model/             # JAXB-модели: Skill, Student, Students
│   ├── xml/               # XmlProcessor — marshal / unmarshal
│   ├── db/                # DatabaseConfig, StudentRepository
│   └── Main.kt            # Точка входа
├── libs/                  # JAR-зависимости
├── pgadmin/
│   ├── servers.json       # Автоматическая регистрация сервера в pgAdmin
│   └── pg_hba.conf        # Настройка аутентификации PostgreSQL
├── test/                  # Тестовые XML-файлы
├── docker-compose.yml     # PostgreSQL + pgAdmin
├── build.sh               # Компиляция и запуск
└── students.xml           # Пример входного файла
```

## Схема БД

```
imports  (id, file_name, imported_at)
    │
students (id, import_id, first_name, second_name)
    │
skills   (id, student_id, name, is_hard)
```

Таблицы создаются автоматически при первом запуске.

## Запуск

### 1. Поднять инфраструктуру

```bash
docker compose up -d
```

### 2. Скомпилировать

```bash
./build.sh
```

> Запускать только из **bash** (Git Bash / WSL).

### 3. Запустить с XML-файлом

```bash
# только входной файл
./build.sh path/to/input.xml

# входной + выходной путь
./build.sh path/to/input.xml path/to/output.xml
```

Или напрямую:

```bash
java -classpath "app.jar;libs/jakarta.xml.bind-api-4.0.2.jar;libs/jaxb-impl-4.0.5.jar;libs/jaxb-core-4.0.5.jar;libs/jakarta.activation-api-2.1.3.jar;libs/angus-activation-2.0.2.jar;libs/istack-commons-runtime-4.2.0.jar;libs/txw2-4.0.5.jar;libs/postgresql-42.7.5.jar" MainKt <input-xml> [output-xml]
```

Если `output-xml` не указан — файл сохраняется рядом с input под именем `<имя>_output.xml`. Если указана несуществующая директория — она создаётся автоматически.

## pgAdmin

Веб-интерфейс для просмотра БД: **http://localhost:8080**

| | |
|---|---|
| Email | `admin@admin.com` |
| Password | `admin` |

Сервер `students_db` регистрируется автоматически.

## Настройка подключения к БД

Параметры можно переопределить через переменные окружения:

```bash
DB_URL=jdbc:postgresql://localhost:5433/students_db
DB_USER=admin
DB_PASSWORD=admin
```

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

## Авторы

| Роль | |
|---|---|
| Автор | Мазняк Олег Владимирович |
| Код-агент | Claude (Anthropic) |
| Независимое ревью | Google Gemini |
