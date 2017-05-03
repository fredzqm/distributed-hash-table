FROM openjdk:8

RUN mkdir -p /app

WORKDIR /app
VOLUME /app
ENTRYPOINT ["java", "-jar", "build/libs/distributed-hash-table-all.jar"]
