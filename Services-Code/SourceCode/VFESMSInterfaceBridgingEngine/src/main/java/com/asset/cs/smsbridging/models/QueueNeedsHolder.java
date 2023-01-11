package com.asset.cs.smsbridging.models;

import com.asset.contactstrategy.common.models.QueueModel;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author aya.moawed
 */
public class QueueNeedsHolder {

    private QueueModel model;
    //Tells if the app queue no longer exist and it has to be emptied
    private boolean shutDown;
    //Holds the connections to queue
   // private DataSourceManager connection;
    //Holds pool of dequeuer threads for each app queue
    private ExecutorService dequeuerPool;
    
    
    private ExecutorService enqueuerPool;

    public ExecutorService getEnqueuerPool() {
        return enqueuerPool;
    }

    public void setEnqueuerPool(ExecutorService enqueuerPool) {
        this.enqueuerPool = enqueuerPool;
    }

    public QueueModel getModel() {
        return model;
    }

    public void setModel(QueueModel model) {
        this.model = model;
    }

    public boolean isShutDown() {
        return shutDown;
    }

    public void setShutDown(boolean shutDown) {
        this.shutDown = shutDown;
    }

//    public DataSourceManager getConnection() {
//        return connection;
//    }
//
//    public void setConnection(DataSourceManager connection) {
//        this.connection = connection;
//    }

    public ExecutorService getDequeuerPool() {
        return dequeuerPool;
    }

    public void setDequeuerPool(ExecutorService dequeuerPool) {
        this.dequeuerPool = dequeuerPool;
    }

}
