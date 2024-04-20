FROM amazoncorretto:17-alpine-jdk

COPY target/Restaurante-0.0.1-SNAPSHOT.jar app.jar
COPY images /app/images
ENTRYPOINT ["java","-jar","/app.jar"]

