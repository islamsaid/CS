# USING JBOSS IMAGE
FROM registry.access.redhat.com/jboss-eap-7/eap70-openshift
#FROM registry.redhat.io/jboss-eap-7/eap74-openjdk8-runtime-openshift-rhel7

# COPY WAR TO DEPLOY
COPY /target/VFEContactStrategyWeb-1.0.war $JBOSS_HOME/standalone/deployments/
COPY /log4j2.xml $JBOSS_HOME/standalone/deployments/
COPY /IntermediateCA.cer $JBOSS_HOME/
COPY /IntermediateCA2.cer $JBOSS_HOME/
COPY /RootCert.cer $JBOSS_HOME/
USER root

CMD ["keytool", "-importcert", "-file", "/opt/eap/IntermediateCA.cer", "-keystore", "/usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts" , "-alias" , "ldap1" , "-storepass", "changeit", "-noprompt"]
CMD ["keytool", "-importcert", "-file", "/opt/eap/IntermediateCA2.cer", "-keystore", "/usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts" , "-alias" , "ldap2" , "-storepass", "changeit", "-noprompt"]
CMD ["keytool", "-importcert", "-file", "/opt/eap/RootCert.cer", "-keystore", "/usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts" , "-alias" , "ldap3" , "-storepass", "changeit", "-noprompt"]

# CHANGE WAR PERMISSIONS TO JBOSS USER
RUN chown jboss:jboss $JBOSS_HOME/standalone/deployments/VFEContactStrategyWeb-1.0.war
RUN chmod 777 /etc/pki/ca-trust/extracted/java/cacerts

RUN keytool -importcert -file /opt/eap/IntermediateCA.cer -keystore /usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts  -alias  ldap1  -storepass changeit -noprompt
RUN keytool -importcert -file /opt/eap/IntermediateCA2.cer -keystore /usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts  -alias  ldap2  -storepass changeit -noprompt
RUN keytool -importcert -file /opt/eap/RootCert.cer -keystore /usr/lib/jvm/java-1.8.0/jre/lib/security/cacerts  -alias  ldap3  -storepass changeit -noprompt

USER jboss



# EXPOSE JBOSS PORTS
EXPOSE 9990
EXPOSE 8080
RUN echo $JBOSS_HOME
# RUN JBOSS STANDALONE SERVER
CMD ["/opt/eap/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]

# CREATE MANAGEMENT USER
RUN $JBOSS_HOME/bin/add-user.sh admin admin123 --silent
