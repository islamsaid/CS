/**
 * created on: Jan 13, 2018 11:50:52 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.runnables;

import java.text.NumberFormat;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.initializer.Manager;
import com.asset.cs.vfesmscinterface.socket.Session;

/**
 * @author mohamed.morsy
 *
 */
public class MonitorRunnable implements Runnable {

    private NumberFormat format = NumberFormat.getInstance();
    private Runtime runtime = Runtime.getRuntime();

    @Override
    public void run() {
        Thread.currentThread().setName("MonitorThread");
        CommonLogger.businessLogger.debug("MonitorThread started successfully");
        long time;
        while (!Manager.isShutdown.get() || !Manager.sessionMap.isEmpty()) {

            try {

//                StringBuilder sb = new StringBuilder();
//                long maxMemory = runtime.maxMemory();
//                long allocatedMemory = runtime.totalMemory();
//                long freeMemory = runtime.freeMemory();
//                sb.append("Free memory: ");
//                sb.append(format.format(freeMemory / 1024));
//                sb.append("k\n");
//                sb.append("Allocated memory: ");
//                sb.append(format.format(allocatedMemory / 1024));
//                sb.append("k\n");
//                sb.append("Max memory: ");
//                sb.append(format.format(maxMemory / 1024));
//                sb.append("k\n");
//                sb.append("Total free memory: ");
//                sb.append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
//				CommonLogger.businessLogger.debug("========== start monitoring ==========");
//				CommonLogger.businessLogger.debug("memory status : " + sb.toString());
//				CommonLogger.businessLogger.debug("archiver task : " + Manager.archiverTaskExecutor.toString());
//				CommonLogger.businessLogger.debug("send sms integration archiver task : "
//						+ Manager.sendSmsIntegrationArchiverTaskExecutor.toString());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Heap Utilization Statistics [MB]")
                        .put(GeneralConstants.StructuredLogKeys.USED_MEMORY, ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.FREE_MEMORY, (Runtime.getRuntime().freeMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.TOTAL_MEMORY, (Runtime.getRuntime().totalMemory() / (1024 * 1024)))
                        .put(GeneralConstants.StructuredLogKeys.MAX_MEMORY, (Runtime.getRuntime().maxMemory() / (1024 * 1024))).build());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Monitoring")
                        .put(GeneralConstants.StructuredLogKeys.ARCHIVER_TASK, Manager.archiverTaskExecutor.toString())
                        .put(GeneralConstants.StructuredLogKeys.SEND_SMS_ARCHIVER_TASK, Manager.sendSmsIntegrationArchiverTaskExecutor.toString()).build());
//                CommonLogger.businessLogger.debug("num of session is: " + Manager.sessionMap.size());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session Stats")
                        .put(GeneralConstants.StructuredLogKeys.SESSION_COUNT, Manager.sessionMap.size()).build());
                /**
                 * 05 - Aug - 2020 The ConcurrentHashMap converted into a
                 * synchronized hash map to avoid the problem of un-removed
                 * closed sessions from the sessionMap All iterators over the
                 * sessionMap are put inside a synchronized block of the
                 * sessionMap
                 */
                synchronized (Manager.sessionMap) {
                    for (Entry<String, Session> sessionMapEntry : Manager.sessionMap.entrySet()) {
//                    CommonLogger.businessLogger.debug("session: " + sessionMapEntry.getValue());
                        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session stats")
                                .put(GeneralConstants.StructuredLogKeys.SESSION, sessionMapEntry.getValue()).build());
                    }
                }
               CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "DividedMessageMap: "+
                       Session.getDividedMessageMap()).build());
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End Monitoring").build());

                if (!Manager.isShutdown.get()) {
                    time = Long.valueOf(Manager.systemProperities
                            .get(Defines.SMSC_INTERFACE_PROPERTIES.SMSC_INTERFACE_MONITOR_THREAD_SLEEP_TIME));
                } else {
                    time = 1000; // to accelerate print system status while closing
                }
                TimeUnit.MILLISECONDS.sleep(time);

            } catch (Exception e) {
                CommonLogger.businessLogger.debug(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
        CommonLogger.businessLogger.debug("MonitorThread closed successfully");
    }

}
