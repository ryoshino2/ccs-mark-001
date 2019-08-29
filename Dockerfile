FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/ccsmark-0.0.1-SNAPSHOT*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-Dspring.profiles.active=docker","-jar", "/app.jar"]