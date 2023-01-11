package com.asset.cs.vfesmscinterface.initializer;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.controller.RabbitmqUtil;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.CsSmscInterfaceHistoryModel;
import com.asset.contactstrategy.common.models.LogModel;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.common.models.EnqueueModel;
import com.asset.contactstrategy.common.models.EnqueueModelDeliverSM;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.interfaces.threads.LivenessThread;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.runnables.ArchiverRunnables;
import com.asset.cs.vfesmscinterface.runnables.MonitorRunnable;
import com.asset.cs.vfesmscinterface.runnables.ReloaderRunnable;
import com.asset.cs.vfesmscinterface.runnables.SendSMSIntegrationArchiverRunnable;
import com.asset.cs.vfesmscinterface.runnables.SessionTimeoutRunnable;
import com.asset.cs.vfesmscinterface.socket.Server;
import com.asset.cs.vfesmscinterface.socket.ServerConnectionListener;
import com.asset.cs.vfesmscinterface.socket.Session;
import com.asset.cs.vfesmscinterface.threads.ShutdownHooKThread;
import com.asset.cs.vfesmscinterface.utils.TaskExecutor;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author mostafa.kashif
 * @author eman.fawzy
 * @author mohamed.morsy
 */
public class Manager {

    private static Server server = null;

    public static AtomicBoolean isShutdown = new AtomicBoolean(false);

    public static HashMap<String, SMSCInterfaceClientModel> clientMap = null;

    public static HashMap<String, String> systemProperities = null;

    public static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

    private static ExecutorService mainExecutor = Executors.newCachedThreadPool();

    private static MainService mainService = new MainService();

    public static TaskExecutor<ArchiverRunnables, LogModel> archiverTaskExecutor;

    public static TaskExecutor<SendSMSIntegrationArchiverRunnable, CsSmscInterfaceHistoryModel> sendSmsIntegrationArchiverTaskExecutor;

    public static MonitorRunnable monitorRunnable;

    public static ReloaderRunnable reloaderRunnable;

    public static SessionTimeoutRunnable sessionTimeoutRunnable;

    public static ServerConnectionListener serverConnectionListener;

    private static LivenessThread livenessThread = null;

    public static ArrayList<ServiceModel> services = null;

    public static Connection consumersConnection = null;
    public static HashMap<String, ServiceModel> queueServiceMap = null;
    public static HashMap<String, QueueModel> serviceQueueMap = null;
    public static HashMap<String, ArrayBlockingQueue<EnqueueModel>> dequeuedDeliverRequests = new HashMap();
    public static ArrayList<Channel> consumersChannels = new ArrayList<>();

    public static void initSystem() {
        try {
            Defines.runningProjectId = GeneralConstants.SRC_ID_SMSC_INTERFACE;

            Initializer.readPropertiesFile();

            Initializer.initializeDataSource();

            Initializer.initializeLoggers();

            Initializer.initializeRabbitmq();

            Manager.systemProperities = mainService.getSystemPropertiesByGroupID(Defines.runningProjectId);

            Manager.clientMap = mainService.retrieveClients();

            Manager.queueServiceMap = mainService.getQueueServiceMap();

            Manager.serviceQueueMap = mainService.getServiceQueueMap();

            Manager.services = mainService.getServices();

            Manager.server = new Server(Defines.SMSC_INTERFACE_PROPERTIES.SERVER_PORT_VALUE);

            Runtime.getRuntime().addShutdownHook(new ShutdownHooKThread());

            CommonLogger.businessLogger.debug("shutdownHook registered successfully");

            // init system threads
            archiverTaskExecutor = new TaskExecutor<>(ArchiverRunnables.class,
                    Defines.SMSC_INTERFACE_PROPERTIES.NO_OF_ARCHIVER_THREADS_VALUE,
                    Defines.SMSC_INTERFACE_PROPERTIES.ARCHIVER_QUEUE_SIZE_VALUE);

            sendSmsIntegrationArchiverTaskExecutor = new TaskExecutor<>(SendSMSIntegrationArchiverRunnable.class,
                    Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_NO_OF_ARCHIVER_THREADS_VALUE,
                    Defines.SMSC_INTERFACE_PROPERTIES.CS_SMS_ARCHIVER_QUEUE_SIZE_VALUE);

            Manager.mainExecutor.execute(new ServerConnectionListener(Manager.server.getServerSocket()));

            Manager.mainExecutor.execute(new SessionTimeoutRunnable());

            Manager.mainExecutor.execute(new ReloaderRunnable());

            Manager.mainExecutor.execute(new MonitorRunnable());

            registerConsumers();
            
            initLivenessThread();

        } catch (CommonException ex) {
            CommonLogger.businessLogger.error("CommonException => ", ex);
            CommonLogger.errorLogger.error(ex);
            shutdownSystem();
        } catch (Exception ex) {
            ex.printStackTrace();
            CommonLogger.businessLogger.error("Exception => ", ex);
            CommonLogger.errorLogger.error(ex);
            shutdownSystem();
        } catch (Throwable th) {
            th.printStackTrace();
            CommonLogger.businessLogger.error("Throwable => ", th);
            CommonLogger.errorLogger.error(th);
            shutdownSystem();
        }
    }

    public static void shutdownSystem() {
        try {
            isShutdown.set(true);

            // force kill sessions
            closeAllSession();

            if (archiverTaskExecutor != null) {
                archiverTaskExecutor.shutdown();
            }

            if (sendSmsIntegrationArchiverTaskExecutor != null) {
                sendSmsIntegrationArchiverTaskExecutor.shutdown();
            }

            if (mainExecutor != null) {
                mainExecutor.shutdownNow();
            }

            if (Manager.server != null) {
                Manager.server.close();
            }

        } catch (Exception ex) {
            CommonLogger.businessLogger.error("Exception => ", ex);
            CommonLogger.errorLogger.error(ex);
        }
    }

    public static void createNewSession(Socket socket) throws IOException {
        Session session = new Session(socket,
                Defines.SMSC_INTERFACE_PROPERTIES.REQUESTS_QUEUE_SESSION_PER_SESSION_VALUE, Data.NUM_OF_SOCKET_READER,
                Defines.SMSC_INTERFACE_PROPERTIES.RESPONSES_QUEUE_SESSION_PER_SESSION_VALUE, Data.NUM_OF_SOCKET_WRITER);
        sessionMap.put(session.getSessionId(), session);
    }

    public static void closeSession(Session session) {
        session.closeSession();
        sessionMap.remove(session.getSessionId());
//		CommonLogger.businessLogger.debug(session + " session colsed & removed successfully");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session Closed & Removerd Successfully")
                .put(GeneralConstants.StructuredLogKeys.SESSION, session).build());
    }

    public static void unbindSession(Session session) {
        session.unbindSession();
        sessionMap.remove(session.getSessionId());
//        CommonLogger.businessLogger.debug(session + " session colsed & removed successfully");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session Closed & Removerd Successfully")
                .put(GeneralConstants.StructuredLogKeys.SESSION, session).build());
    }

    public static void closeAllSession() {
        if (sessionMap != null) {
            /**
             * 05 - Aug - 2020 The ConcurrentHashMap converted into a
             * synchronized hash map to avoid the problem of un-removed closed
             * sessions from the sessionMap All iterators over the sessionMap
             * are put inside a synchronized block of the sessionMap
             */
            synchronized (sessionMap) {
                for (Entry<String, Session> entry : sessionMap.entrySet()) {
                    entry.getValue().closeSession();
                }
            }
           // sessionMap.clear();
        }
        CommonLogger.businessLogger.debug("All session colsed & removed successfully");
    }

    public static void initLivenessThread() {
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Manager.initLivenessThread() Invoked...").build());
        // initializeSystemProperties();

        ConcurrentHashMap<String, ExecutorService> executorServices = new ConcurrentHashMap<String, ExecutorService>();

        executorServices.put("archiverRunnables", archiverTaskExecutor.getExecutorService());
        executorServices.put("sendSmsIntegrationArchiver", sendSmsIntegrationArchiverTaskExecutor.getExecutorService());
        /*
		 * already included in the main executer ConcurrentHashMap <String,Runnable>
		 * runnables = new ConcurrentHashMap <String,Runnable> ();
		 * runnables.put("monitorRunnable",monitorRunnable);
		 * runnables.put("reloadRunnable",reloaderRunnable);
		 * runnables.put("sessionTimeoutRunnable",sessionTimeoutRunnable);
		 * runnables.put("serverConnectionListener",serverConnectionListener);
         */
        livenessThread = new LivenessThread(isShutdown, null, executorServices, null, CommonLogger.livenessLogger,
                CommonLogger.businessLogger, Integer.parseInt(systemProperities
                        .get(Defines.SMSC_INTERFACE_PROPERTIES.SMSC_INTERFACE_LIVENESS_THREAD_SLEEP_TIME)));
        livenessThread.start();
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Manager.initLivenessThread() Ended...").build());
    }

    public static void registerConsumers() throws CommonException, IOException {
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Registering RabbitMQ Consumers...").build());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Getting RabbitMQ connection...").build());
        consumersConnection = RabbitmqUtil.getConnection();
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Get RabbitMQ connection successfully").build());

        for (Entry<String, ServiceModel> entry : queueServiceMap.entrySet()) {

            dequeuedDeliverRequests.put(entry.getKey(), new ArrayBlockingQueue<>(Defines.SMSC_INTERFACE_PROPERTIES.DEQUEUED_DELIVER_QUEUE_SIZE_VALUE));

            for (int i = 0; i < Defines.SMSC_INTERFACE_PROPERTIES.RABBITMQ_CONSUMERS_NUMBER_VALUE; i++) {
                Channel channel = RabbitmqUtil.getChannel(consumersConnection);
                consumersChannels.add(channel);
                DeliverCallback deliverSmDeliverCallback = (consumerTag, delivery) -> {
                    EnqueueModelDeliverSM enqueueModel;
                    try {
                        enqueueModel = Utility.convertJsonBytesToEnqueueModelDeliverSM(delivery.getBody());

                        if (enqueueModel != null) {
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                                    "TransId [" + enqueueModel.getTransId()
                                    + "] | EnqueueModelDeliverSM : " + enqueueModel.toString()).build());
                            dequeuedDeliverRequests.get(entry.getKey()).put(enqueueModel);
                        }
                    } catch (CommonException | InterruptedException ex) {
                        CommonLogger.errorLogger.error("Exception => ", ex);
                    }
                };
                String deliverSmQueueName = systemProperities.get(Defines.SMSC_INTERFACE_PROPERTIES.DELIVER_QUEUE_NAME_PREFIX) + entry.getValue().getSelectedApplicationQueueModel().getAppName();
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Create consumer to " + deliverSmQueueName).build());
                channel.queueDeclare(deliverSmQueueName, true, false, false, null);
                channel.basicConsume(deliverSmQueueName, false, deliverSmDeliverCallback, consumerTag -> {
                });
            }
        }
    }

    public static void shutdownChannels() throws CommonException {
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Starting shutdown Consumers Channels").build());

        for (int i = 0; i < consumersChannels.size(); i++) {
            Channel channel = consumersChannels.get(i);
            RabbitmqUtil.closeChannel(channel);
        }

        RabbitmqUtil.closeConnection(consumersConnection);

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Shutdown Consumers Channels end").build());
    }
}
