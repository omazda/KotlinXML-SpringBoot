# Этап 1: сборка fat JAR через Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Кешируем зависимости отдельным слоем
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Компиляция и упаковка
COPY src ./src
RUN mvn package -DskipTests -q

# Этап 2: минимальный образ для запуска
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/students-spring-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
