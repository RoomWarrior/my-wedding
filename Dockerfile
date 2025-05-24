# Этап 1: Сборка приложения
FROM maven:3.9.9-amazoncorretto-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файлы проекта
COPY . .

# Выполняем сборку проекта
RUN mvn clean package -DskipTests

# Этап 2: Создание финального образа
FROM amazoncorretto:17

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/wedding-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]
