# Changelog

## [2.2.0] - 2026-03-16

### Added
- `docker-compose.yml` — сервис `pgadmin` (`dpage/pgadmin4:latest`), порт `8080`
- `pgadmin/servers.json` — автоматическая регистрация сервера `students_db` при старте pgAdmin
- `pgadmin/pg_hba.conf` — кастомная аутентификация: `trust` для Docker-сети, `scram-sha-256` для внешних соединений

---

## [2.1.0] - 2026-03-16

### Changed
- `src/db/StudentRepository.kt` — добавлена таблица `imports`; каждый запуск с любым XML создаёт новую запись, данные не перезаписываются
- `src/Main.kt` — передаёт имя файла в `StudentRepository.save()`

### Schema
```
imports  ──< students ──< skills
```

---

## [2.0.0] - 2026-03-16

### Added
- `docker-compose.yml` — PostgreSQL 16 (`postgres:16-alpine`), порт `5433`
- `src/db/DatabaseConfig.kt` — JDBC подключение, параметры через переменные окружения
- `src/db/StudentRepository.kt` — сохранение данных в БД в транзакции с rollback; авто-создание таблиц
- `libs/postgresql-42.7.5.jar` — PostgreSQL JDBC драйвер

---

## [1.3.0] - 2026-03-16

### Added
- `README.md` — документация проекта

### Changed
- `src/Main.kt` — улучшена обработка ошибок: `isFile()`, `exitProcess(1)`, `try/catch` для marshal
- `src/xml/XmlProcessor.kt` — рефакторинг: создание `Marshaller` вынесено в приватный метод

---

## [1.2.0] - 2026-03-16

### Changed
- `src/Main.kt` — перехват `UnmarshalException` с человекочитаемым сообщением вместо stack trace

---

## [1.1.0] - 2026-03-16

### Changed
- `src/Main.kt` — путь к XML принимается как аргумент `args[0]`; выходной файл именуется `<имя>_output.xml`
- `build.sh` — передаёт аргумент в `MainKt`; проверка наличия аргумента и существования файла

---

## [1.0.0] - 2026-03-16

### Added
- `src/model/Skill.kt` — модель навыка (`@XmlValue`, `@XmlAttribute`)
- `src/model/Student.kt` — модель студента (`@XmlElement`, `@XmlElementWrapper`)
- `src/model/Students.kt` — корневой элемент (`@XmlRootElement`)
- `src/xml/XmlProcessor.kt` — `unmarshal(File)`, `marshal(Students)`, `marshalToFile(Students, File)`
- `src/Main.kt` — точка входа
- `build.sh` — скрипт компиляции и запуска
- `libs/` — JAXB 4.0.x зависимости (jakarta.xml.bind-api, jaxb-impl, jaxb-core, istack-commons-runtime, txw2, jakarta.activation-api, angus-activation)
