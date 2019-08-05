FROM java:8-alpine
ADD target/nukr-0.0.1-SNAPSHOT-standalone.jar /nukr/app.jar
EXPOSE 8080

CMD ["java", "-jar", "/nukr/app.jar"]
