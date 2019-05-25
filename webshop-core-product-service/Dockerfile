FROM maven:3.6.1-jdk-11-slim as builder

ENV BUILD_DIR /build
RUN mkdir -p $BUILD_DIR
WORKDIR $BUILD_DIR

COPY pom.xml $BUILD_DIR

#Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins

COPY src $BUILD_DIR/src

RUN mvn package -DskipTests

# building done, proceeding to run the app

FROM openjdk:11-jre-slim
EXPOSE 8080

ENV APP_HOME /app

ENV JAVA_OPTS=""

# create base app folder
RUN mkdir $APP_HOME
WORKDIR $APP_HOME

# copy executable jar file from the builder image
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
