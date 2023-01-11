#FROM java:8
#FROM 172.30.1.1:5000/contact-strategy/java8:latest
FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.8

EXPOSE 8080

WORKDIR /opt/docker
ADD ["/target/BridgingDequeuerService-1.0.jar", "/opt/docker/BridgingDequeuerService-1.0.jar"]
ADD ["/log4j2.xml", "/opt/docker/log4j2.xml"]
ADD ["/target/lib", "/opt/docker/lib"]
USER root
RUN ["chown", "-R", "daemon:daemon", "."]
RUN ["chmod", "o+rwx", "/opt/docker"]
USER daemon
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /opt/docker/BridgingDequeuerService-1.0.jar
