FROM openjdk:17-jdk-alpine
WORKDIR /app/docker
COPY target/spring-0.0.1-SNAPSHOT.jar spring.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar spring.jar
