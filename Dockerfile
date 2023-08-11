FROM ubuntu:jammy as builder

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:20 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

MAINTAINER "me@christophebernard.be"
LABEL author="Christophe Bernard"

RUN apt-get update && apt-get install -y maven

WORKDIR /build
COPY . /build

RUN mvn install
RUN mvn clean package

FROM ubuntu:jammy as runtime

ENV JAVA_HOME=/opt/java/openjdk
COPY --from=eclipse-temurin:20 $JAVA_HOME $JAVA_HOME
ENV PATH="${JAVA_HOME}/bin:${PATH}"

MAINTAINER "me@christophebernard.be"
LABEL author="Christophe Bernard"

WORKDIR /app
COPY --from=builder /build/target/thermostat_bot*.jar /app/thermostat_bot.jar
RUN printenv > /app/.env

ENTRYPOINT ["java", "-jar", "/app/thermostat_bot.jar"]
