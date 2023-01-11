#FROM java:8
#FROM 172.30.1.1:5000/contact-strategy/java8:latest
FROM registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.8

WORKDIR /opt/docker
USER root

ADD ["/target/VFESMSInterfaceBridgingEngine-1.0.jar", "/opt/docker/VFESMSInterfaceBridgingEngine-1.0.jar"]
ADD ["/log4j2.xml", "/opt/docker/log4j2.xml"]
ADD ["/target/lib", "/opt/docker/lib"]

RUN ["chown", "-R", "daemon:daemon", "."]
RUN ["chmod", "o+rwx", "/opt/docker"]
USER daemon
ADD ["/live-check.sh", "/opt/docker/live-check.sh"]
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar /opt/docker/VFESMSInterfaceBridgingEngine-1.0.jar