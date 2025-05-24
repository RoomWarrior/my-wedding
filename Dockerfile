# Используем базовый образ с Java
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл приложения
COPY target/wedding-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
