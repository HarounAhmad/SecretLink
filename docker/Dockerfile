FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENV SERVER_PORT=8080
ENV CRYPTO_SECRET_KEY=change_this
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=${SERVER_PORT}"]