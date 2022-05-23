FROM adoptopenjdk/openjdk11:alpine
WORKDIR opt/app
EXPOSE 8080
HEALTHCHECK --interval=5m --timeout=3s CMD curl -f http://localhost:8080/actuator/health/ || exit 1
ARG JAR_FILE=build/libs/magneto-recruitment-ms-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]