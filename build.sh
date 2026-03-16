#!/usr/bin/env bash
# build.sh — компиляция и запуск проекта
# Использовать только из bash (Git Bash / WSL), не из PowerShell

CP="app.jar;libs/jakarta.xml.bind-api-4.0.2.jar;libs/jaxb-impl-4.0.5.jar;libs/jaxb-core-4.0.5.jar;libs/jakarta.activation-api-2.1.3.jar;libs/angus-activation-2.0.2.jar;libs/istack-commons-runtime-4.2.0.jar;libs/txw2-4.0.5.jar;libs/postgresql-42.7.5.jar"
COMPILE_CP="${CP/app.jar;/}"
SRC="src/model/Skill.kt src/model/Student.kt src/model/Students.kt src/xml/XmlProcessor.kt src/db/DatabaseConfig.kt src/db/StudentRepository.kt src/Main.kt"

echo "=== Компиляция ==="
kotlinc $SRC -classpath "$COMPILE_CP" -include-runtime -d app.jar

echo "=== Запуск ==="
# Использование: ./build.sh <input-xml> [output-xml]
XML_FILE="${1:-students.xml}"
OUTPUT_FILE="${2:-}"
if [ -n "$OUTPUT_FILE" ]; then
    java -classpath "$CP" MainKt "$XML_FILE" "$OUTPUT_FILE"
else
    java -classpath "$CP" MainKt "$XML_FILE"
fi
