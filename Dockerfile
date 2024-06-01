FROM openjdk:21
WORKDIR /app
ARG JAR_FILE=target/CodeBuddy-0.0.1-SNAPSHOT.jar

ENV DB_USERNAME=postgres
ENV DB_PASSWORD=admin
ENV DB_NAME=codeBuddy
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV APP_PORT=8050

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-Dspring.datasource.password=${DB_PASSWORD}", "-Dspring.datasource.username=${DB_USERNAME}", "-Dspring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}", "-Dserver.port=${APP_PORT}", "-jar", "app.jar"]