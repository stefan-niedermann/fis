FROM openjdk:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ENTRYPOINT ["java","-jar","fis.jar"]