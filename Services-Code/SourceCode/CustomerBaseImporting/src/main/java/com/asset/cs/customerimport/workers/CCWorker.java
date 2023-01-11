/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.customerimport.workers;

/**
 *
 * @author nancy.abdelgawad
 */
public class CCWorker extends Thread {
    
    private boolean workerShutDownFlag = false;
    private boolean workerAlive;
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

    public boolean isWorkerAlive() {
        return workerAlive;
    }

    public void setWorkerAlive(boolean workerAlive) {
        this.workerAlive = workerAlive;
    }
    
    
    
}
