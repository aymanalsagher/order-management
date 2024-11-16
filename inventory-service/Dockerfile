FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/inventory-service*.jar inventory-service.jar
ENTRYPOINT ["java", "-jar", "/inventory-service.jar"]
