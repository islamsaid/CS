/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.threads;

import com.asset.contactstrategy.common.controller.RabbitmqUtil;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import static com.asset.cs.sendingsms.controller.Controller.debugLogger;
import com.asset.cs.sendingsms.controller.Manager;
import static com.asset.cs.sendingsms.controller.Manager.deploymentQueues;
import com.asset.cs.sendingsms.defines.Defines;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kerollos.asaad
 */
public class ReloadThread extends Thread {

    private MainService mainService = new MainService();
    private long startTimeStamp = 0;

    private boolean checkReloadCounter() throws CommonException {
        return Integer.valueOf(mainService.getInstanceReloadCounter(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID)))) > 0;
    }

    @Override
    public void run() {
//        CommonLogger.businessLogger.info("*****************RELOAD THREAD STARTED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread Started").build());
        startTimeStamp = System.currentTimeMillis();
        while (!Defines.ENGINE_SHUTDOWN_FLAG.get()) {
            try {
                long sleepTime = Long.valueOf(Defines.databaseConfigurations.get(Defines.SENDING_SMS_RELOAD_CONFIGURATION_TIME));
                if (!Defines.CLOUD_MODE) {
                    debugLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting checking reload counter for instance")
                            .put(GeneralConstants.StructuredLogKeys.INSTANCE_ID, Defines.INSTACE_ID).build());
                    if (checkReloadCounter()) {
                        Defines.instanceConfigurations = mainService.getInstanceProperties(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID)));
                        mainService.updateInstanceReloadCounter(Integer.valueOf(Defines.fileConfigurations.get(Defines.INSTACE_ID)), "0");
                        Manager.prepareDeploymentQueues();
                        reload();
                    }
                } else {
                    reload();
                }
                long endTimeStamp = System.currentTimeMillis();
                long elapsedTime = endTimeStamp - startTimeStamp;
                if (elapsedTime > 60000 && com.asset.contactstrategy.common.defines.Defines.MESSAGING_MODE.equals(com.asset.contactstrategy.common.defines.Defines.RABBITMQ)) {
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            Thread.currentThread().getName() + " | Start checking time windows of queues").build());
                    Defines.appQueueMap = mainService.getHashedApplicationQueues();
                    Iterator smppAppsIterator = Defines.appQueueMap.entrySet().iterator();
                    while (smppAppsIterator.hasNext()) {

                        Map.Entry pair = (Map.Entry) smppAppsIterator.next();

                        QueueModel appQueue = (QueueModel) pair.getValue();

                        if (appQueue.isTimeWindowFlag()) {
                            checkTimeWindow(appQueue);
                        }

                    }
                    startTimeStamp = endTimeStamp;
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                CommonLogger.businessLogger.error("Reload Thread Caugth InterruptedException--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread Caugth InterruptedException--->" + ex, ex);
            } catch (CommonException ex) {
                CommonLogger.businessLogger.error("Reload Thread Caugth CommonException--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread Caugth CommonException--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("Reload Thread Caugth Exception--->" + ex);
                CommonLogger.errorLogger.error("Reload Thread Caugth Exception--->" + ex, ex);
            }
        }
//        CommonLogger.businessLogger.info("*****************RELOAD THREAD FINISHED ***********************");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread Finished").build());
    }

    private void reload() throws CommonException {
        Defines.databaseConfigurations = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_SENDING_SMS_ENGINE);

        Defines.deliveryEngineConfigurations = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_DELIVERY_AGGREGATION);
        SystemLookups.VFE_PREFIX = Utility.loadLookupsMap(DBStruct.VFE_TRIM_SMSC_MSG_ID_PREFIX.TABLE_NAME, DBStruct.VFE_TRIM_SMSC_MSG_ID_PREFIX.VFE_TRIM_SMSC_MSG_ID_PREFIX_ID, DBStruct.VFE_TRIM_SMSC_MSG_ID_PREFIX.VFE_TRIM_SMSC_MSG_ID_PREFIX_PREFIX);

        Defines.appQueueMap = mainService.getHashedApplicationQueues();
        Iterator smppAppsIterator = Defines.appQueueMap.entrySet().iterator();
        while (smppAppsIterator.hasNext()) {
            Map.Entry pair = (Map.Entry) smppAppsIterator.next();
            if (!Manager.deqeueurs_Senders_QMap.containsKey(pair.getKey()) && (deploymentQueues.containsKey(((QueueModel) pair.getValue()).getAppName()) || Defines.CLOUD_MODE)) {
//                CommonLogger.businessLogger.info("Reload Thread found new App_id:" + pair.getKey() + "in database");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reload Thread Found new AppID in Database")
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, pair.getKey()).build());
                Manager.addNewSMPPApp((QueueModel) pair.getValue());
            }
        }
        Iterator smsQueueMapIterator = Manager.deqeueurs_Senders_QMap.entrySet().iterator();
        while (smsQueueMapIterator.hasNext()) {
            boolean deleted = true;
            Map.Entry pair = (Map.Entry) smsQueueMapIterator.next();
            Iterator smppAppIterator = Defines.appQueueMap.entrySet().iterator();
            while (smppAppIterator.hasNext()) {
                Map.Entry pairNew = (Map.Entry) smppAppIterator.next();
                if (pairNew.getKey().equals(pair.getKey()) && (deploymentQueues.containsKey((String) pair.getKey()) || Defines.CLOUD_MODE)) {
                    deleted = false;
                    break; // TODO;
                }
            }
            if (deleted) {
//                CommonLogger.businessLogger.info("Sender Reloader Thread is going to remove queue and close threadpools for App_id: " + (String) pair.getKey());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Sender Reloader Thread is goint to Remover Queue and Close Threadpools")
                        .put(GeneralConstants.StructuredLogKeys.APP_ID, (String) pair.getKey()).build());
                Manager.appsThreadsShutdownMap.put((String) pair.getKey(), true);
                Manager.shutdownApp((String) pair.getKey());
                smsQueueMapIterator = Manager.deqeueurs_Senders_QMap.entrySet().iterator();
            }
        }
    }

    private void checkTimeWindow(QueueModel appQueue) throws CommonException {
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, Thread.currentThread().getName()
                + " | Checking time window of app queue : " + appQueue.getAppName()).build());
        Calendar cal = Calendar.getInstance();
        int systemHour = cal.get(Calendar.HOUR_OF_DAY);
        int systemMin = cal.get(Calendar.MINUTE);
        int systemTime = (systemHour * 100) + systemMin;
        if (systemTime >= ((appQueue.getTimeWindowFromHour() * 100) + appQueue.getTimeWindowFromMin())
                && systemTime < ((appQueue.getTimeWindowToHour() * 100) + appQueue.getTimeWindowToMin())) {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    Thread.currentThread().getName() + " | App queue : " + appQueue.getAppName() + " is within time window From "
                    + appQueue.getTimeWindowFromHour() + ":" + appQueue.getTimeWindowFromMin()
                    + " To " + appQueue.getTimeWindowToHour() + ":" + appQueue.getTimeWindowToMin()).build());
            if (Defines.queueChannelMap.get(appQueue.getAppId()) == null) {
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        Thread.currentThread().getName() + " | Register consumers to app queue : " + appQueue.getAppName()).build());
                MainService commonMainService = new MainService();
                com.rabbitmq.client.Connection conn = Defines.queueConnectionMap.get(appQueue.getAppId());
                for (int i = 0; i < appQueue.getDequeuePoolSize(); i++) {

                    Channel channel = null;
                    try {
                        channel = RabbitmqUtil.getChannel(conn);
                        channel.basicQos(appQueue.getThreshold());
                    } catch (IOException ex) {
                        throw new CommonException(Thread.currentThread().getName() + " | Exception --> Failed to create RabbitMQ channel ",
                                com.asset.contactstrategy.common.defines.ErrorCodes.RABBITMQ.CHANNEL_ERROR);
                    }
                    CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                            Thread.currentThread().getName() + " | Register consumer to app queue : "
                            + appQueue.getAppName() + " | consumerTag: " + appQueue.getAppName()).build());
                    commonMainService.registerRabbitmqConsumer(channel, new RabbitmqDequeuer(channel, appQueue), appQueue, "");
                    if (Defines.queueChannelMap.get(appQueue.getAppId()) == null) {
                        Defines.queueChannelMap.put(appQueue.getAppId(), new ArrayList<Channel>());
                    }
                    Defines.queueChannelMap.get(appQueue.getAppId()).add(channel);
                }
            }
        } else {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    Thread.currentThread().getName() + " | App queue : " + appQueue.getAppName() + " is not within time window From "
                    + appQueue.getTimeWindowFromHour() + ":" + appQueue.getTimeWindowFromMin()
                    + " To " + appQueue.getTimeWindowToHour() + ":" + appQueue.getTimeWindowToMin()).build());
            if (Defines.queueChannelMap.get(appQueue.getAppId()) != null) {
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        Thread.currentThread().getName() + " | Un-register consumers to app queue : " + appQueue.getAppName()).build());
                List<Channel> channels = Defines.queueChannelMap.get(appQueue.getAppId());
                for (Channel channel : channels) {
                    try {
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                                Thread.currentThread().getName() + " | Un-register consumer "
                                + appQueue.getAppId() + " from app queue : " + appQueue.getAppName()).build());
                        channel.basicCancel(Long.toString(appQueue.getAppId()));
                    } catch (IOException ex) {
                        throw new CommonException(Thread.currentThread().getName() + " | Exception --> Failed to cancel consumer of queue: " + appQueue.getAppName(),
                                com.asset.contactstrategy.common.defines.ErrorCodes.RABBITMQ.CHANNEL_ERROR);
                    }
                    RabbitmqUtil.closeChannel(channel);
                }

                Defines.queueChannelMap.remove(appQueue.getAppId());
            }
        }
    }
}
