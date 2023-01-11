/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.managerEngine.workers;

/**
 *
 * @author nancy.abdelgawad
 */
public class CCWorker extends Thread {
    
    private boolean workerShutDownFlag = false;
    
    private String workerId;

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
    
    public boolean isWorkerShutDownFlag() {
        return workerShutDownFlag;
    }

    public void setWorkerShutDownFlag(boolean workerShutDownFlag) {
        this.workerShutDownFlag = workerShutDownFlag;
    }

    
    
    
}
