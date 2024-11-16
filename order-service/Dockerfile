FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/order-service*.jar order-service.jar
ENTRYPOINT ["java", "-jar", "/order-service.jar", "--spring.profiles.active=compoose"]
