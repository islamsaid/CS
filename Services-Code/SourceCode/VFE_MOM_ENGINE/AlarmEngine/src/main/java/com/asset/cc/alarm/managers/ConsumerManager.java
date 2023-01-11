/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cc.alarm.managers;

import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.cc.alarm.constants.Defines;
import com.asset.cc.alarm.services.CCWorker;
import com.asset.cc.alarm.services.ConsumerWorker;
import java.util.Vector;

/**
 *
 * @author mahmoud.abdou
 */
public class ConsumerManager {

    private static final String CLASS_NAME = "com.asset.cc.alarm.managers.ConsumerManager";
    private static Vector<ConsumerWorker> workersList = new Vector<ConsumerWorker>();

    public static void startConsumerManager() throws EngineException {
        String methodName = "start";
        EngineLogger.debugLogger.info("-------------- Starting Consumer Manager ---------------");
        try {
            int no_of_threads = Integer.valueOf(ShutdownManager.getPropertiesMap().get(EngineDefines.No_OF_WORKER_THREADS).toString()).intValue();
//            int no_of_threads = ResourcesCachingManager.getIntValue(Defines.No_OF_WORKER_THREADS);
            EngineLogger.debugLogger.debug("No Of Worker Threads are: " + no_of_threads);
            for (int x = 0; x < no_of_threads; x++) {
                ConsumerWorker workerThread = new ConsumerWorker();
                workerThread.setName(x + 1 + "_" + no_of_threads);
                workersList.add(workerThread);
                workerThread.start();
            }
        } catch (Exception e) {
            throw processException(e, methodName);
        }
        EngineLogger.debugLogger.info("-------------- Ending Consumer Manager ---------------");
    }

    public static void shutdownConsumerManager() {
        EngineLogger.debugLogger.info("-------------- Shutdown Consumer Manager ---------------");

        for (CCWorker worker : workersList) {
            worker.setWorkerShutDownFlag(true);
        }
    }

    private static EngineException processException(Exception e, String methodName) {
        // Handle Contextual Campaign Exception 
        if (e instanceof EngineException) {
            return (EngineException) e;
        } // Handle other exception types
        else {
            return new EngineException(e,EngineErrorCodes.UNKNOWN_ERROR, "Unknown error in CLASS:" + CLASS_NAME + " method: " + methodName);
        }
    }
}
