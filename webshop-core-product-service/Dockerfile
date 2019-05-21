FROM maven:3.6.1-jdk-11 as builder

COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM openjdk:11.0.3-jre-slim
COPY --from=builder ./target/webshop-core-product-service-0.0.1-SNAPSHOT.jar ./
RUN java -jar webshop-core-product-service-0.0.1-SNAPSHOT.jar

EXPOSE 8080
