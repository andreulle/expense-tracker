FROM openjdk:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/expense-tracker-0.0.1-SNAPSHOT-standalone.jar /expense-tracker/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/expense-tracker/app.jar"]
