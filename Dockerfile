FROM gradle:8.14.3-jdk17 AS build
COPY --chown=gradle:gradle . /src
WORKDIR /src

RUN gradle build --no-daemon

FROM eclipse-temurin:17-jre

EXPOSE 8085

RUN mkdir /app


COPY --from=build /src/build/libs/wise-task-event-1.0.0.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]