FROM openjdk:17-jdk-alpine
RUN ls -all
RUN ls -all CACHED
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java","-jar","/app.jar"]