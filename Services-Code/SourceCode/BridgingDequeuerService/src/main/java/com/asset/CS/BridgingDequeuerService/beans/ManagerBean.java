/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.beans;

import com.asset.CS.BridgingDequeuerService.Threads.MonitorThread;
import com.asset.CS.BridgingDequeuerService.models.QueueNeedsHolder;
import com.asset.CS.BridgingDequeuerService.Threads.ReloadingThread;
import com.asset.CS.BridgingDequeuerService.configuration.SpringConfiguration;
import com.asset.CS.BridgingDequeuerService.services.MainService;
import com.asset.contactstrategy.common.controller.EngineManager;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author mostafa.kashif
 */
public class ManagerBean extends EngineManager {
    
    public HashMap<String, QueueNeedsHolder> workingAppQueues = new HashMap<>();
    public static AtomicBoolean DEQUEUER_SERVICE_SHUTFOWN_FLAG = new AtomicBoolean(false);
    public static AtomicLong smsControllerSuccessCount = new AtomicLong(0);
    public static AtomicLong smsControllerFailureCount = new AtomicLong(0);
    public static AtomicLong smsControllerTotalCount = new AtomicLong(0);
    public static AtomicLong readinessControllerSuccessCount = new AtomicLong(0);
    public static AtomicLong readinessControllerFailureCount = new AtomicLong(0);
    public static AtomicLong readinessControllerTotalCount = new AtomicLong(0);
    public static AtomicLong livenessControllerSuccessCount = new AtomicLong(0);
    public static AtomicLong livenessControllerFailureCount = new AtomicLong(0);
    public static AtomicLong livenessControllerTotalCount = new AtomicLong(0);
    public static AtomicLong concurrentCount = new AtomicLong(0);
    public static HashMap<String, SystemPropertiesModel> systemPropertiesMap = new HashMap<String, SystemPropertiesModel>();
//    @Autowired
//    public InitializationBean initializationBean;

    @Autowired
    public MainService databaseService;
    
    public ManagerBean() {
    }
    
    @Autowired
    private ReloadingThread reloadingThread;
    
    @Autowired
    private MonitorThread monitorThread;
    
    public MonitorThread getMonitorThread() {
        return monitorThread;
    }
    
    public void setMonitorThread(MonitorThread monitorThread) {
        this.monitorThread = monitorThread;
    }
    
    public ReloadingThread getReloadingThread() {
        return reloadingThread;
    }
    
    public void setReloadingThread(ReloadingThread reloadingThread) {
        this.reloadingThread = reloadingThread;
    }
    
    public ManagerBean(MainService databaseService) {
        try {
            this.databaseService = databaseService;
            systemPropertiesMap = databaseService.getPropertiesPerInterface(GeneralConstants.SRC_ID_DEQUEUER_WEB_SERVICE);
            prepareArrayBlockingQueuesForApplicationQueues();
            //      startReloadThread();
        } catch (CommonException ex) {
            Logger.getLogger(ManagerBean.class.getName()).log(Level.SEVERE, null, ex);
            // throw ex;
        }
    }
    
    public void prepareArrayBlockingQueuesForApplicationQueues() throws CommonException {
        CommonLogger.businessLogger.info("Preparing Application Queues Information ..");
        ArrayList<QueueModel> queueModels = (ArrayList<QueueModel>) databaseService.getProceduresQueues();
        if (queueModels != null && queueModels.size() > 0) {
            ArrayList<QueueModel> appQueuesCloned = (ArrayList<QueueModel>) queueModels.clone();
            for (QueueModel model : queueModels) {
                if (workingAppQueues.get(model.getAppName()) == null) {
                    
                    addNewApplicationQueue(model);
                }
                appQueuesCloned.remove(model);
            }
            for (QueueModel model : appQueuesCloned) {
                shutdownExistingQueue(model);
            }
        } else {
            //CommonLogger.businessLogger.info("No procedure queues were found for the given queue names");
            CommonLogger.businessLogger.info("No procedure queues were found in database");
        }
        
    }
    
    private void addNewApplicationQueue(QueueModel model) throws CommonException {
//        CommonLogger.businessLogger.debug("Found new queue with APP_NAME:" + model.getAppName() + " Preparing connections");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found new queue... preparing connections")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, model.getAppName()).build());
        QueueNeedsHolder queue = (QueueNeedsHolder) SpringConfiguration.applicationContext.getBean(QueueNeedsHolder.class);
        queue.setShutDown(false);
        queue.setModel(model);
        //queue.setConnection((DataSource) (DataSourceBuilder.create().username(model.getSchemaName()).password(Utility.decrypt(model.getSchemaPassword(), Defines.ENCRYPTION_KEY))).url(model.getDatabaseUrl()).driverClassName(Defines.C3P0_DRIVER_CLASS).build());

//        ComboPooledDataSource c3p0ConnectionPool = new ComboPooledDataSource();
//        try {
//            c3p0ConnectionPool.setDriverClass(Defines.C3P0_DRIVER_CLASS);
//        } catch (PropertyVetoException ex) {
//            Logger.getLogger(ManagerBean.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        c3p0ConnectionPool.setJdbcUrl(model.getDatabaseUrl());
//        c3p0ConnectionPool.setUser(model.getSchemaName());
//        c3p0ConnectionPool.setPassword(Utility.decrypt(model.getSchemaPassword(), Defines.ENCRYPTION_KEY));
//        c3p0ConnectionPool.setAcquireIncrement(Defines.QUEUE_C3P0_ACQUIRE_INCREMENT_VALUE);
//        c3p0ConnectionPool.setCheckoutTimeout(Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE);
//        c3p0ConnectionPool.setInitialPoolSize(Defines.ENQUEUE_C3P0_INITIAL_POOL_SIZE_VALUE);
//        c3p0ConnectionPool.setMaxConnectionAge(Defines.ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE);
//        c3p0ConnectionPool.setMaxIdleTime(Defines.ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE);
//        c3p0ConnectionPool.setMaxPoolSize(Defines.ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE);
//        c3p0ConnectionPool.setMaxStatements(Defines.ENQUEUE_C3P0_MAX_STATEMENTS_VALUE);
//        c3p0ConnectionPool.setMinPoolSize(Defines.ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(Defines.C3P0_DRIVER_CLASS);
        hikariConfig.setJdbcUrl(model.getDatabaseUrl());
        hikariConfig.setUsername(model.getSchemaName());
        hikariConfig.setPassword(Utility.decrypt(model.getSchemaPassword(), Defines.ENCRYPTION_KEY));
        hikariConfig.setConnectionTimeout(Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE);
        hikariConfig.setMaxLifetime(Defines.ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE);
        hikariConfig.setIdleTimeout(Defines.ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE);
        hikariConfig.setMaximumPoolSize(Defines.ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE);
        hikariConfig.setMinimumIdle(Defines.ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE);
        hikariConfig.setPoolName("Hikari-BridgingDequeuerService-" + Thread.currentThread().getName());
        HikariDataSource hikariDataSourse = new HikariDataSource(hikariConfig);
        
        queue.setConnection(hikariDataSourse);
        
        queue.setJdbcTemplate(new JdbcTemplate(queue.getConnection()));
        // DataSource datasource =(DataSource) SpringConfiguration.applicationContext.getBean("queuesDatasource");

//        DataSourceManager queueDatasource =queue.getConnection();
//        queueDatasource.setDbUrl(model.getDatabaseURL());
//        queueDatasource.setDriverClass(Defines.C3P0_DRIVER_CLASS);
//                queueDatasource.setUsername( model.getSchemaName());
//                queueDatasource.setPassword(Utility.decrypt(model.getSchemaPassword(), Defines.ENCRYPTION_KEY));
//                queueDatasource.setDbUrl(model.getDatabaseURL());
        // queue.setConnection(new DataSourceManager(model.getDatabaseURL(), model.getSchemaName(), Utility.decrypt(model.getSchemaPassword(), Defines.ENCRYPTION_KEY)));
        workingAppQueues.put(queue.getModel().getAppName(), queue);
        
    }
    
    private void shutdownExistingQueue(QueueModel model) {
//        CommonLogger.businessLogger.debug("Shutting down queue with APP_NAME:" + model.getAppName() + " .. queue will stop its dequeing.. queue will be removed when all its curent sms are handled");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Shutting down queue ... queue will stop its dequeing.. queue will be removed when all its curent sms are handled")
                .put(GeneralConstants.StructuredLogKeys.APP_NAME, model.getAppName()).build());
        workingAppQueues.remove(model.getAppName());
    }

//  private  void startReloadThread() {
//        this.reloadingThread=(ReloadingThread)SpringConfiguration.applicationContext.getBean(ReloadingThread.class);
//          this.reloadingThread.start();
//    }
    @PostConstruct
    public void startReloadThread() throws Exception {

        // this.reloadingThread=(ReloadingThread)SpringConfiguration.applicationContext.getBean(ReloadingThread.class);
        this.reloadingThread.start();
    }
    
    @PostConstruct
    public void startMonitorThread() throws Exception {

        // this.reloadingThread=(ReloadingThread)SpringConfiguration.applicationContext.getBean(ReloadingThread.class);
        this.monitorThread.start();
    }
    
    @Override
    public void shutdown() {
        try {
            CommonLogger.businessLogger.info("Starting shutting down dequeuer Web Service");
            ManagerBean.DEQUEUER_SERVICE_SHUTFOWN_FLAG.set(true);
            if (reloadingThread != null) {
                reloadingThread.join();
            }
            if (monitorThread != null) {
                monitorThread.join();
            }
//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew((System.currentTimeMillis()))));
            
        } catch (InterruptedException ex) {
            Logger.getLogger(ManagerBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
