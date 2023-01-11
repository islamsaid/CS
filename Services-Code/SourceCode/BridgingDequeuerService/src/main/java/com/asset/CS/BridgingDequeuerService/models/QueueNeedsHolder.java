package com.asset.CS.BridgingDequeuerService.models;

import com.asset.contactstrategy.common.models.QueueModel;
import java.util.concurrent.ExecutorService;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author aya.moawed
 */
public class QueueNeedsHolder {

    private QueueModel model;
    //Tells if the app queue no longer exist and it has to be emptied
    private boolean shutDown;
    //Holds the connections to queue
    
    private DataSource connection;
    //Holds pool of dequeuer threads for each app queue
    private ExecutorService dequeuerPool;
    
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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

    public DataSource getConnection() {
        return connection;
    }

    public void setConnection(DataSource connection) {
        this.connection = connection;
    }

    public ExecutorService getDequeuerPool() {
        return dequeuerPool;
    }

    public void setDequeuerPool(ExecutorService dequeuerPool) {
        this.dequeuerPool = dequeuerPool;
    }

}
