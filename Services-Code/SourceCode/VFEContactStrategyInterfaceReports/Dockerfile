# USING JBOSS IMAGE
FROM registry.access.redhat.com/jboss-eap-7/eap70-openshift

# COPY WAR TO DEPLOY
COPY /target/VFEContactStrategyInterfaceReports-1.0.war $JBOSS_HOME/standalone/deployments/
COPY /log4j2.xml $JBOSS_HOME/standalone/deployments/
USER root

# CHANGE WAR PERMISSIONS TO JBOSS USER
RUN chown jboss:jboss $JBOSS_HOME/standalone/deployments/VFEContactStrategyInterfaceReports-1.0.war
USER jboss

# EXPOSE JBOSS PORTS
EXPOSE 9990
EXPOSE 8080

# RUN JBOSS STANDALONE SERVER
CMD ["/opt/eap/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

# CREATE MANAGEMENT USER
RUN $JBOSS_HOME/bin/add-user.sh admin admin123 --silent
