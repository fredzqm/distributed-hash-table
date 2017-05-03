FROM gradle:3.4

USER root
COPY . /app
WORKDIR /app

RUN gradle fatjar

ENTRYPOINT ["java", "-jar", "build/libs/distributed-hash-table-all.jar"]
