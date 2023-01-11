/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.controller;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import oracle.jdbc.OracleConnection;

/**
 *
 * @author mostafa.kashif
 */
public class DataSourceManger {

    private static javax.sql.DataSource ds = null;

    private static String dbUrl = null;
    private static String username = null;
    private static String password = null;
    private static int poolMaxSize = 0;
    private static String driverClass = null;
//    private static ComboPooledDataSource c3p0ConnectionPool = null;
    private static HikariDataSource hikariDataSourse = null;

    public static void init() throws Exception {
        try {
            HikariConfig hikariConfig = null;
            if (CommonLogger.businessLogger != null) {
                CommonLogger.businessLogger.debug("Setting up HikariCP configurations");
            }
            if (Defines.C3P0_DRIVER_CLASS != null) {
                dbUrl = Defines.C3P0_JDBC_URL;
                username = Defines.C3P0_USER;
                password = Defines.C3P0_PASSWORD;
                driverClass = Defines.C3P0_DRIVER_CLASS;
                //poolMaxSize = Integer.parseInt(Defines.DB_CONNECTION_POOL_MAX_SIZE);
//                c3p0ConnectionPool = new ComboPooledDataSource();
                hikariConfig = new HikariConfig();
                hikariConfig.setDriverClassName(driverClass);
                hikariConfig.setJdbcUrl(dbUrl);
                hikariConfig.setUsername(username);
                hikariConfig.setPassword(password);
                hikariConfig.setConnectionTimeout(Defines.C3P0_CHECKOUT_TIMEOUT);
                hikariConfig.setMaxLifetime(Defines.C3P0_MAX_CONNECTION_AGE);
                hikariConfig.setIdleTimeout(Defines.C3P0_MAX_IDLE_TIME);
                hikariConfig.setMaximumPoolSize(Defines.C3P0_MAX_POOL_SIZE);
                hikariConfig.setMinimumIdle(Defines.C3P0_MIN_POOL_SIZE);
                hikariConfig.setPoolName("Hikari-Common-" + Thread.currentThread().getName());
                hikariDataSourse = new HikariDataSource(hikariConfig);
//                getC3p0ConnectionPool().setDriverClass(driverClass);
//                getC3p0ConnectionPool().setJdbcUrl(dbUrl);
//                getC3p0ConnectionPool().setUser(username);
//                getC3p0ConnectionPool().setPassword(password);
//                getC3p0ConnectionPool().setAcquireIncrement(Defines.C3P0_ACQUIRE_INCREMENT); // has no match in hikari
//                getC3p0ConnectionPool().setCheckoutTimeout(Defines.C3P0_CHECKOUT_TIMEOUT);
//                getC3p0ConnectionPool().setInitialPoolSize(Defines.C3P0_INITIAL_POOL_SIZE); // has no match in hikari
//                getC3p0ConnectionPool().setMaxConnectionAge(Defines.C3P0_MAX_CONNECTION_AGE);
//                getC3p0ConnectionPool().setMaxIdleTime(Defines.C3P0_MAX_IDLE_TIME);
//                getC3p0ConnectionPool().setMaxPoolSize(Defines.C3P0_MAX_POOL_SIZE);
//                getC3p0ConnectionPool().setMaxStatements(Defines.C3P0_MAX_STATEMENTS); has no match in hikari
//                getC3p0ConnectionPool().setMinPoolSize(Defines.C3P0_MIN_POOL_SIZE);
                //getC3p0ConnectionPool().setUnreturnedConnectionTimeout(poolMaxSize);

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

    public static Connection getConnection(int numTrials) throws CommonException {

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
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, hikariDataSourse.getHikariPoolMXBean().getActiveConnections())
                            .put(GeneralConstants.StructuredLogKeys.MAX_CONNECTIONS, hikariDataSourse.getMaximumPoolSize()).build());
                }
                con = hikariDataSourse.getConnection();
                con.setAutoCommit(false);
                return con;
            }

        } catch (SQLException ex) {

            System.err.println("Sql exception in getConnection()-----> TrailNum=[" + numTrials + "]  -->" + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.GET_CONNECTION_ERROR + "---->  for [getConnection] TrailNum=[" + numTrials + "]-->", ex);
            if (numTrials > 0) {
                return getConnection(--numTrials);
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.GET_CONNECTION_ERROR);
            }

        } catch (Exception ex) {
            System.err.println("Exception in getConnection()-----> " + ex);
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.GET_CONNECTION_ERROR + "---->  for [getConnection]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.GET_CONNECTION_ERROR);
        } finally {
            //CommonLogger.businessLogger.info("Got Conneciton from connectionPool");
        }
    }

    public static Connection getConnection() throws CommonException {
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
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, hikariDataSourse.getHikariPoolMXBean().getActiveConnections())
                            .put(GeneralConstants.StructuredLogKeys.MAX_CONNECTIONS, hikariDataSourse.getMaximumPoolSize()).build());
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
        } finally {
            //CommonLogger.businessLogger.info("Got Conneciton from connectionPool");
        }
    }

    public static void closeConnection(Connection con) throws CommonException {
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
            //CommonLogger.businessLogger.info("Closed Conneciton from connectionPool");
        }
    }

    public static void commitConnection(Connection con) throws CommonException {
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
            //CommonLogger.businessLogger.info("Commited Conneciton from connectionPool");
        }
    }

    public static void closeDBResources(ResultSet rs, Statement statement) {
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
        } finally {
            //CommonLogger.businessLogger.info("Closed DB Resources Conneciton from connectionPool");
        }

    }

    public static void closeDBResources(ResultSet rs, Statement statement, CallableStatement callStat) {
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
        } finally {
            //CommonLogger.businessLogger.info("Closed Db Resources Conneciton from connectionPool");
        }

    }

    public static void rollBack(Connection conn) throws CommonException {

        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                    + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack]", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
        } finally {
            //CommonLogger.businessLogger.info("Roll Backed Conneciton from connectionPool");
        }
    }

    public static void closeConnectionPool() throws CommonException {

        try {
            //CommonLogger.businessLogger.info("Start Closing All Connections");

            if (hikariDataSourse != null) {
                hikariDataSourse.close();
            } else {
                CommonLogger.businessLogger.info("hikariDataSourse hasent started yet... No connections were created");
            }
            hikariDataSourse = null;

            //CommonLogger.businessLogger.info("All Connections are closed successfully");
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Exception in closeConnectionPool ", ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.DATASOURCE_CLOSE);

        } finally {
            //CommonLogger.businessLogger.info("Closed Connection Pool");
        }
    }

//    public static ComboPooledDataSource getC3p0ConnectionPool() {
//        return c3p0ConnectionPool;
//    }
    public static HikariDataSource getHikariDataSourse() {
        return hikariDataSourse;
    }

}
