FROM adoptopenjdk/openjdk8:latest

RUN apt-get update && \
    apt-get install -y curl

EXPOSE 8080

WORKDIR /home/grundstuecksinformation

ARG JAR_FILE
COPY ${JAR_FILE} /home/grundstuecksinformation/app.jar
RUN chown -R 1001:0 /home/grundstuecksinformation && \
    chmod -R g=u /home/grundstuecksinformation

USER 1001

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/home/grundstuecksinformation/app.jar"]

HEALTHCHECK --interval=30s --timeout=30s --start-period=60s CMD curl http://localhost:8080/actuator/health