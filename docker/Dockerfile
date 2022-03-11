FROM openjdk:17-jdk-alpine
RUN ls -all
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
RUN apk add --update-cache tesseract-ocr && rm -rf /var/cache/apk/*
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]