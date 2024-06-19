FROM maven:3.8.5-openjdk-18-slim AS build

WORKDIR /app

COPY . /app

RUN mvn clean install

FROM openjdk:18-jdk-alpine

COPY --from=build /app/target/FairBilling-1.0-SNAPSHOT.jar /app/FairBilling.jar

RUN apk add --no-cache bash

ENTRYPOINT ["bash"]
