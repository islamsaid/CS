/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.asset.contactstrategy.interfaces.threads;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.utils.Utility;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mostafa.kashif
 */
public class LivenessThread extends Thread {

    private AtomicBoolean engineShutdownFlag;
    private ArrayList<Boolean> flags;
    private ConcurrentHashMap <String,ExecutorService> executors;
    private ConcurrentHashMap <String,Thread> threads;
    private Logger threadLogger;
    private Logger commonLogger;
     private int  sleepTime;
    
    public ConcurrentHashMap <String,ExecutorService> getExecutors() {
        return executors;
    }

    public void setExecutors(ConcurrentHashMap <String,ExecutorService> executors) {
        this.executors = executors;
    }
    
    public LivenessThread(AtomicBoolean engineShutdownFlag,ArrayList<Boolean> flags, ConcurrentHashMap <String,ExecutorService> executors, ConcurrentHashMap <String,Thread> threads,Logger threadLogger,Logger commonLogger,int sleepTime) {
    this.engineShutdownFlag=engineShutdownFlag;
    this.executors=executors;
    this.flags=flags;
    this.threads=threads;
    this.threadLogger=threadLogger;
    this.commonLogger=commonLogger;
    this.sleepTime=sleepTime;
    }

    
  
    @Override
    public void run() {
        Thread.currentThread().setName("LivenessThread");
        threadLogger.info("*****************LIVENESS THREAD STARTED ***********************");
        while(!engineShutdownFlag.get()){
            try {
                if(Utility.checkInterface(null, executors, null, threads,commonLogger))
                {
                threadLogger.info("Engine is running normally");
                }
                else {
                    threadLogger.info("Engine is not running normally");
                }
                Thread.sleep(sleepTime);
            }catch (InterruptedException ex) {
                threadLogger.error("LivenessThread Caught InterruptedException--->" + ex);
                CommonLogger.errorLogger.error("LivenessThread Caught InterruptedException--->" + ex, ex);
            } catch (Exception ex) {
                threadLogger.error("LivenessThread Caught Exception--->" + ex);
                CommonLogger.errorLogger.error("LivenessThread  Caught Exception--->" + ex, ex);
            }
        }
        threadLogger.info("*****************LIVENESS THREAD ENDED ***********************");
    }
    
}
