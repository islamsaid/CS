/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.sendingsms.controller;

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
            if (Defines.C3P0_DRIVER_CLASS != null) {
                //dbUrl = Defines.DB_URL;
                //username = Defines.DB_USERNAME;
                //password = Defines.DB_PASSWORD;
                driverClass = Defines.C3P0_DRIVER_CLASS;
                //poolMaxSize = Integer.parseInt(Defines.DB_CONNECTION_POOL_MAX_SIZE);
//                c3p0ConnectionPool = new ComboPooledDataSource(com.asset.cs.sendingsms.defines.Defines.C3P0_QUEUE_SETTING_NAME);
//                c3p0ConnectionPool.setDriverClass(driverClass);
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
                hikariConfig.setDriverClassName(driverClass);
                hikariConfig.setJdbcUrl(dbUrl);
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setConnectionTimeout(Defines.QUEUE_C3P0_CHECKOUT_TIMEOUT_VALUE);
                hikariConfig.setMaxLifetime(Defines.ENQUEUE_C3P0_MAX_CONNECTION_AGE_VALUE);
                hikariConfig.setIdleTimeout(Defines.ENQUEUE_C3P0_MAX_IDLE_TIME_VALUE);
                hikariConfig.setMaximumPoolSize(Defines.ENQUEUE_C3P0_MAX_POOL_SIZE_VALUE);
                hikariConfig.setMinimumIdle(Defines.ENQUEUE_C3P0_MIN_POOL_SIZE_VALUE);
                hikariConfig.setPoolName("Hikari-SendingSMSEngine-" + Thread.currentThread().getName());
                hikariDataSourse = new HikariDataSource(hikariConfig);

            } else {
                ds = null;
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY, Defines.WEBLOGIC_CONTEXT_FACTORY);
                env.put(Context.PROVIDER_URL, Defines.WEBLOGIC_PROVIDER_URL);
                Context context = new InitialContext(env);
                ds = (javax.sql.DataSource) context.lookup(Defines.DATA_SOURCE_NAME);
            }
            if (CommonLogger.businessLogger != null) {
                CommonLogger.businessLogger.debug("HikariCP configurations setted successfully");
            }
            System.out.println("DataSource created successfully");
        } catch (NamingException ex) {
            if (CommonLogger.errorLogger != null) {
                CommonLogger.errorLogger.error("Exception in intialize DataSource configurations ", ex);
            }
            System.err.println("Sql exception in init()-----> " + ex);
            // System.err.println("Sql exception in init()-----> " + ex,ex);
            // throw new JSFTemplateException(ex, ErrorCodes.NAMING_EXCEPTION_DATASOURCE_MANAGER);
            throw new Exception(ex);
        } catch (Exception ex) {
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
        long beginTime = System.currentTimeMillis();
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
//                    CommonLogger.businessLogger.debug("warning low free connection, used connectios: " + hikariDataSourse.getHikariPoolMXBean().getActiveConnections() + " Max connections: " + hikariDataSourse.getMaximumPoolSize());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Warning Low Free Connection")
                            .put(GeneralConstants.StructuredLogKeys.CONNECTION_POOL_STATS, hikariDataSourse.getHikariPoolMXBean().getActiveConnections())
                            .put(GeneralConstants.StructuredLogKeys.MAX_CONNECTIONS, hikariDataSourse.getMaximumPoolSize()).build());

                }
                con = hikariDataSourse.getConnection();
                con.setAutoCommit(false); // TODO
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
        } finally {
            long endTime = System.currentTimeMillis();
//            CommonLogger.businessLogger.debug("get connection in " + (endTime - beginTime) + " msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Getting Connection")
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_TIME, (endTime - beginTime)).build());
        }
    }

    public void closeConnection(Connection con) throws CommonException {
        long beginTime = System.currentTimeMillis();
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
        } finally {
            long endTime = System.currentTimeMillis();
//            CommonLogger.businessLogger.debug("closed connection in " + (endTime - beginTime) + " msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Closed Connection")
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_TIME, (endTime - beginTime)).build());
        }
    }

    public void commitConnection(Connection con) throws CommonException {
        long beginTime = System.currentTimeMillis();
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
        } finally {
            long endTime = System.currentTimeMillis();
//            CommonLogger.businessLogger.debug("commit connection in " + (endTime - beginTime) + " msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Commit Connection")
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_TIME, (endTime - beginTime)).build());
        }
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

    }

    public void rollBack(Connection conn) throws CommonException {
        long beginTime = System.currentTimeMillis();
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
        } finally {
            long endTime = System.currentTimeMillis();
//            CommonLogger.businessLogger.debug("closed connection in " + (endTime - beginTime) + " msec");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Closed Connection")
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_TIME, (endTime - beginTime)).build());
        }
    }

    public void closeConnectionPool() throws CommonException {

        try {
//            CommonLogger.businessLogger.info("Start Closing All Connections");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Start Closing All Connections").build());
            hikariDataSourse.close();
            hikariDataSourse = null;

//            CommonLogger.businessLogger.info("All Connections are closed successfully");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "All Connections Are Closed Successfully").build());

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Exception in closeConnectionPool ", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATASOURCE_CLOSE);

        }
    }

//    public ComboPooledDataSource getC3p0ConnectionPool() {
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
}
