FROM openjdk:17-jdk-alpine
ARG JAR_FILE=build/libs/account-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

WORKDIR /app
COPY . /app

EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]