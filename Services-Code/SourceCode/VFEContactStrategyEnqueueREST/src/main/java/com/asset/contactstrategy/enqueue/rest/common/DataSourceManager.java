/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.enqueue.rest.common;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import oracle.jdbc.OracleConnection;

/**
 *
 * @author mostafa.kashif
 */
public class DataSourceManager {

    private javax.sql.DataSource ds = null;

    public String dbUrl = null;
    public String username = null;
    public String password = null;
    private int poolMaxSize = 0;
    private String driverClass = null;
//    private ComboPooledDataSource c3p0ConnectionPool = null;
    private HikariDataSource hikariDataSourse = null;
//    public ComboPooledDataSource getC3p0ConnectionPool() 
//    {
//        return c3p0ConnectionPool;
//    }
//
//    public void setC3p0ConnectionPool(ComboPooledDataSource c3p0ConnectionPool) {
//        this.c3p0ConnectionPool = c3p0ConnectionPool;
//    }
    
    public HikariDataSource getHikariDataSourse() {
        return hikariDataSourse;
    }

    public void setHikariDataSourse(HikariDataSource hikariDataSourse) {
        this.hikariDataSourse = hikariDataSourse;
    }
    
    public DataSourceManager(String dbUrl, String username, String password) {
        this.dbUrl = dbUrl;
        this.username = username;
        this.password = password;
    }

    public void init() throws Exception {
        try {
            HikariConfig hikariConfig = null;
            if (CommonLogger.businessLogger != null) {
                CommonLogger.businessLogger.debug("Setting up HikariCP configurations");
            }
            //if (Defines.DB_DRIVER_CLASS != null) {
                //dbUrl = Defines.DB_URL;
                //username = Defines.DB_USERNAME;
                //password = Defines.DB_PASSWORD;
                //driverClass = Defines.DB_DRIVER_CLASS;
                //poolMaxSize = Integer.parseInt(Defines.DB_CONNECTION_POOL_MAX_SIZE);
//                c3p0ConnectionPool = new ComboPooledDataSource();
//                c3p0ConnectionPool.setDriverClass(Defines.DB_STATIC_DRIVER_CLASS);
//                c3p0ConnectionPool.setJdbcUrl(dbUrl);
//                c3p0ConnectionPool.setUser(username);
//                c3p0ConnectionPool.setPassword(password);
//                c3p0ConnectionPool.setAcquireIncrement(Defines.QUEUE_C3P0_ACQUIRE_INCREMENT_VALUE);
//                c3p0ConnectionPool.setCheckoutTimeout(Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE);
//                c3p0ConnectionPool.setInitialPoolSize(Defines.ENQUEUE_C3P0_INITIAL_POOL_SIZE_VALUE);
//                c3p0ConnectionPool.setMaxConnectionAge(Defines.ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE);
//                c3p0ConnectionPool.setMaxIdleTime(Defines.ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE);
//                c3p0ConnectionPool.setMaxPoolSize(Defines.ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE);
//                c3p0ConnectionPool.setMaxStatements(Defines.ENQUEUE_C3P0_MAX_STATEMENTS_VALUE);
//                c3p0ConnectionPool.setMinPoolSize(Defines.ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE);
                
                hikariConfig = new HikariConfig();
                hikariConfig.setDriverClassName(Defines.DB_STATIC_DRIVER_CLASS);
                hikariConfig.setJdbcUrl(dbUrl);
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setConnectionTimeout(Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE);
                hikariConfig.setMaxLifetime(Defines.ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE);
                hikariConfig.setIdleTimeout(Defines.ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE);
                hikariConfig.setMaximumPoolSize(Defines.ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE);
                hikariConfig.setMinimumIdle(Defines.ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE);
                hikariConfig.setPoolName("Hikari-EnqueueREST-" + Thread.currentThread().getName());
                hikariDataSourse = new HikariDataSource(hikariConfig);
                
//            } else {
//                ds = null;
//                Hashtable env = new Hashtable();
//                env.put(Context.INITIAL_CONTEXT_FACTORY, Defines.WEBLOGIC_CONTEXT_FACTORY);
//                env.put(Context.PROVIDER_URL, Defines.WEBLOGIC_PROVIDER_URL);
//                Context context = new InitialContext(env);
//                ds = (javax.sql.DataSource) context.lookup(Defines.DATA_SOURCE_NAME);
//            }
            if (CommonLogger.businessLogger != null) {
                CommonLogger.businessLogger.debug("HikariCP configurations setted successfully");
            }
            System.out.println("DataSource created successfully");
        } 
//        catch (NamingException ex) {
//            if (CommonLogger.errorLogger != null) {
//                CommonLogger.errorLogger.error("Exception in intialize DataSource configurations ", ex);
//            }
//            System.err.println("Sql exception in init()-----> " + ex);
//            // System.err.println("Sql exception in init()-----> " + ex,ex);
//            // throw new JSFTemplateException(ex, ErrorCodes.NAMING_EXCEPTION_DATASOURCE_MANAGER);
//            throw new Exception(ex);
//        } 
        catch (Exception ex) {
            if (CommonLogger.errorLogger != null) {
                CommonLogger.errorLogger.error("Exception in intialize DataSource configurations ", ex);
            }
            System.err.println("Exception in init()-----> " + ex);
            //System.err.println("Exception in init()-----> " + ex, ex);
            // throw new JSFTemplateException(ex, ErrorCodes.GENERAL_EXCEPTION_DATASOURCE_MANAGER);
            throw new Exception(ex);
        }
    }

    public Connection getConnection() throws CommonException {
        try {
            OracleConnection conn = null;
            Connection con = null;
            if (ds == null && hikariDataSourse == null) {
                init();
            }
            if (ds != null) {
                conn = (OracleConnection) ds.getConnection();
                conn.setAutoCommit(false);
                return conn;
            } else {
                if (hikariDataSourse.getMaximumPoolSize() < hikariDataSourse.getHikariPoolMXBean().getActiveConnections() + 3) {
                    CommonLogger.businessLogger.debug("warning low free connection, used connectios: " + hikariDataSourse.getHikariPoolMXBean().getActiveConnections() + " Max connections: " + hikariDataSourse.getMaximumPoolSize());
                }
                con = hikariDataSourse.getConnection();
                con.setAutoCommit(false);
                return con;
            }
        } catch (SQLException ex) {
            System.err.println("Sql exception in getConnection()-----> " + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.GET_CONNECTION_ERROR + "---->  for [getConnection]", ex);
            //System.err.println("Sql exception in getConnection()-----> " + ex, ex);
            // throw new JSFTemplateException(ex, ErrorCodes.SQL_EXCEPTION_DATASOURCE_MANAGER);
            throw new CommonException(ex.getMessage(), ErrorCodes.GET_CONNECTION_ERROR);
        } catch (Exception ex) {
            System.err.println("Exception in getConnection()-----> " + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.GET_CONNECTION_ERROR + "---->  for [getConnection]", ex);

            //System.err.println("Exception in getConnection()-----> " + ex, ex);
            // throw new JSFTemplateException(ex, ErrorCodes.GENERAL_EXCEPTION_DATASOURCE_MANAGER);
            throw new CommonException(ex.getMessage(), ErrorCodes.GET_CONNECTION_ERROR);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Got Conneciton from connectionPool");
//        }
    }

    public void closeConnection(Connection con) throws CommonException {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            System.err.println("Sql exception in closeConnection()-----> " + ex);
            //System.err.println("Sql exception in closeConnection()-----> " + ex, ex);
            //   throw new JSFTemplateException(ex, ErrorCodes.SQL_EXCEPTION_DATASOURCE_MANAGER);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.CLOSE_CONNECTION_ERROR + "---->  for [closeConnection]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
        } catch (Exception ex) {
            System.err.println("Exception in closeConnection()-----> " + ex);
            //System.err.println("Exception in closeConnection()-----> " + ex, ex);
            // throw new JSFTemplateException(ex, ErrorCodes.GENERAL_EXCEPTION_DATASOURCE_MANAGER);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.CLOSE_CONNECTION_ERROR + "---->  for [closeConnection]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Closed Conneciton from connectionPool");
//        }
    }

    public void commitConnection(Connection con) throws CommonException {
        try {
            if (con != null) {
                con.commit();
            }
        } catch (SQLException ex) {
            System.err.println("Sql exception in commitConnection()-----> " + ex);
            //System.err.println("Sql exception in commitConnection()-----> " + ex, ex);
            //    throw new JSFTemplateException(ex, ErrorCodes.SQL_EXCEPTION_DATASOURCE_MANAGER);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.COMMITTING_ERROR + "---->  for [commitConnection]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.COMMITTING_ERROR);
        } catch (Exception ex) {
            System.err.println("Exception in commitConnection()-----> " + ex);
            // System.err.println("Exception in commitConnection()-----> " + ex, ex);
            //   throw new JSFTemplateException(ex, ErrorCodes.GENERAL_EXCEPTION_DATASOURCE_MANAGER);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.COMMITTING_ERROR + "---->  for [commitConnection]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.COMMITTING_ERROR);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Commited Conneciton from connectionPool");
//        }
    }

    public void closeDBResources(ResultSet rs, Statement statement) {
        String methodId = "DataSourceManger || closeDBResources ";
        try {
            if (rs != null) {
                rs.close();
            }
            if (statement != null) {
                statement.close();
            }

        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(methodId + "error while releasing statement" + ex.getMessage());
            CommonLogger.errorLogger.error(methodId + "error while releasing statement", ex);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Closed DB Resources Conneciton from connectionPool");
//        }

    }

    public void closeDBResources(ResultSet rs, Statement statement, CallableStatement callStat) {
        String methodId = "DataSourceManger || closeDBResources ";
        try {
            if (rs != null) {
                rs.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (callStat != null) {
                callStat.close();
            }

        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(methodId + "error while releasing statement" + ex.getMessage());
            CommonLogger.errorLogger.error(methodId + "error while releasing statement", ex);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Closed DB Resources Conneciton from connectionPool");
//        }

    }

    public void rollBack(Connection conn) throws CommonException {

        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Roll Backed Conneciton from connectionPool");
//        }
    }

    public void closeConnectionPool() throws CommonException {

        try 
        {
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Start Closing All Connections").build());
            
            if (hikariDataSourse != null)
                hikariDataSourse.close();
            else
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "hikariDataSourse hasent started yet... No connections were created").build());
            hikariDataSourse = null;

            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "All Connections are closed successfully").build());
        } 
        catch (Exception ex) 
        {
            CommonLogger.businessLogger.error("Exception When calling closeConnectionPool on Connection pool");
            CommonLogger.errorLogger.error("Exception When calling closeConnectionPool on Connection pool", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATASOURCE_CLOSE);
        }
//        finally
//        {
//            CommonLogger.businessLogger.info("Closed Connection Pool");
//        }
    }

//    public void printAllc3p0ConnectionPoolStats() 
//    {
//        StringBuilder logString = new StringBuilder();
//        if (c3p0ConnectionPool != null)
//        {
//            //logString.append("getAcquireIncrement: ").append(c3p0ConnectionPool.getAcquireIncrement());
//            //logString.append("getAcquireRetryAttempts: ").append(c3p0ConnectionPool.getAcquireRetryAttempts());
//            //logString.append("getAcquireRetryDelay: ").append(c3p0ConnectionPool.getAcquireRetryDelay());
//            //logString.append("getAutomaticTestTable: ").append(c3p0ConnectionPool.getAutomaticTestTable());
//            logString.append("getCheckoutTimeout: ").append(c3p0ConnectionPool.getCheckoutTimeout());
//            //logString.append("getConnectionCustomizerClassName: ").append(c3p0ConnectionPool.getConnectionCustomizerClassName());
//            //logString.append("getConnectionTesterClassName: ").append(c3p0ConnectionPool.getConnectionTesterClassName());
//            //logString.append("getContextClassLoaderSource: ").append(c3p0ConnectionPool.getContextClassLoaderSource());
//            logString.append(" || getDataSourceName: ").append(c3p0ConnectionPool.getDataSourceName() + "\n");
//            //logString.append("getDescription: ").append(c3p0ConnectionPool.getDescription());
//            //logString.append("getDriverClass: ").append(c3p0ConnectionPool.getDriverClass());
//            //logString.append("getFactoryClassLocation: ").append(c3p0ConnectionPool.getFactoryClassLocation());
//            //logString.append("getIdentityToken: ").append(c3p0ConnectionPool.getIdentityToken());
//            //logString.append("getIdleConnectionTestPeriod: ").append(c3p0ConnectionPool.getIdleConnectionTestPeriod());
//            logString.append("getInitialPoolSize: ").append(c3p0ConnectionPool.getInitialPoolSize());
//            //logString.append("*getJdbcUrl: ").append(c3p0ConnectionPool.getJdbcUrl());
//            //logString.append("getMaxAdministrativeTaskTime: ").append(c3p0ConnectionPool.getMaxAdministrativeTaskTime());
//            logString.append(" || getMaxConnectionAge: ").append(c3p0ConnectionPool.getMaxConnectionAge() + "\n");
//            logString.append("getMaxIdleTime: ").append(c3p0ConnectionPool.getMaxIdleTime());
//            logString.append(" || getMaxIdleTimeExcessConnections: ").append(c3p0ConnectionPool.getMaxIdleTimeExcessConnections() + "\n");
//            logString.append("getMaxPoolSize: ").append(c3p0ConnectionPool.getMaxPoolSize());
//            logString.append(" || getMaxStatements: ").append(c3p0ConnectionPool.getMaxStatements() + "\n");
//            logString.append("getMaxStatementsPerConnection: ").append(c3p0ConnectionPool.getMaxStatementsPerConnection());
//            logString.append(" || getMinPoolSize: ").append(c3p0ConnectionPool.getMinPoolSize() + "\n");
//            //logString.append("getPreferredTestQuery: ").append(c3p0ConnectionPool.getPreferredTestQuery());
//            //logString.append("getUnreturnedConnectionTimeout: ").append(c3p0ConnectionPool.getUnreturnedConnectionTimeout());
//            logString.append("getUser: ").append(c3p0ConnectionPool.getUser());
////            try {
//              //  logString.append("sampleStatementCacheStatus: ").append(c3p0ConnectionPool.sampleStatementCacheStatus(username, password));
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//                //logString.append("sampleThreadPoolStatus: ").append(c3p0ConnectionPool.sampleThreadPoolStatus());
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//                //logString.append("getLastCheckinFailure: ").append(c3p0ConnectionPool.getLastCheckinFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//                //logString.append("getLastCheckoutFailure: ").append(c3p0ConnectionPool.getLastCheckoutFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//                //logString.append("getLastConnectionTestFailure: ").append(c3p0ConnectionPool.getLastConnectionTestFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//               // logString.append("getLastIdleTestFailure: ").append(c3p0ConnectionPool.getLastIdleTestFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append(" || getLoginTimeout: ").append(c3p0ConnectionPool.getLoginTimeout() + "\n");
//            } catch (SQLException ex) {
//                logString.append(" || " + ex.toString()).append(" || ").append(ex.getMessage() + "\n");
//            }
////            try {
////                //logString.append("getNumBusyConnections: ").append(c3p0ConnectionPool.getNumBusyConnections(username, password));
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append("getNumConnections: ").append(c3p0ConnectionPool.getNumConnections(username, password));
//            } catch (SQLException ex) {
//                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
//            }
////            try {
////                //logString.append("getNumFailedCheckins: ").append(c3p0ConnectionPool.getNumFailedCheckins(username, password));
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append(" || getNumFailedCheckouts: ").append(c3p0ConnectionPool.getNumFailedCheckouts(username, password) + "\n");
//            } catch (SQLException ex) {
//                logString.append(" || " + ex.toString()).append(" || ").append(ex.getMessage() + "\n");
//            }
////            try {
////                //logString.append("getNumFailedIdleTests: ").append(c3p0ConnectionPool.getNumFailedIdleTests(username, password));
////            } catch (SQLException ex) {
////                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append("getNumIdleConnections: ").append(c3p0ConnectionPool.getNumIdleConnections(username, password));
//            } catch (SQLException ex) {
//                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
//            }
//            try {
//                logString.append(" || getNumThreadsAwaitingCheckout: ").append(c3p0ConnectionPool.getNumThreadsAwaitingCheckout(username, password) + "\n");
//            } catch (SQLException ex) {
//                logString.append(" || " + ex.toString()).append(" || ").append(ex.getMessage() + "\n");
//            }
//            try {
//                logString.append("getNumUnclosedOrphanedConnections: ").append(c3p0ConnectionPool.getNumUnclosedOrphanedConnections(username, password));
//            } catch (SQLException ex) {
//                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
//            }
//            try {
//                logString.append(" || getNumUserPools: ").append(c3p0ConnectionPool.getNumUserPools() + "\n");
//            } catch (SQLException ex) {
//                logString.append(" || " + ex.toString()).append(" || ").append(ex.getMessage() + "\n");
//            }
//            try {
//                logString.append("getThreadPoolNumActiveThreads: ").append(c3p0ConnectionPool.getThreadPoolNumActiveThreads());
//            } catch (SQLException ex) {
//                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
//            }
//            try {
//                logString.append(" || getThreadPoolNumIdleThreads: ").append(c3p0ConnectionPool.getThreadPoolNumIdleThreads() + "\n");
//            } catch (SQLException ex) {
//                logString.append(" || " + ex.toString()).append(" || ").append(ex.getMessage() + "\n");
//            }
//            try {
//                logString.append("getThreadPoolNumTasksPending: ").append(c3p0ConnectionPool.getThreadPoolNumTasksPending());
//            } catch (SQLException ex) {
//                logString.append(ex.toString()).append(" || ").append(ex.getMessage());
//            }
//            try {
//                logString.append(" || getThreadPoolSize: ").append(c3p0ConnectionPool.getThreadPoolSize() + "\n");
//            } catch (SQLException ex) {
//                logString.append(" || " + ex.toString()).append(" || ").append(ex.getMessage() + "\n");
//            }
//            //logString.append("getUnreturnedConnectionTimeout: ").append(c3p0ConnectionPool.getUnreturnedConnectionTimeout());
//        }
//        else
//            logString.append("c3p0ConnectionPool hasent started yet... No connections were created");
//        CommonLogger.businessLogger.info(logString.toString());
//    }

}
