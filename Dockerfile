FROM gradle:3.4

USER root
WORKDIR /app
ADD *.gradle /app/
VOLUME /app/data

RUN gradle dependencies
COPY . /app
RUN gradle fatjar

ENTRYPOINT ["java", "-jar", "build/libs/distributed-hash-table-all.jar"]
