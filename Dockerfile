FROM java:8-alpine
ADD target/gemnsis-0.0.1-SNAPSHOT-standalone.jar /gemnsis/app.jar
EXPOSE 8080

CMD ["java", "-jar", "/gemnsis/app.jar"]
