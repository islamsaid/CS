FROM rhosp13/openstack-base:20210908.1


LABEL description="Red Hat OpenStack Platform 13.0 rabbitmq"
LABEL io.openshift.tags="rhosp osp openstack osp-13.0"
LABEL summary="Red Hat OpenStack Platform 13.0 rabbitmq"
LABEL io.k8s.display-name="Red Hat OpenStack Platform 13.0 rabbitmq"
LABEL com.redhat.component="openstack-rabbitmq-container"
LABEL name="rhosp13/openstack-rabbitmq"
LABEL version="13.0"
LABEL batch="20210908.1"






RUN usermod --append --home /var/lib/rabbitmq --groups kolla rabbitmq \
    && mkdir -p /var/lib/rabbitmq \
    && chown -R 42439:42439 /var/lib/rabbitmq




    



RUN yum -y install erlang-hipe hostname rabbitmq-server pacemaker pacemaker-remote pcs libqb resource-agents && yum clean all && rm -rf /var/cache/yum




# External rabbitmq plugins installation is disabled


COPY extend_start.sh /usr/local/bin/kolla_extend_start
COPY rabbitmq_get_gospel_node.py /usr/local/bin/rabbitmq_get_gospel_node
RUN chmod 755 /usr/local/bin/kolla_extend_start /usr/local/bin/rabbitmq_get_gospel_node


RUN mkdir -p /etc/libqb /openstack && \
    ln -s /usr/share/openstack-tripleo-common/healthcheck/rabbitmq /openstack/healthcheck && \
    chmod a+rx /openstack/healthcheck



USER rabbitmq