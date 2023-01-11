/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfacereports.common;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.controller.Initializer;
import com.asset.contactstrategy.common.dao.SMSHistoryDao;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfacereports.threads.LoggingManagerThread;
import com.asset.contactstrategy.interfacereports.threads.MonitorThread;
import com.asset.contactstrategy.interfacereports.threads.ReloadingThread;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hazem.fekry
 */
public class ConfigurationManager extends HttpServlet {

    public static boolean INTERFACE_REPORTS_SHUTDOWN_FLAG = false;
    public static boolean SHUTDOWN_FLAG = false;
    public static boolean MONITOR_THREAD_SHUTDOWN_FLAG = false;
//    public static int INSTANCE_ID;
    public static HashMap<String, String> SRC_ID_SYSTEM_PROPERIES;

    public static ArrayBlockingQueue<InterfacesLogModel> requestsToBeLogged;
    public static ExecutorService logThreadPool;
    public static int MAX_LOGGING_THREAD_POOL_SIZE;

    public static HashMap<Integer, String> statusHashMap = new HashMap<>();

//    public static ArrayBlockingQueue<SMSHistoryModel> messagesToBeArchived;
//    private static ExecutorService archiveThreadPool;
//    private static int MAX_ARCHIVING_THREAD_POOL_SIZE;
//    public static HashMap<Long, ArrayBlockingQueue<SMS>> smsToBeSent;
//    public static HashMap<Long, ExecutorService> enQueueThreadPoolHashMap;
//    public static HashMap<Long, Boolean> shutdownFlagPool;
//    public static HashMap<Long, DataSourceManager> databaseConnectionPool;
//    public static ExecutorService queueThreadPool;
//    private static int MAX_SMS_SEND_QUEUE_SIZE;
//    private static int MAX_QUEUE_THREAD_POOL_SIZE;
    public static AtomicLong concurrentRequests;

    public static AtomicLong retrieveMessageStatusAccumulator;
    public static AtomicLong retrieveMessageStatusSuccessCount;
    public static AtomicLong retrieveMessageStatusFailedCount;

    public static AtomicLong retrieveSMSsInterfaceAccumulator;
    public static AtomicLong retrieveSMSsInterfaceSuccessCount;
    public static AtomicLong retrieveSMSsInterfaceFailedCount;

    public static AtomicLong checkInterfaceAccumulator;
    public static AtomicLong checkInterfaceSuccessCount;
    public static AtomicLong checkInterfaceFailedCount;

    public static AtomicLong readyCheckAccumulator;
    public static AtomicLong readyCheckSuccessCount;
    public static AtomicLong readyCheckFailedCount;

    public static Thread reloadingThread;

    public static Thread monitoringThread;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        System.out.println("Configuration Manager Servlet Initialized");

        try {
            Defines.runningProjectId = GeneralConstants.SRC_ID_INTERFACE_REPORTS;
            initializeAll();

            Field[] fields = Defines.VFE_CS_SMS_H_STATUS_LK.class.getFields();
            for (int i = 0; i < fields.length; i++) {
                try {
                    statusHashMap.put(fields[i].getInt(null), fields[i].getName());
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(SMSHistoryDao.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(SMSHistoryDao.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            concurrentRequests = new AtomicLong(0);

            retrieveMessageStatusAccumulator = new AtomicLong(0);
            retrieveMessageStatusSuccessCount = new AtomicLong(0);
            retrieveMessageStatusFailedCount = new AtomicLong(0);

            retrieveSMSsInterfaceAccumulator = new AtomicLong(0);
            retrieveSMSsInterfaceSuccessCount = new AtomicLong(0);
            retrieveSMSsInterfaceFailedCount = new AtomicLong(0);

            checkInterfaceAccumulator = new AtomicLong(0);
            checkInterfaceSuccessCount = new AtomicLong(0);
            checkInterfaceFailedCount = new AtomicLong(0);

            readyCheckAccumulator = new AtomicLong(0);
            readyCheckSuccessCount = new AtomicLong(0);
            readyCheckFailedCount = new AtomicLong(0);

            Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE = Long.parseLong(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_CONCURRENT_REQUESTS));
            Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE));

            Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME_VALUE = Integer.parseInt(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.RELOAD_THREAD_SLEEP_TIME));
            //Initializing All Queues
//            messagesToBeArchived = new ArrayBlockingQueue<>(Integer.valueOf(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.MAX_ARCHIVING_QUEUE_SIZE)));
            requestsToBeLogged = new ArrayBlockingQueue<>(Defines.INTERFACES.MAX_LOGGING_QUEUE_SIZE_VALUE);
//            INSTANCE_ID = Integer.parseInt(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.INSTANCE_ID));
            MONITOR_THREAD_SHUTDOWN_FLAG = false;
            INTERFACE_REPORTS_SHUTDOWN_FLAG = false;
            SHUTDOWN_FLAG = false;

            //Initializing SMS Queues
//            smsToBeSent = new HashMap<>();
//            enQueueThreadPoolHashMap = new HashMap<>();
//            shutdownFlagPool = new HashMap<>();
//            databaseConnectionPool = new HashMap<>();
//            MAX_SMS_SEND_QUEUE_SIZE = Integer.valueOf(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.MAX_SMS_SEND_QUEUE_SIZE));
//            MAX_QUEUE_THREAD_POOL_SIZE = Integer.valueOf(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.MAX_QUEUE_THREAD_POOL_SIZE));
//            CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//            CommonLogger.businessLogger.info("//Enqueue Queues/////////////////////////////////////////////////////////////////////");
//            for (Map.Entry<Long, QueueModel> queue : SystemLookups.QUEUE_LIST.entrySet())
//            {
//                CommonLogger.businessLogger.info("//QueueName: " + queue.getValue().getAppName() + " Queue Id: " + queue.getKey());
//                
//                smsToBeSent.put(queue.getKey(), new ArrayBlockingQueue<SMS>(MAX_SMS_SEND_QUEUE_SIZE));
//                CommonLogger.businessLogger.info("//Java Queue MaxSize: " + MAX_SMS_SEND_QUEUE_SIZE);
//                
//                shutdownFlagPool.put(queue.getKey(), Boolean.FALSE);
//                CommonLogger.businessLogger.info("//Shutdown Flag: " + Boolean.FALSE);
//                
//                databaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseURL(), 
//                        queue.getValue().getSchemaName(), queue.getValue().getSchemaPassword()));
//                CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseURL());
//                CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
//                
//                enQueueThreadPoolHashMap.put(queue.getKey(), Executors.newFixedThreadPool(MAX_QUEUE_THREAD_POOL_SIZE));
//                CommonLogger.businessLogger.info("//Thread Pool Max Size: " + MAX_QUEUE_THREAD_POOL_SIZE);
//                
//                for (int i = 0; i < MAX_QUEUE_THREAD_POOL_SIZE; i++)
//                {
//                    enQueueThreadPoolHashMap.get(queue.getKey()).execute(new EnQueueManagerThread(queue.getKey(), String.valueOf(queue.getValue().getAppName())));
//                }
//                CommonLogger.businessLogger.info("//Threads Initialized: " + MAX_QUEUE_THREAD_POOL_SIZE);
//                CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//            }
            MAX_LOGGING_THREAD_POOL_SIZE = Defines.INTERFACES.MAX_LOGGING_THREAD_POOL_SIZE_VALUE;
            logThreadPool = Executors.newFixedThreadPool(MAX_LOGGING_THREAD_POOL_SIZE);
            for (int i = 0; i < MAX_LOGGING_THREAD_POOL_SIZE; i++) {
                logThreadPool.execute(new LoggingManagerThread(i));
            }
//            CommonLogger.businessLogger.info("Logging Thread Pool MaxSize: " + MAX_LOGGING_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("Logging Threads Initialized: " + MAX_LOGGING_THREAD_POOL_SIZE);
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Logging Thread Pool Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_MAX_SIZE, MAX_LOGGING_THREAD_POOL_SIZE)
                    .put(GeneralConstants.StructuredLogKeys.THREADS_INITIALIZED, MAX_LOGGING_THREAD_POOL_SIZE).build());

//            MAX_ARCHIVING_THREAD_POOL_SIZE = Integer.valueOf(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.MAX_ARCHIVING_THREAD_POOL_SIZE));
//            archiveThreadPool = Executors.newFixedThreadPool(MAX_ARCHIVING_THREAD_POOL_SIZE);
//            for (int i = 0; i < MAX_ARCHIVING_THREAD_POOL_SIZE; i++)
//            {
//                archiveThreadPool.execute(new ArchiveManagerThread(i));
//            }
//            CommonLogger.businessLogger.info("Archiving Thread Pool MaxSize: " + MAX_ARCHIVING_THREAD_POOL_SIZE);
//            CommonLogger.businessLogger.info("Archiving Threads Initialized: " + MAX_ARCHIVING_THREAD_POOL_SIZE);
            reloadingThread = new Thread(new ReloadingThread());
            reloadingThread.start();
            CommonLogger.businessLogger.info("Reloading Thread Initialized");

            monitoringThread = new Thread(new MonitorThread());
            monitoringThread.start();
            CommonLogger.businessLogger.info("Monitoring Thread Initialized");

            CommonLogger.businessLogger.info("Interface Reports Started Successfully");
        } catch (CommonException e) {
            CommonLogger.businessLogger.info("Error While Initializing");
            CommonLogger.businessLogger.error(e.getErrorMsg());
            CommonLogger.errorLogger.error(e.getErrorMsg(), e);
            throw new ServletException(e.getErrorMsg());
        } catch (Exception e) {
            CommonLogger.businessLogger.info("Error While Initializing");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }

    }

    private void initializeAll() throws Exception {
        Initializer.readPropertiesFile();
        Initializer.initializeLoggers();
        Initializer.initializeDataSource();
        Initializer.loadInterfacesLookups();
        Initializer.loadInterfacesData();
        Initializer.loadSystemProperties();
        //Initializer.loadAppsQueues();
        MainService mainService = new MainService();
        SRC_ID_SYSTEM_PROPERIES = mainService.getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_INTERFACE_REPORTS);
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            reloadData();
            out.println("<h1>Refresh Done</h1>");
        } catch (Exception e) {
            out.println("<h1>Error While Refreshing</h1>");
            out.println("<h2>" + e.getMessage() + "</h2>");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    @Override
    public void destroy() {
        INTERFACE_REPORTS_SHUTDOWN_FLAG = true;
        CommonLogger.businessLogger.info("INTERFACE_REPORTS_SHUTDOWN_FLAG was Rasied...");

        try {
//            for (Map.Entry<Long, ExecutorService> queueThreadPool : enQueueThreadPoolHashMap.entrySet())
//            {
//                shutdownFlagPool.put(queueThreadPool.getKey(), Boolean.TRUE);
//                queueThreadPool.getValue().shutdown();
//                CommonLogger.businessLogger.info("Shutdoown Flag was rasied and Shutdown signal was sent for Enqueuer Thread: " + SystemLookups.QUEUE_LIST.get(queueThreadPool.getKey()).getAppName());
//            }
//            
//            Iterator enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
//            Long queueId;
//            while (enQueueThreadPool.hasNext())
//            {
//                queueId = Long.valueOf(String.valueOf(enQueueThreadPool.next()));
//                CommonLogger.businessLogger.info("Awaiting Termination for Enqueue Thread: " + queueId);
//                enQueueThreadPoolHashMap.get(queueId).awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
//                enQueueThreadPoolHashMap.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue ThreadPool for Queue " + queueId);
//                shutdownFlagPool.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue Flag for Queue " + queueId);
//                smsToBeSent.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue Queue for Queue " + queueId);
//                CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(databaseConnectionPool.get(queueId).getC3p0ConnectionPool()));
//                databaseConnectionPool.get(queueId).closeConnectionPool();
//                databaseConnectionPool.remove(queueId);
//                CommonLogger.businessLogger.info("Removed Enqueue DatabasePool for Queue " + queueId);
//                enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
//            }

            SHUTDOWN_FLAG = true;

            logThreadPool.shutdown();
//            archiveThreadPool.shutdown();

            logThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            CommonLogger.businessLogger.info("Logging Thread Pool Closed Successfully");
//            archiveThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
//            CommonLogger.businessLogger.info("Archiving Thread Pool Closed Successfully");
            reloadingThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("Reloading Thread Closed Successfully");

            MONITOR_THREAD_SHUTDOWN_FLAG = true;
            monitoringThread.join(Long.MAX_VALUE);
            CommonLogger.businessLogger.info("Monitoring Thread Closed Successfully");

            DataSourceManger.closeConnectionPool();
            CommonLogger.businessLogger.info("DataSourceManager Closed Successfully");

//            CommonLogger.businessLogger.info("Request Logging Queue Details ||  Size: " + requestsToBeLogged.size()
//                    + " | Remaining Capacity: " + requestsToBeLogged.remainingCapacity());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Request Logging Queue Details")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, requestsToBeLogged.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_REMAINING_CAPACITY, requestsToBeLogged.remainingCapacity()).build());

//            CommonLogger.businessLogger.info("***************************************Threads dump start*********************");
//            CommonLogger.businessLogger.info(Utility.generateThreadDump(System.currentTimeMillis()));
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threads Dump Stats")
                    .put(GeneralConstants.StructuredLogKeys.THREAD_DUMP, Utility.generateThreadDumpNew(System.currentTimeMillis())).build());
//            CommonLogger.businessLogger.info("***************************************Threads dump End*********************");

            CommonLogger.businessLogger.info("Interface Reports UnDeployed Successfully");
        } catch (InterruptedException e) {
            CommonLogger.businessLogger.info("Error While Shutting Down Threads");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        } catch (Exception e) {
            CommonLogger.businessLogger.info("Error While Shutting Down Threads");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

    public static void reloadData() {
        try {
//            CommonLogger.businessLogger.info("Started Reloading Data..");
//            CommonLogger.businessLogger.info("Current Data...");
//            CommonLogger.businessLogger.info("Current Services Count: " + SystemLookups.SERVICES.size());
//            CommonLogger.businessLogger.info("Current Queues Count: " + SystemLookups.QUEUE_LIST.size());
//            CommonLogger.businessLogger.info("Current AdsGroups Count: " + SystemLookups.ADS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SmsGroups Count: " + SystemLookups.SMS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SystemProperties Count: " + SystemLookups.SYSTEM_PROPERTIES.size());

            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Reloading Thread Stats...")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_COUNT, SystemLookups.SERVICES.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, SystemLookups.QUEUE_LIST.size())
                    .put(GeneralConstants.StructuredLogKeys.ADS_GROUPS_COUNT, SystemLookups.ADS_GROUPS.size())
                    .put(GeneralConstants.StructuredLogKeys.SMS_GROUPS_COUNT, SystemLookups.SMS_GROUPS.size())
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_COUNT, SystemLookups.SYSTEM_PROPERTIES.size()).build());

            Initializer.loadInterfacesData();
            Initializer.loadSystemProperties();
            //Initializer.loadAppsQueues();

            SRC_ID_SYSTEM_PROPERIES = new MainService().getSystemPropertiesByGroupID(GeneralConstants.SRC_ID_INTERFACE_REPORTS);

            Defines.INTERFACES.MAX_CONCURRENT_REQUESTS_VALUE = Long.parseLong(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_CONCURRENT_REQUESTS));
            Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE = Float.parseFloat(SRC_ID_SYSTEM_PROPERIES.get(Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE));
//            for (Map.Entry<Long, QueueModel> queue : SystemLookups.QUEUE_LIST.entrySet())
//            {
//                if (!enQueueThreadPoolHashMap.containsKey(queue.getKey()))
//                {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Adding New Enqueue Queue//////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//QueueName: " + queue.getValue().getAppName() + " Queue Id: " + queue.getKey());
//                
//                    smsToBeSent.put(queue.getKey(), new ArrayBlockingQueue<SMS>(MAX_SMS_SEND_QUEUE_SIZE));
//                    CommonLogger.businessLogger.info("//Java Queue MaxSize: " + MAX_SMS_SEND_QUEUE_SIZE);
//
//                    shutdownFlagPool.put(queue.getKey(), Boolean.FALSE);
//                    CommonLogger.businessLogger.info("//Shutdown Flag: " + Boolean.FALSE);
//
//                    databaseConnectionPool.put(queue.getKey(), new DataSourceManager(queue.getValue().getDatabaseURL(), 
//                            queue.getValue().getSchemaName(), queue.getValue().getSchemaPassword()));
//                    CommonLogger.businessLogger.info("//Database Connection Pool: ");
//                    CommonLogger.businessLogger.info("//    URL: " + queue.getValue().getDatabaseURL());
//                    CommonLogger.businessLogger.info("//    UserName: " + queue.getValue().getSchemaName());
//                    CommonLogger.businessLogger.info("//    Password: " + queue.getValue().getSchemaPassword());
//
//                    enQueueThreadPoolHashMap.put(queue.getKey(), Executors.newFixedThreadPool(MAX_QUEUE_THREAD_POOL_SIZE));
//                    CommonLogger.businessLogger.info("//Thread Pool Max Size: " + MAX_QUEUE_THREAD_POOL_SIZE);
//
//                    for (int i = 0; i < MAX_QUEUE_THREAD_POOL_SIZE; i++)
//                    {
//                        enQueueThreadPoolHashMap.get(queue.getKey()).execute(new EnQueueManagerThread(queue.getKey(), String.valueOf(queue.getValue().getAppName())));
//                    }
//                    CommonLogger.businessLogger.info("//Threads Initialized: " + MAX_QUEUE_THREAD_POOL_SIZE);
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                }
//            }
//            
//            Iterator enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
//            Long queueId;
//            while (enQueueThreadPool.hasNext())
//            {
//                queueId = Long.valueOf(String.valueOf(enQueueThreadPool.next()));
//                if (!SystemLookups.QUEUE_LIST.containsKey(queueId))
//                {
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//Removing Enqueue Queue/////////////////////////////////////////////////////////////");
//                    CommonLogger.businessLogger.info("//QueueName: " + SystemLookups.QUEUE_LIST.get(queueId).getAppName() + " Queue Id: " + SystemLookups.QUEUE_LIST.get(queueId));
//                    
//                    shutdownFlagPool.put(queueId, Boolean.TRUE);
//                    enQueueThreadPoolHashMap.get(queueId).shutdown();
//                    enQueueThreadPoolHashMap.get(queueId).awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
//                    
//                    smsToBeSent.remove(queueId);
//                    CommonLogger.businessLogger.info("//Java Queue Closed And Removed");
//                    
//                    CommonLogger.businessLogger.info(Utility.getC3P0ConnectionPoolStats(databaseConnectionPool.get(queueId).getC3p0ConnectionPool()));
//                    databaseConnectionPool.get(queueId).closeConnectionPool();
//                    databaseConnectionPool.remove(queueId);
//                    CommonLogger.businessLogger.info("//DatabasePool Closed And Removed");
//                    
//                    enQueueThreadPoolHashMap.remove(queueId);
//                    CommonLogger.businessLogger.info("//Thread Pool Closed And Removed");
//                    
//                    shutdownFlagPool.remove(queueId);
//                    CommonLogger.businessLogger.info("//ShutdownFlag Closed And Removed");
//                    CommonLogger.businessLogger.info("/////////////////////////////////////////////////////////////////////////////////////");
//                    enQueueThreadPool = enQueueThreadPoolHashMap.keySet().iterator();
//                }
//            }

//            CommonLogger.businessLogger.info("Updated Data...");
//            CommonLogger.businessLogger.info("Current Services Count: " + SystemLookups.SERVICES.size());
//            CommonLogger.businessLogger.info("Current Queues Count: " + SystemLookups.QUEUE_LIST.size());
//            CommonLogger.businessLogger.info("Current AdsGroups Count: " + SystemLookups.ADS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SmsGroups Count: " + SystemLookups.SMS_GROUPS.size());
//            CommonLogger.businessLogger.info("Current SystemProperties Count: " + SystemLookups.SYSTEM_PROPERTIES.size());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Updated Data")
                    .put(GeneralConstants.StructuredLogKeys.SERVICE_COUNT, SystemLookups.SERVICES.size())
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_SIZE, SystemLookups.QUEUE_LIST.size())
                    .put(GeneralConstants.StructuredLogKeys.ADS_GROUPS_COUNT, SystemLookups.ADS_GROUPS.size())
                    .put(GeneralConstants.StructuredLogKeys.SMS_GROUPS_COUNT, SystemLookups.SMS_GROUPS.size())
                    .put(GeneralConstants.StructuredLogKeys.SYSTEM_COUNT, SystemLookups.SYSTEM_PROPERTIES.size()).build());
        } catch (CommonException ce) {
            CommonLogger.businessLogger.info("Error While Refreshing Data");
            CommonLogger.businessLogger.error(ce.getErrorMsg());
            CommonLogger.errorLogger.error(ce.getErrorMsg(), ce);
        } //        catch (InterruptedException e) 
        //        {
        //            CommonLogger.businessLogger.info("Error While Refreshing Data");
        //            CommonLogger.businessLogger.error(e.getMessage());
        //            CommonLogger.errorLogger.error(e.getMessage(), e);
        //        }
        catch (Exception e) {
            CommonLogger.businessLogger.info("Error While Refreshing Data");
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }
}
