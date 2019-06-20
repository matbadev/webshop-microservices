# Build application
FROM maven:3.6.1-jdk-11-slim as builder

ENV BUILD_DIR /build
RUN mkdir -p $BUILD_DIR
WORKDIR $BUILD_DIR

COPY pom.xml $BUILD_DIR
RUN mvn --batch-mode dependency:go-offline

COPY src $BUILD_DIR/src
RUN mvn --batch-mode package


# Run application
FROM tomcat:9.0

COPY --from=builder /build/target/*.war /usr/local/tomcat/webapps/webshop.war
COPY ./conf/tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml

EXPOSE 8080
