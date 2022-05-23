FROM adoptopenjdk/openjdk11:alpine
WORKDIR opt/app
EXPOSE 8080
ARG JAR_FILE=build/libs/magneto-recruitment-ms-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]