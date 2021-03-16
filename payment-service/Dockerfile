FROM openjdk:12
VOLUME /tmp
ADD target/payment-service-0.0.1-SNAPSHOT.jar payment-service-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","payment-service-0.0.1-SNAPSHOT.jar"]