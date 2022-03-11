FROM openjdk:17-jdk-alpine
RUN ls -all
RUN ls -all CACHED
COPY fis.jar /fis.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java","-jar","/fis.jar"]