FROM openjdk:17-jdk-alpine

MAINTAINER Niedermann IT <info@niedermann.it>

RUN apk add --update-cache tesseract-ocr && rm -rf /var/cache/apk/*
RUN addgroup -S jarfis && adduser -S jarfis -G jarfis

USER jarfis:jarfis

EXPOSE 8080

ARG JAR_FILE
COPY ${JAR_FILE} fis.jar

ENTRYPOINT ["java","-jar","/fis.jar"]