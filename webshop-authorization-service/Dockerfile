# Build application
FROM maven:3.6.1-jdk-11-slim as builder

ENV BUILD_DIR /build
RUN mkdir -p $BUILD_DIR
WORKDIR $BUILD_DIR

COPY pom.xml $BUILD_DIR
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src $BUILD_DIR/src
RUN mvn package -DskipTests


# Run application
FROM openjdk:11-jre-slim

ENV APP_DIR /app
RUN mkdir -p $APP_DIR
WORKDIR $APP_DIR

COPY --from=builder /build/target/*.jar app.jar

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

EXPOSE 8080
