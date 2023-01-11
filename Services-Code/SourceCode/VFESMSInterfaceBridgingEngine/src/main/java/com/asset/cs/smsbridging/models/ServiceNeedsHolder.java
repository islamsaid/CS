package com.asset.cs.smsbridging.models;

import com.asset.contactstrategy.common.models.SMSBridgeJSONStructure;
import com.asset.contactstrategy.common.models.ServiceModel;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author aya.moawed 2595
 */
public class ServiceNeedsHolder {

    private ServiceModel model;
    private ArrayBlockingQueue<SMSBridgeJSONStructure> queueForDequeuerResult;
    private boolean shutDown;

    public ServiceModel getModel() {
        return model;
    }

    public void setModel(ServiceModel model) {
        this.model = model;
    }

    public ArrayBlockingQueue<SMSBridgeJSONStructure> getQueueForDequeuerResult() {
        return queueForDequeuerResult;
    }

    public void setQueueForDequeuerResult(ArrayBlockingQueue<SMSBridgeJSONStructure> queueForDequeuerResult) {
        this.queueForDequeuerResult = queueForDequeuerResult;
    }

    public boolean isShutDown() {
        return shutDown;
    }

    public void setShutDown(boolean shutDown) {
        this.shutDown = shutDown;
    }
    
}
