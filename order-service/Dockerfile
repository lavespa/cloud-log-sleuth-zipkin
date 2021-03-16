FROM openjdk:12
VOLUME /tmp
ADD target/order-service-0.0.1-SNAPSHOT.jar order-service-0.0.1-SNAPSHOT.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","order-service-0.0.1-SNAPSHOT.jar"]