# Используем базовый образ с Java и Maven
FROM maven:3.8.6-openjdk-21-slim AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY . .

# Выполняем сборку проекта
RUN mvn clean package -DskipTests

# Финальный образ
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/wedding-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
