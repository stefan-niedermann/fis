FROM openjdk:17-jre-alpine
COPY fis.jar /fis.jar
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java","-jar","/fis.jar"]