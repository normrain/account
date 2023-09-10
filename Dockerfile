FROM gradle:8.3.0-jdk17-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:17-jdk-alpine
EXPOSE 8080
RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/account-0.0.1-SNAPSHOT.jar /app/spring-boot-application.jar


CMD ["java", "-jar", "/app/spring-boot-application.jar"]