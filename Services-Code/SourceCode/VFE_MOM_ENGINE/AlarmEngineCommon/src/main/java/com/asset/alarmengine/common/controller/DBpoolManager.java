/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.alarmengine.common.controller;

import com.asset.alarmengine.common.defines.EngineDefines;
import com.asset.alarmengine.common.defines.EngineErrorCodes;
import com.asset.alarmengine.common.exception.EngineException;
import com.asset.alarmengine.common.logger.EngineLogger;
import com.asset.alarmengine.common.utilities.EngineUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

/**
 *
 * @author Mostafa Kashif
 */
public class DBpoolManager {
    
	public static String PASSWORD = null;
	public static String USER = null;
	public static String DB_URL = null;
	private static DBpoolManager dbConnManager = null;
	private static DataSource dataSource = null;
	
	private DBpoolManager() {
		
			USER = EngineUtils.getStringValueFromMap(EngineDefines.DB_USERNAME, EngineDefines.propertiesMap);
			PASSWORD =EngineUtils.getStringValueFromMap(EngineDefines.DB_PASSWORD, EngineDefines.propertiesMap);
			DB_URL = EngineUtils.getStringValueFromMap(EngineDefines.DB_URL, EngineDefines.propertiesMap);			
		
	}
	public static DBpoolManager getInstance() {
		if (dbConnManager == null) {
			dbConnManager = new DBpoolManager();
		}
		return dbConnManager;
	}
	
	 public Connection getConnection() throws EngineException {
	        Connection con = null;
	        try {
	            if (dataSource == null) {
	                dataSource = setupDataSource();
	            }
	            
	            
	            con = dataSource.getConnection();
	            con.setAutoCommit(false);
	        } catch (Exception e) {
	        	          EngineLogger.debugLogger.error(
						"Getting Connection Caught Exception---->  for user:"+USER+", URL:"+DB_URL+ e);
				EngineLogger.errorLogger.error(
						"Getting Connection Caught Exception----> for user:"+USER+", URL:"+DB_URL+ e);
	        	throw new EngineException(e,EngineErrorCodes.ERROR_IN_DATABASE_POOL);
	        }
	        return con;
	    }

//	    private static DataSource setupDataSource() {
//	        ComboPooledDataSource cpds = new ComboPooledDataSource();
//	        cpds.setJdbcUrl( DB_URL );
//	        cpds.setUser(USER);                                  
//	        cpds.setPassword(PASSWORD);
//	   
//	        return cpds;
//
//	    }
	    private static DataSource setupDataSource() {
                HikariConfig hikariConfig = new HikariConfig();
                hikariConfig.setJdbcUrl(DB_URL);
                hikariConfig.setUsername(USER);
                hikariConfig.setPassword(PASSWORD);
                HikariDataSource hikariDataSourse = new HikariDataSource(hikariConfig);
                return hikariDataSourse;
	    }
	    
		public static void closeConnection(Connection connection) throws EngineException {
			if (connection != null) {
				try {
					connection.close();
				connection = null;
				} catch (SQLException e) {
					EngineLogger.debugLogger.error(
							"Closing Connection Caught SQLException---->"+ e);
					EngineLogger.errorLogger.error(
							"Closing Connection Caught SQLException---->"+e);
					
					throw new EngineException(e,EngineErrorCodes.ERROR_IN_DATABASE_POOL);
				}
			}

		}
		public static void close(ResultSet resultSet) throws EngineException {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					EngineLogger.debugLogger.error(
							"Closing resultset Caught SQLException---->"+ e);
					EngineLogger.errorLogger.error(
							"Closing resultset Caught SQLException---->"+e);
					throw new EngineException(EngineErrorCodes.ERROR_IN_DATABASE_POOL);
				}
			}
		}
		public static void close(Statement statement) throws EngineException {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					EngineLogger.debugLogger.error(
							"Closing statement Caught SQLException---->"+ e);
					EngineLogger.errorLogger.error(
							"Closing statement Caught SQLException---->"+e);
					throw new EngineException(EngineErrorCodes.ERROR_IN_DATABASE_POOL);
				}
			}
		}
		
		public void rollback(Connection connection) throws EngineException {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e) {
					EngineLogger.debugLogger.error(
							"rollback Connection Caught SQLException---->"+ e);
					EngineLogger.errorLogger.error(
							"rollback Connection Caught SQLException---->"+e);
					throw new EngineException(EngineErrorCodes.ERROR_IN_DATABASE_POOL);
				}
			}

		}
		
		public void commit(Connection connection) throws EngineException {
			if (connection != null) {
				try {
					connection.commit();
				} catch (SQLException e) {
					EngineLogger.debugLogger.error(
							"Commit Caught SQLException---->"+ e);
					EngineLogger.errorLogger.error(
							"Commit Caught SQLException---->"+e);
					throw new EngineException(EngineErrorCodes.ERROR_IN_DATABASE_POOL);
				}
			}
		}

}
