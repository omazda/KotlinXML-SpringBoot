# Changelog

## [3.0.1] - 2026-04-18

### Changed
- `docker-compose.yml` обновлён: Spring Boot теперь поднимается вместе с PostgreSQL и pgAdmin одной командой.
- В `README.md` актуализированы инструкции по запуску, остановке и полной очистке Docker-стека.
- В документации явно указано, что веб-интерфейс и API доступны на `http://localhost:8081`.

### Infrastructure
- Для запуска всего стека используется `docker compose up --build -d`.
- Spring Boot контейнер подключается к Postgres внутри compose по `jdbc:postgresql://db:5432/students_db`.
- Данные PostgreSQL продолжают храниться в named volume `pg_data`.

---

## [3.0.0] - 2026-04-18

### Changed
- Проект переведён с консольного режима на Spring Boot веб-приложение.
- Слой persistence переведён с JDBC/Spring JDBC на Spring Data JPA.
- Сборка переведена на Maven Wrapper (`mvnw`, `mvnw.cmd`).
- Docker Compose обновлён: данные PostgreSQL теперь сохраняются в named volume `pg_data`.

### Added
- Веб-интерфейс для загрузки XML и просмотра данных.
- REST API:
  - `GET /api/imports`
  - `POST /api/imports`
  - `PUT /api/imports/students/{studentId}`
  - `DELETE /api/imports/students/{studentId}`
  - `GET /api/imports/export`
- Редактирование студентов из интерфейса.
- Удаление студентов из интерфейса.
- Удаление пустого импорта после удаления последнего студента.
- Выгрузка всех студентов в единый XML-файл.
- Поиск, фильтрация и сортировка в динамической таблице.

### Infrastructure
- PostgreSQL работает на `localhost:5433`.
- pgAdmin доступен на `http://localhost:8080`.
- Spring Boot приложение работает на `http://localhost:8081`.

---

## [2.2.0] - 2026-03-16

### Added
- `docker-compose.yml` — сервис `pgadmin` (`dpage/pgadmin4:latest`), порт `8080`.
- `pgadmin/servers.json` — автоматическая регистрация сервера `students_db` при старте pgAdmin.
- `pgadmin/pg_hba.conf` — кастомная аутентификация: `trust` для Docker-сети, `scram-sha-256` для внешних соединений.

---

## [2.1.0] - 2026-03-16

### Changed
- `src/db/StudentRepository.kt` — добавлена таблица `imports`; каждый запуск с любым XML создаёт новую запись импорта, данные не перезаписываются.
- `src/Main.kt` — передаёт имя файла в `StudentRepository.save()`.

### Schema

```text
imports -> students -> skills
```

---

## [2.0.0] - 2026-03-16

### Added
- `docker-compose.yml` — PostgreSQL 16 (`postgres:16-alpine`), порт `5433`.
- `src/db/DatabaseConfig.kt` — JDBC-подключение, параметры через переменные окружения.
- `src/db/StudentRepository.kt` — сохранение данных в БД в транзакции с rollback; авто-создание таблиц.
- `libs/postgresql-42.7.5.jar` — PostgreSQL JDBC-драйвер.

---

## [1.3.0] - 2026-03-16

### Added
- `README.md` — документация проекта.

### Changed
- `src/Main.kt` — улучшена обработка ошибок: `isFile()`, `exitProcess(1)`, `try/catch` для marshal.
- `src/xml/XmlProcessor.kt` — рефакторинг: создание `Marshaller` вынесено в приватный метод.

---

## [1.2.0] - 2026-03-16

### Changed
- `src/Main.kt` — перехват `UnmarshalException` с человекочитаемым сообщением вместо stack trace.

---

## [1.1.0] - 2026-03-16

### Changed
- `src/Main.kt` — путь к XML принимается как аргумент `args[0]`; выходной файл именуется `<имя>_output.xml`.
- `build.sh` — передаёт аргумент в `MainKt`; проверка наличия аргумента и существования файла.

---

## [1.0.0] - 2026-03-16

### Added
- `src/model/Skill.kt` — модель навыка (`@XmlValue`, `@XmlAttribute`).
- `src/model/Student.kt` — модель студента (`@XmlElement`, `@XmlElementWrapper`).
- `src/model/Students.kt` — корневой элемент (`@XmlRootElement`).
- `src/xml/XmlProcessor.kt` — `unmarshal(File)`, `marshal(Students)`, `marshalToFile(Students, File)`.
- `src/Main.kt` — точка входа.
- `build.sh` — скрипт компиляции и запуска.
- `libs/` — JAXB 4.0.x зависимости.
