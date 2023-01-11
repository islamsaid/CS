/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.utils;

import com.asset.contactstrategy.common.dao.SMSGroupDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.defines.SystemLookups;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.exception.InterfacesBusinessException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DequeuerResponseModel;
import com.asset.contactstrategy.common.models.EnqueueModelREST;
import com.asset.contactstrategy.common.models.RESTResponseModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.SMS;
import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.models.SMSBridgeJSONStructure;
import com.asset.contactstrategy.common.models.SMSHistoryModel;
import com.asset.contactstrategy.common.models.TLVOptionalModel;
import com.asset.contactstrategy.interfaces.models.CustomersModel;
import com.asset.contactstrategy.common.models.EnqueueModelDeliverSM;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.InterfacesLogModel;
import com.asset.contactstrategy.interfaces.models.RESTLogModel;
import com.asset.contactstrategy.interfaces.models.ServiceWhitelistModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import com.asset.contactstrategy.interfaces.models.SmsBusinessModel;
import com.asset.contactstrategy.interfaces.models.TemplateModel;
import com.fasterxml.jackson.core.JsonProcessingException;
//import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import javax.crypto.Mac;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Zain Al-Abedin
 */
public class Utility {

    public static ArrayList mapToArrayList(HashMap map, ArrayList list) {
        ArrayList toReturn = list;
        try {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                toReturn.add(entry.getValue());
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error converting map to ArrayList " + map.toString(), e);
        }
        return toReturn;
    }

    /**
     * This method used to return the next number of predefined database
     * sequence.
     *
     * @param sequence
     * @param conn
     * @return
     * @throws CommonException
     */
    public static int getNextId(String sequence, Connection conn) throws CommonException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT " + sequence + ".NEXTVAL FROM DUAL";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            return (rs.next()) ? rs.getInt(1) : -1;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error(SMSGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [getNextId]" + ex);
            throw new CommonException(ex.getMessage(), ex.getErrorCode());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                CommonLogger.errorLogger.error(SMSGroupDAO.class.getName() + " || " + "Getting Caught Exception---->  for [addNewGroup]" + ex);
                throw new CommonException(ex.getMessage(), ex.getErrorCode());
            }
        }
    }

    public static void sendMOMAlarem(MOMErrorsModel mOMErrorsModel) {
        try {
//            CommonLogger.businessLogger.debug(Utility.class.getName() + " || " + "Starting [sendMOMAlarem]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "sendMOMAlarem Started").build());

            MainService mainService = new MainService();
            mainService.insertMOMError(mOMErrorsModel);
//            CommonLogger.businessLogger.debug(Utility.class.getName() + " || " + "End [sendMOMAlarem]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "sendMOMAlarem Ended").build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [sendMOMAlarem]" + ex);

        }
    }

    //Esmail.Anbar | Cloud CR | Reload is now automatic from REST Services
    public static void CountersReloader() {
//        try {
//            CommonLogger.businessLogger.debug(Utility.class.getName() + " || " + "Starting [CountersReloader]...");
//            MainService mainService = new MainService();
//            mainService.CountersReloader();
//            CommonLogger.businessLogger.debug(Utility.class.getName() + " || " + "End [CountersReloader]...");
//
//        } catch (CommonException ex) {
//            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [CountersReloader]" + ex);
//
//        }
    }

    public static HashMap<Integer, LookupModel> loadLookupsMap(String tableName, String idColName, String lableColName) throws CommonException {
        CommonLogger.businessLogger.debug("Starting");
        HashMap<Integer, LookupModel> lookups = null;
        try {
            MainService mainService = new MainService();
            ArrayList<LookupModel> lookupList;
            lookupList = mainService.loadIdLableLookups(tableName, idColName, lableColName);
            lookups = lookupListToMap(lookupList);
        } catch (CommonException e) {
            throw new CommonException("Failed to retrieve lookups for table " + tableName, ErrorCodes.UNKOWN_ERROR);
        }
        return lookups;
    }

    public static HashMap<Integer, LookupModel> lookupListToMap(ArrayList<LookupModel> list) {
        HashMap<Integer, LookupModel> map = new HashMap<>();
        if (list != null) {
            for (LookupModel lk : list) {
                map.put(lk.getId(), lk);
            }
        }
        return map;
    }

//    public static int getMsisdnModX(String MSISDN) {
//        return Integer.parseInt(MSISDN.substring(2)) % Integer.parseInt(SystemLookups.SYSTEM_PROPERTIES.get(Defines.SYSTEM_PROPERTIES_MOD_X_KEY));
//    }
    //Changed by kashif
    public static int getMsisdnLastTwoDigits(String MSISDN) {
        return Integer.parseInt(MSISDN.substring(MSISDN.length() - 2, MSISDN.length()));
    }

    public static int calcConcMsgCount(int textMsgLen, int lang) {
        //HAZEMTODO :check limit size
        if (lang == Defines.LANGUAGE_ENGLISH) {
            return textMsgLen <= 160 ? 1 : new Double(Math.ceil(textMsgLen / 153.0)).intValue();
        } else {
            return textMsgLen <= 70 ? 1 : new Double(Math.ceil(textMsgLen / 67.0)).intValue();
        }
    }

    public static String addAdsScriptToMsgText(String textMsg, String adsScript) {
        return textMsg + ". " + adsScript;
    }

    public static String generateTransId(String prefix) {
        CommonLogger.businessLogger.info("generateTransId Started...");
        Random randomGen = new Random();
        StringBuilder transId = new StringBuilder();
        long currentSysTime = System.currentTimeMillis();
        int randomInt = randomGen.nextInt(1000);
        long servletThreadId = Thread.currentThread().getId();
//        long instanceId = Long.parseLong(Initializer.propertiesFileBundle.getString("INSTANCE_ID"));

        transId.append(prefix)
                //                .append(instanceId)
                .append(servletThreadId)
                .append(randomInt)
                .append(currentSysTime);

        CommonLogger.businessLogger.info("Generated TransId = " + transId);
        CommonLogger.businessLogger.info("generateTransId Ended...");
        return transId.toString();
    }

    /**
     * generateExcelFile generates a StreamedContent of type excel.
     *
     * @param values : is a map with keys representing the columns in excel and
     * fields in objects. Their values are the list of values of that
     * column/field.
     * @return : StreamedContent of type excel.
     *
     */
    public static InputStream generateExcelFile(Map<String, ArrayList<String>> values) throws CommonException {
        InputStream in = null;
        if (values != null) {
            try {
                HSSFWorkbook book = new HSSFWorkbook();
                HSSFSheet sheet = book.createSheet();

                int rowIndex = Defines.EXCEL_GENERATION.RowStart;
                int columnIndex = Defines.EXCEL_GENERATION.ColumnStart;
                String key = null;//is used to get expected number of rows
                //Get the keys
                Set<String> columns = values.keySet();

                //Set Style For Column Names
                HSSFCellStyle headerCellStyle = sheet.getWorkbook().createCellStyle();
                Font font = sheet.getWorkbook().createFont();
                font.setBoldweight(Font.BOLDWEIGHT_BOLD);
                font.setFontName(Defines.EXCEL_GENERATION.DEFAULT_FONT_NAME);
                font.setColor(Defines.EXCEL_GENERATION.RED_COLOR);
                headerCellStyle.setFont(font);
                headerCellStyle.setWrapText(true);
                headerCellStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
                //Set Column Names in the first row
                Row row = sheet.createRow(rowIndex);
                for (String columnName : columns) {
                    Cell cell = row.createCell(columnIndex);
                    cell.setCellStyle(headerCellStyle);
                    cell.setCellValue(columnName);
                    columnIndex++;
                    key = columnName;
                }

                //Prepare the place to insert values
                rowIndex++;
                columnIndex = Defines.EXCEL_GENERATION.ColumnStart;
                //Set Style For Column Values
                HSSFCellStyle valueCellStyle = sheet.getWorkbook().createCellStyle();
                Font valueFont = sheet.getWorkbook().createFont();
                valueFont.setBoldweight(Font.BOLDWEIGHT_NORMAL);
                valueFont.setFontName(Defines.EXCEL_GENERATION.DEFAULT_FONT_NAME);
                valueFont.setColor(Defines.EXCEL_GENERATION.BLACK_COLOR);
                valueCellStyle.setFont(valueFont);
                valueCellStyle.setWrapText(true);
                valueCellStyle.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
                //Determine number of rows by getting size of the value list of first key
                int expectedNumberOfRows = values.get(key).size();
                //Insert Values in each column
                int valuesIndex = 0; //used to loop inside the values list.
                //1)for each row
                for (int r = 1; r < (expectedNumberOfRows + 1); r++) {
                    Row valueRow = sheet.createRow(rowIndex);
                    //2)pass through each cell (columnIndex) in that row (valueRow) and set its value (columnValue).
                    for (String columnName : columns) {
                        String columnValue = values.get(columnName).get(valuesIndex);
                        Cell cell = valueRow.createCell(columnIndex);
                        cell.setCellStyle(valueCellStyle);
                        if ((columnValue == null) || (columnValue.equals(Defines.EXCEL_GENERATION.NULL_VAL))) {
                            columnValue = "";
                        }
                        cell.setCellValue(columnValue);
                        columnIndex++;
                    }
                    columnIndex = Defines.EXCEL_GENERATION.ColumnStart;
                    valuesIndex++;
                    rowIndex++;
                }

                //Set sizes of columns
                for (int c = 0; c <= columns.size(); c++) {
                    sheet.autoSizeColumn(c);
                }

                //Write the excel file and put in memory
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                book.write(os);

                //Read the excel file from memory
                in = new ByteArrayInputStream(os.toByteArray());
                os.close();

                return in;
            } catch (Exception e) {
                in = null;
                throw new CommonException("Exception while generating CSV file", ErrorCodes.UNKOWN_ERROR);
            }

        }
        return in;
    }

    public static Connection getDBConnection(String dbURL, String schemaName, String schemaPswd) throws CommonException {
        try {
            Connection connection = DriverManager.getConnection(dbURL, schemaName, schemaPswd);
            connection.setAutoCommit(false);
            return connection;
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.GET_CONNECTION_ERROR + "---- > [" + Utility.class.getName() + ".getDBConnection]", ex);
            throw new CommonException("Problem in opening Connection", ErrorCodes.GET_CONNECTION_ERROR);
        }
    }

    public static void closeDBConnection(Connection connection) throws CommonException {
        try {
            if ((connection != null) && (!connection.isClosed())) {
                connection.close();
            }
        } catch (SQLException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                    + ErrorCodes.CLOSE_CONNECTION_ERROR + "---- > [" + Utility.class.getName() + ".getDBConnection]", ex);
            throw new CommonException("Problem in closing Connection", ErrorCodes.GET_CONNECTION_ERROR);
        }
    }

    public static String getHikariConnectionPoolStats(HikariDataSource hikariDataSource) {
        StringBuilder logString = new StringBuilder();
        if (hikariDataSource != null) {
            logString.append("HikariCP_Status:: ").append("ConnectionTimeout: ").append(hikariDataSource.getConnectionTimeout());
            logString.append(" || ").append("PoolName: ").append(hikariDataSource.getPoolName());
            logString.append(" || ").append("MaxLifetime: ").append(hikariDataSource.getMaxLifetime());
            logString.append(" || ").append("IdleTimeout: ").append(hikariDataSource.getIdleTimeout());
            logString.append(" || ").append("ActiveConnections: ").append(hikariDataSource.getHikariPoolMXBean().getActiveConnections());
            logString.append(" || ").append("IdleConnections: ").append(hikariDataSource.getHikariPoolMXBean().getIdleConnections());
            logString.append(" || ").append("TotalConnections: ").append(hikariDataSource.getHikariPoolMXBean().getTotalConnections());
            logString.append(" || ").append("MaximumPoolSize: ").append(hikariDataSource.getMaximumPoolSize());
            logString.append(" || ").append("MinimumIdle: ").append(hikariDataSource.getMinimumIdle());
            logString.append(" || ").append("Username: ").append(hikariDataSource.getUsername());
            logString.append(" || ").append("ThreadsAwaitingConnection: ").append(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());

            try {
                logString.append(" || ").append("getLoginTimeout: ").append(hikariDataSource.getLoginTimeout());//.append("\n");
            } catch (SQLException ex) {
                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
            }

        } else {
            logString.append("HikariCP_Status:: ").append("HikariDataSource hasent started yet... No connections were created");
        }
        return logString.toString();
    }

//    public static String getC3P0ConnectionPoolStats(ComboPooledDataSource c3p0ConnectionPool) {
//        StringBuilder logString = new StringBuilder();
//        if (c3p0ConnectionPool != null) {
//            String username = c3p0ConnectionPool.getUser();
//            String password = c3p0ConnectionPool.getPassword();
//            //logString.append("c3p0_Status:: ").append("getAcquireIncrement: ").append(c3p0ConnectionPool.getAcquireIncrement());
//            //logString.append(" || ").append("getAcquireRetryAttempts: ").append(c3p0ConnectionPool.getAcquireRetryAttempts());
//            //logString.append(" || ").append("getAcquireRetryDelay: ").append(c3p0ConnectionPool.getAcquireRetryDelay());
//            //logString.append(" || ").append("getAutomaticTestTable: ").append(c3p0ConnectionPool.getAutomaticTestTable());
//            logString.append("c3p0_Status:: ").append("getCheckoutTimeout: ").append(c3p0ConnectionPool.getCheckoutTimeout());//.append("\n");
//            //logString.append(" || ").append("getConnectionCustomizerClassName: ").append(c3p0ConnectionPool.getConnectionCustomizerClassName());
//            //logString.append(" || ").append("getConnectionTesterClassName: ").append(c3p0ConnectionPool.getConnectionTesterClassName());
//            //logString.append(" || ").append("getContextClassLoaderSource: ").append(c3p0ConnectionPool.getContextClassLoaderSource());
//            logString.append(" || ").append("getDataSourceName: ").append(c3p0ConnectionPool.getDataSourceName());//.append("\n");
//            //logString.append(" || ").append("getDescription: ").append(c3p0ConnectionPool.getDescription());
//            //logString.append(" || ").append("getDriverClass: ").append(c3p0ConnectionPool.getDriverClass());
//            //logString.append(" || ").append("getFactoryClassLocation: ").append(c3p0ConnectionPool.getFactoryClassLocation());
//            //logString.append(" || ").append("getIdentityToken: ").append(c3p0ConnectionPool.getIdentityToken());
//            //logString.append(" || ").append("getIdleConnectionTestPeriod: ").append(c3p0ConnectionPool.getIdleConnectionTestPeriod());
//            logString.append(" || ").append("getInitialPoolSize: ").append(c3p0ConnectionPool.getInitialPoolSize());//.append("\n");
//            //logString.append(" || ").append("*getJdbcUrl: ").append(c3p0ConnectionPool.getJdbcUrl());
//            //logString.append(" || ").append("getMaxAdministrativeTaskTime: ").append(c3p0ConnectionPool.getMaxAdministrativeTaskTime());
//            logString.append(" || ").append("getMaxConnectionAge: ").append(c3p0ConnectionPool.getMaxConnectionAge());//.append("\n");
//            logString.append(" || ").append("getMaxIdleTime: ").append(c3p0ConnectionPool.getMaxIdleTime());//.append("\n");
//            logString.append(" || ").append("getMaxIdleTimeExcessConnections: ").append(c3p0ConnectionPool.getMaxIdleTimeExcessConnections());//.append("\n");
//            logString.append(" || ").append("getMaxPoolSize: ").append(c3p0ConnectionPool.getMaxPoolSize());//.append("\n");
//            logString.append(" || ").append("getMaxStatements: ").append(c3p0ConnectionPool.getMaxStatements());//.append("\n");
//            logString.append(" || ").append("getMaxStatementsPerConnection: ").append(c3p0ConnectionPool.getMaxStatementsPerConnection());//.append("\n");
//            logString.append(" || ").append("getMinPoolSize: ").append(c3p0ConnectionPool.getMinPoolSize());//.append("\n");
//            //logString.append(" || ").append("getPreferredTestQuery: ").append(c3p0ConnectionPool.getPreferredTestQuery());
//            //logString.append(" || ").append("getUnreturnedConnectionTimeout: ").append(c3p0ConnectionPool.getUnreturnedConnectionTimeout());
//            logString.append(" || ").append("getUser: ").append(c3p0ConnectionPool.getUser());//.append("\n");
////            try {
//            //  logString.append(" || ").append("sampleStatementCacheStatus: ").append(c3p0ConnectionPool.sampleStatementCacheStatus(username, password));
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//            //logString.append(" || ").append("sampleThreadPoolStatus: ").append(c3p0ConnectionPool.sampleThreadPoolStatus());
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//            //logString.append(" || ").append("getLastCheckinFailure: ").append(c3p0ConnectionPool.getLastCheckinFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//            //logString.append(" || ").append("getLastCheckoutFailure: ").append(c3p0ConnectionPool.getLastCheckoutFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//            //logString.append(" || ").append("getLastConnectionTestFailure: ").append(c3p0ConnectionPool.getLastConnectionTestFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
////            try {
//            // logString.append(" || ").append("getLastIdleTestFailure: ").append(c3p0ConnectionPool.getLastIdleTestFailure(username, password).getMessage());
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append(" || ").append("getLoginTimeout: ").append(c3p0ConnectionPool.getLoginTimeout());//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getNumBusyConnections: ").append(c3p0ConnectionPool.getNumBusyConnections(username, password));//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getNumConnections: ").append(c3p0ConnectionPool.getNumConnections(username, password));//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
////            try {
////                //logString.append(" || ").append("getNumFailedCheckins: ").append(c3p0ConnectionPool.getNumFailedCheckins(username, password));
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append(" || ").append("getNumFailedCheckouts: ").append(c3p0ConnectionPool.getNumFailedCheckouts(username, password));//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
////            try {
////                //logString.append(" || ").append("getNumFailedIdleTests: ").append(c3p0ConnectionPool.getNumFailedIdleTests(username, password));
////            } catch (SQLException ex) {
////                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());
////            }
//            try {
//                logString.append(" || ").append("getNumIdleConnections: ").append(c3p0ConnectionPool.getNumIdleConnections(username, password));//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getNumThreadsAwaitingCheckout: ").append(c3p0ConnectionPool.getNumThreadsAwaitingCheckout(username, password));//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getNumUnclosedOrphanedConnections: ").append(c3p0ConnectionPool.getNumUnclosedOrphanedConnections(username, password));//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getNumUserPools: ").append(c3p0ConnectionPool.getNumUserPools());//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getThreadPoolNumActiveThreads: ").append(c3p0ConnectionPool.getThreadPoolNumActiveThreads());//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getThreadPoolNumIdleThreads: ").append(c3p0ConnectionPool.getThreadPoolNumIdleThreads());//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getThreadPoolNumTasksPending: ").append(c3p0ConnectionPool.getThreadPoolNumTasksPending());//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            try {
//                logString.append(" || ").append("getThreadPoolSize: ").append(c3p0ConnectionPool.getThreadPoolSize());//.append("\n");
//            } catch (SQLException ex) {
//                logString.append(" || ").append(ex.toString()).append(" || ").append(ex.getMessage());//.append("\n");
//            }
//            //logString.append(" || ").append("getUnreturnedConnectionTimeout: ").append(c3p0ConnectionPool.getUnreturnedConnectionTimeout());
//        } else {
//            logString.append("c3p0_Status:: ").append("c3p0ConnectionPool hasent started yet... No connections were created");
//        }
//        return logString.toString();
//    }
//    public static String validateMSISDNFormatNew(String msisdn) throws CommonException
//    {
//        String methodName = "validateMSISDNFormat";
//        Matcher matcher = null;
//        try {
//            Pattern pattern = Pattern.compile(GeneralConstants.MSISDN_VALIDATE_PATTERN);
//            matcher = pattern.matcher(msisdn);
//            if (matcher.matches())
//            {
//                if (msisdn.startsWith("00"))
//                {
//                        msisdn = msisdn.substring(2);
//                }
//                else if (msisdn.startsWith("01"))
//                {
//                        msisdn = "2" + msisdn;
//                }
//                else if (msisdn.startsWith("1"))
//                {
//                        msisdn = "20" + msisdn;
//                }
//                else if (msisdn.startsWith("+"))
//                {
//                        msisdn = msisdn.substring(1);
//                }
////                else
////                {
////                        msisdn = msisdn;
////                }
//            }
//            else
//                throw new CommonException("Invalid MSISDN", "");
//        } catch (Exception e) {
//            CommonLogger.errorLogger.error("Error While validating MSISDN [" + msisdn + "] ", e);
//            CommonLogger.businessLogger.error("Error While validating MSISDN " + msisdn + "] " + e);
//            throw new CommonException("Error While validating MSISDN [" + msisdn + "] " + e, e.getMessage());
//        }
//        return msisdn;
//    }
    public static String validateMSISDNFormatSMPP(String msisdn) throws CommonException { //2595
        Matcher matcher = null;
        try {
            String msisdnLength = SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MSISDN_LENGTH);
            String msisdnValidationPattern = SystemLookups.SYSTEM_PROPERTIES.get(Defines.INTERFACES.MSISDN_VALIDATION_PATTERN);

            if ((msisdnLength == null || msisdnLength.trim().isEmpty()) || (msisdnValidationPattern == null || msisdnValidationPattern.trim().isEmpty())) {
                throw new CommonException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE, ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.MSISDN_CONFIGS_MISSING, "Needed MSISDN configurations were not found.");
            }
            if (msisdn.length() > Integer.parseInt(msisdnLength)) {
                throw new CommonException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE, ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_MSISDN_LENGTH, "MSISDN [" + msisdn + "] length exceeded maximum length");
            }
            Pattern pattern = Pattern.compile(msisdnValidationPattern);
            matcher = pattern.matcher(msisdn);
            if (matcher.matches()) {
                if (msisdn.startsWith("00")) {
                    msisdn = msisdn.substring(2);
                } else if (msisdn.startsWith("01")) {
                    msisdn = "2" + msisdn;
                } else if (msisdn.startsWith("1")) {
                    msisdn = "20" + msisdn;
                } else if (msisdn.startsWith("+")) {
                    msisdn = msisdn.substring(1);
                }
            } else {
                throw new CommonException(GeneralConstants.SRC_ID_RETRIEVE_MESSAGE_STATUS_INTERFACE, ErrorCodes.RETRIEVE_MESSAGE_STATUS_INTERFACE.INVALID_MSISDN_PATTERN, "MSISDN [" + msisdn + "] does not match the configured pattern");
            }
        } catch (CommonException e) {
            CommonLogger.errorLogger.error("Error While validating MSISDN [" + msisdn + "] ", e);
            CommonLogger.businessLogger.error("Error While validating MSISDN " + msisdn + "] " + e.getErrorMsg());
            throw e;
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Error While validating MSISDN [" + msisdn + "] ", e);
            CommonLogger.businessLogger.error("Error While validating MSISDN " + msisdn + "] " + e);
            throw new CommonException("Error While validating MSISDN [" + msisdn + "] " + e, e.getMessage());
        }
        return msisdn;
    }

    public static String encrypt(String text, String key) throws CommonException {
        String encryptedString = "";
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, spec);
            byte[] byteDataToEncrypt = text.getBytes("UTF8");
            byte[] byteCipherText = aesCipher.doFinal(byteDataToEncrypt);
            encryptedString = new String((new BASE64Encoder()).encodeBuffer(byteCipherText).getBytes("UTF8"));
        } catch (NoSuchAlgorithmException nsae) {
            CommonLogger.errorLogger.error("No Such Algorithm Exception---->", nsae);
            CommonLogger.businessLogger.error("No Such Algorithm Exception---->" + nsae);
            throw new CommonException("No Such Algorithm Exception:text:" + text + ",key " + key, nsae.getMessage());

        } catch (NoSuchPaddingException nspe) {
            CommonLogger.errorLogger.error("NoSuchPaddingException---->", nspe);
            CommonLogger.businessLogger.error("NoSuchPaddingException---->" + nspe);
            throw new CommonException("NoSuchPaddingException:text:" + text + ",key " + key, nspe.getMessage());
        } catch (InvalidKeyException ike) {
            CommonLogger.errorLogger.error("InvalidKeyException---->", ike);
            CommonLogger.businessLogger.error("InvalidKeyException---->" + ike);
            throw new CommonException("InvalidKeyException:text:" + text + ",key " + key, ike.getMessage());
        } catch (IllegalStateException ise) {
            CommonLogger.errorLogger.error("IllegalStateException---->", ise);
            CommonLogger.businessLogger.error("IllegalStateException---->" + ise);
            throw new CommonException("IllegalStateException:text:" + text + ",key " + key, ise.getMessage());
        } catch (IllegalBlockSizeException ibse) {
            CommonLogger.errorLogger.error("IllegalBlockSizeException---->", ibse);
            CommonLogger.businessLogger.error("IllegalBlockSizeException---->" + ibse);
            throw new CommonException("IllegalBlockSizeException:text:" + text + ",key " + key, ibse.getMessage());
        } catch (BadPaddingException bpe) {
            CommonLogger.errorLogger.error("BadPaddingException---->", bpe);
            CommonLogger.businessLogger.error("BadPaddingException---->" + bpe);
            throw new CommonException("BadPaddingException:text:" + text + ",key " + key, bpe.getMessage());
        } catch (Exception exception) {
            CommonLogger.errorLogger.error("BadPaddingException---->", exception);
            CommonLogger.businessLogger.error("BadPaddingException---->" + exception);
            throw new CommonException("BadPaddingException:text:" + text + ",key " + key, exception.getMessage());
        }
        return encryptedString;
    }

    public static String decrypt(String text, String key) throws CommonException {
        String decryptedString = "";
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, spec);
            byte[] byteDecryptedText = aesCipher.doFinal((new BASE64Decoder()).decodeBuffer(text));
            decryptedString = new String(byteDecryptedText, "UTF8");

        } catch (NoSuchAlgorithmException nsae) {
            CommonLogger.errorLogger.error("No Such Algorithm Exception---->", nsae);
            CommonLogger.businessLogger.error("No Such Algorithm Exception---->" + nsae);
            throw new CommonException("No Such Algorithm Exception:text:" + text + ",key " + key, nsae.getMessage());

        } catch (NoSuchPaddingException nspe) {
            CommonLogger.errorLogger.error("NoSuchPaddingException---->", nspe);
            CommonLogger.businessLogger.error("NoSuchPaddingException---->" + nspe);
            throw new CommonException("NoSuchPaddingException:text:" + text + ",key " + key, nspe.getMessage());

        } catch (InvalidKeyException ike) {
            CommonLogger.errorLogger.error("InvalidKeyException---->", ike);
            CommonLogger.businessLogger.error("InvalidKeyException---->" + ike);
            throw new CommonException("InvalidKeyException:text:" + text + ",key " + key, ike.getMessage());

        } catch (IllegalStateException ise) {
            CommonLogger.errorLogger.error("IllegalStateException---->", ise);
            CommonLogger.businessLogger.error("IllegalStateException---->" + ise);
            throw new CommonException("IllegalStateException:text:" + text + ",key " + key, ise.getMessage());

        } catch (IllegalBlockSizeException ibse) {
            CommonLogger.errorLogger.error("IllegalBlockSizeException---->", ibse);
            CommonLogger.businessLogger.error("IllegalBlockSizeException---->" + ibse);
            throw new CommonException("IllegalBlockSizeException:text:" + text + ",key " + key, ibse.getMessage());

        } catch (BadPaddingException bpe) {
            CommonLogger.errorLogger.error("BadPaddingException---->", bpe);
            CommonLogger.businessLogger.error("BadPaddingException---->" + bpe);
            throw new CommonException("BadPaddingException:text:" + text + ",key " + key, bpe.getMessage());

        } catch (Exception exception) {
            CommonLogger.errorLogger.error("BadPaddingException---->", exception);
            CommonLogger.businessLogger.error("BadPaddingException---->" + exception);
            throw new CommonException("BadPaddingException:text:" + text + ",key " + key, exception.getMessage());

        }
        return decryptedString;
    }

    //CSPhase1.5 | Esmail.Anbar | Adding Template Update
    //Centralized Function
    public static String templateValidator(InputModel input, String templateText, String templateReplacmentCSV) throws InterfacesBusinessException {
        String[] templateVsValues = templateReplacmentCSV.split("(?<!\\\\),");
        String[] replacementString;
        int replcaementCount = 0;
        Pattern pattern = Pattern.compile(Defines.INTERFACES.TEMPLATE_REPLACE_REGEX);

        if (templateText != null) {
            while (pattern.matcher(templateText).matches()) {
                if (replcaementCount >= templateVsValues.length) {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.TEMPLATE_PARAMETERS_ARE_INVALID,
                            "Template replacement values in input are less than values defined for template... ReplacementInputCount: "
                            + templateVsValues.length + " | Configured count in Template: " + replcaementCount);
                    CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                    throw ibe;
                }

                if (templateVsValues[replcaementCount].contains(",")) {
                    templateVsValues[replcaementCount] = templateVsValues[replcaementCount].replaceAll("\\\\,", ",");
                }
                if (templateVsValues[replcaementCount].contains(";")) {
                    templateVsValues[replcaementCount] = templateVsValues[replcaementCount].replaceAll("\\\\;", ";");
                }

                replacementString = templateVsValues[replcaementCount].split("=");

//                if (!templateText.contains(replacementString[0]))
//                {
//                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.TEMPLATE_PARAMETERS_ARE_INVALID,
//                            "Template Doesnt Have A Replacement for Entered Input Parameter: " + replacementString[0]);
//                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
//                    throw ibe;
//                }
                try {
                    templateText = templateText.replaceAll("\\Q" + replacementString[0] + "\\E", replacementString[1]);
                } catch (Exception e) {
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.TEMPLATE_PARAMETERS_ARE_INVALID, "Template Parameter value is Invalid... TemplateParameterValue: " + templateVsValues[replcaementCount]);
                    CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                    throw ibe;
                }
                replcaementCount++;
            }

            return templateText;
        } else {
            throw new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE, ErrorCodes.SEND_SMS.TEMPLATE_PARAMETERS_ARE_INVALID, "Template Text is Null");
        }
    }

    //Centralized Function
    public static long generateCsMsgId(String interfaceName) {
//        CommonLogger.businessLogger.info(transId + " || " + "Started " + interfaceName + ".generateCsMsgId()...");

        long csMsgId = 0;
        Random randomGen = new Random();
        StringBuilder csMsgIdString = new StringBuilder();

        long currentSysTime = System.nanoTime();
        int randomInt = randomGen.nextInt(1000);
        long servletThreadId = Thread.currentThread().getId();
        long reversedSystemTime = 0;
//        long instanceId = Long.parseLong(Initializer.propertiesFileBundle.getString(Defines.INTERFACES.INSTANCE_ID));

        CommonLogger.businessLogger.info("Generated System.nanoTime() = " + currentSysTime);

        while (currentSysTime != 0) {
            reversedSystemTime = reversedSystemTime * 10;
            reversedSystemTime = reversedSystemTime + currentSysTime % 10;
            currentSysTime = currentSysTime / 10;
        }

        CommonLogger.businessLogger.info("Reversed System.nanoTime() = " + reversedSystemTime
                + " || " + "servletThreadId = " + servletThreadId
                + " || " + "randomInt = " + randomInt);

        csMsgIdString
                //.append(instanceId)
                .append(reversedSystemTime)
                .append(servletThreadId)
                .append(randomInt);

        CommonLogger.businessLogger.info("Before Trimming: csMsgIdString = " + csMsgIdString);

        try {
            if (csMsgIdString.length() > 19) {
                csMsgId = Long.parseLong(csMsgIdString.substring(0, 19));
            } else {
                csMsgId = Long.parseLong(csMsgIdString.toString());
            }
        } catch (Exception e) {
            CommonLogger.businessLogger.info("Attempting to trim on 18 digits | " + e.getMessage());
//            CommonLogger.errorLogger.error(e.getMessage(), e);

            csMsgId = Long.parseLong(csMsgIdString.substring(0, 18));
        }

        CommonLogger.businessLogger.info("After Trimming: csMsgIdString = " + csMsgIdString);
        //csMsgIdString.substring(0, 19);
        //csMsgId = Long.parseLong(csMsgIdString.toString());

        CommonLogger.businessLogger.info("Generated CsMsgId = " + csMsgId);
//        CommonLogger.businessLogger.info(transId + " || " + "Ended " + interfaceName + ".generateCsMsgId()...");
        return csMsgId;
    }

    //Centralized Function
    public static void logInterfaceResult(String transId, String requestBody, String decodedURL, String output, String status, long timeInMs, String interfaceName, ArrayBlockingQueue<InterfacesLogModel> requestsToBeLogged) {
        InterfacesLogModel logModel = new InterfacesLogModel();
        try {
            logModel.setInterfaceInputURL(decodedURL);
            logModel.setInterfaceName(interfaceName);
            logModel.setInterfaceOutput(output);
            logModel.setRequestStatus(status);
            logModel.setResponseTime(timeInMs);
            logModel.setTransId(transId);
            logModel.setRequestBody(requestBody);

            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Result")
                    .put(GeneralConstants.StructuredLogKeys.URL, decodedURL)
                    .put(GeneralConstants.StructuredLogKeys.INTERFACE_NAME, interfaceName)
                    .put(GeneralConstants.StructuredLogKeys.REQUEST_BODY, requestBody)
                    .put(GeneralConstants.StructuredLogKeys.OUTPUT, output)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, status)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, timeInMs).build());
//            CommonLogger.businessLogger.info("Logging Object: "
//                    + " | " + "URL: " + decodedURL + " | " + "InterfaceName: " + interfaceName
//                    + " | " + "RequestBody: " + requestBody + " | " + "Output: " + output + " | " + "Status: " + status
//                    + " | " + "ResponseTime: " + timeInMs);

            requestsToBeLogged.put(logModel);
        } catch (InterruptedException ex) {
            CommonLogger.businessLogger.error("Error While Adding Interface Request to Logging Queue... | " + ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
//            CommonLogger.businessLogger.info("Error While Adding Interface Request to Logging Queue...");
        } //        catch (UnsupportedEncodingException ex)
        //        {
        //            CommonLogger.businessLogger.error(ex.getMessage());
        //            CommonLogger.errorLogger.error(ex.getMessage(), ex);
        //            CommonLogger.businessLogger.info(transId + " || " + "Error While Decoding Interface Request for Logging Queue...");
        //        }
        catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

    public static void logRESTResult(String transId, String requestBody, String decodedURL, String output, String status, long timeInMs, String interfaceName, ArrayBlockingQueue<RESTLogModel> loggingQueue) {
        RESTLogModel logModel = new RESTLogModel();
        try {
            if (decodedURL.length() > 4000) {
                decodedURL = decodedURL.substring(0, 4000);
            }

            if (status.length() > 4000) {
                status = status.substring(0, 4000);
            }
            logModel.setInputURL(decodedURL);
            logModel.setRestName(interfaceName);
            logModel.setRestOutput(output);
            logModel.setRequestStatus(status);
            logModel.setResponseTime(timeInMs);
            logModel.setTransId(transId);
            logModel.setRequestBody(requestBody);

            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Result")
                    .put(GeneralConstants.StructuredLogKeys.URL, decodedURL)
                    .put(GeneralConstants.StructuredLogKeys.INTERFACE_NAME, interfaceName)
                    .put(GeneralConstants.StructuredLogKeys.REQUEST_BODY, requestBody)
                    .put(GeneralConstants.StructuredLogKeys.OUTPUT, output)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, status)
                    .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, timeInMs).build());
//            CommonLogger.businessLogger.info(transId + " || " + "Logging Object: "
//                    + " | " + "TransId: " + transId + " | " + "URL: " + decodedURL + " | " + "InterfaceName: " + interfaceName
//                    + " | " + "RequestBody: " + requestBody + " | " + "Output: " + output + " | " + "Status: " + status
//                    + " | " + "ResponseTime: " + timeInMs);

            loggingQueue.put(logModel);
        } catch (InterruptedException ex) {
            CommonLogger.businessLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            CommonLogger.businessLogger.info("Error While Adding Interface Request to Logging Queue...");
        } //        catch (UnsupportedEncodingException ex)
        //        {
        //            CommonLogger.businessLogger.error(ex.getMessage());
        //            CommonLogger.errorLogger.error(ex.getMessage(), ex);
        //            CommonLogger.businessLogger.info(transId + " || " + "Error While Decoding Interface Request for Logging Queue...");
        //        }
        catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
    }

    //Centralized Function
    public static void addMessageToQueues(String transId, InputModel input, ServicesModel service, String ipAddress, HashMap<Long, ArrayBlockingQueue<SmsBusinessModel>> smsToBeSent, ArrayBlockingQueue<SMSHistoryModel> messagesToBeArchived) throws CommonException {
        try {
//            if (input.getSubmissionDate() == null)
//                input.setSubmissionDate(systemSubmissionDate);

            //CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
            EnqueueModelREST smsModel = new EnqueueModelREST(
                    input.getCsMsgId(),
                    service.getQueueModel().getAppName(),
                    input.getOriginatorMSISDN(),
                    input.getDestinationMSISDN(),
                    input.getMessageText(),
                    input.getMessageType(),
                    input.getOriginatorType(),
                    input.getLanguage(),
                    0,
                    0,
                    input.getConcatNumber(),
                    0,
                    ipAddress,
                    service.getDeliveryReport(),
                    input.getOptionalParam1(),
                    input.getOptionalParam2(),
                    input.getOptionalParam3(),
                    input.getOptionalParam4(),
                    input.getOptionalParam5(),
                    new Timestamp(input.getSubmissionDate().getTime()),
                    input.getExpirationHours(),
                    input.getTlvs(),
                    input.getRequestId(),
                    input.getServiceType(),
                    input.getEsmClass(),
                    input.getProtocolId(),
                    input.getPriorityFlag(),
                    input.getScheduleDeliveryTime(),
                    input.getValidityPeriod(),
                    input.getSmDefaultMsgId());

            SmsBusinessModel smsBusinessModel = new SmsBusinessModel(smsModel, String.valueOf(input.getCsMsgId()), input.getDestinationMSISDN(), input.getSystemName(), input.getExpirationDate(), input.isUpdatedSystemQuota(), input.isUpdatedSystemMonitor(), input.isUpdatedDoNotApply(), input.getDayInSmsStats(), true, input.isUpdatedCustomerStatistics(), input.getCampaignId(), ipAddress, transId, Defines.VFE_CS_SMS_H_STATUS_LK.FAILED_ENQUEUE);

            smsToBeSent.get(service.getQueueModel().getVersionId()).put(smsBusinessModel);

            messagesToBeArchived.put(new SMSHistoryModel(
                    input.getCsMsgId(),
                    input.getDestinationMSISDN(),
                    new Timestamp(input.getSubmissionDate().getTime()),
                    null,
                    input.getMessageText(),
                    Defines.VFE_CS_SMS_H_STATUS_LK.ENQUEUED,
                    null,
                    input.getConcatNumber(),
                    0,
                    0,
                    0,
                    0,
                    input.getMessageType(),
                    input.isDoNotApply() ? 1 : 0,
                    input.isViolateFlag() ? 1 : 0,
                    service.getSystemCategory(),
                    input.getMessagePriority(),
                    Integer.parseInt(String.valueOf(Long.parseLong(input.getDestinationMSISDN()) % Defines.MOD_X)),
                    service.getVersionId(),
                    input.getOriginatorMSISDN(),
                    input.getOptionalParam1(),
                    input.getOptionalParam2(),
                    input.getOptionalParam3(),
                    input.getOptionalParam4(),
                    input.getOptionalParam5(),
                    0,
                    service.getQueueModel().getAppName(),
                    service.getDeliveryReport()));
        } //        catch (SQLException e)
        //        {
        //            CommonLogger.businessLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + e.getMessage());
        //            CommonLogger.errorLogger.error(transId + " || csMsgId: " + input.getCsMsgId() + " | " + e.getMessage(), e);
        //            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        //        }
        catch (InterruptedException e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.toString() + " || " + e.getMessage());
            CommonLogger.errorLogger.error(e.toString() + " || " + e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }

    //Centralized Function
    public static InputModel getInputModel(long csMsgId, String originatorMSISDN, String destinationMSISDN, String messageText, String originatorTypeTemp,
            String messageTypeTemp, String lang, String systemName, String doNotApplyTemp, String messagePriorityTemp, String optionalParam1, String optionalParam2,
            String optionalParam3, String optionalParam4, String optionalParam5, String templatesIds, String templatesParameters, String submissionDateString) throws InterfacesBusinessException {
        Byte language = null;
        byte originatorType;
        byte messageType;
        boolean doNotApply = false;
        byte messagePriority;
        //CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
        Date expirationDate = null;
        Date submissionDate = null;

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Interface Parameters (Raw)")
                .put("ORIGINATOR_" + GeneralConstants.StructuredLogKeys.MSISDN, originatorMSISDN)
                .put(GeneralConstants.StructuredLogKeys.MSISDN, destinationMSISDN)
                .put(GeneralConstants.StructuredLogKeys.MESSAGE, messageText)
                .put(GeneralConstants.StructuredLogKeys.ORIG_TYPE, originatorTypeTemp)
                .put(GeneralConstants.StructuredLogKeys.MSG_TYPE, messageTypeTemp)
                .put(GeneralConstants.StructuredLogKeys.LANGUAGE, lang)
                .put(GeneralConstants.StructuredLogKeys.SYSTEM_NAME, systemName)
                .put(GeneralConstants.StructuredLogKeys.DO_NOT_APPLY, doNotApplyTemp)
                .put(GeneralConstants.StructuredLogKeys.MESSAGE_PRIORITY, messagePriorityTemp)
                .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_1, optionalParam1)
                .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_2, optionalParam2)
                .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_3, optionalParam3)
                .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_4, optionalParam4)
                .put(GeneralConstants.StructuredLogKeys.OPTIONAL_PARAMETER_5, optionalParam5)
                .put(GeneralConstants.StructuredLogKeys.TEMPLATE_IDs, templatesIds)
                .put(GeneralConstants.StructuredLogKeys.TEMPLATE_PARAMETERS, templatesParameters)
                .put(GeneralConstants.StructuredLogKeys.SUBMISSION_DATE, submissionDateString)
                .build());

        if (originatorMSISDN == null || originatorMSISDN.isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.ORIGINATOR_MSISDN_IS_MISSING, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        if (destinationMSISDN == null || destinationMSISDN.isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.DESTINATION_MSISDN_IS_MISSING, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
//        if (((templatesIds == null || templatesIds.isEmpty()) && (templatesParameters != null && !templatesParameters.isEmpty()))
//        || ((templatesIds != null && !templatesIds.isEmpty()) && (templatesParameters == null || templatesParameters.isEmpty())))
//        {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                ErrorCodes.SEND_SMS.INVALID_TEMPLATE_INPUT, null);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
//            throw ibe;
//        }
//        if ((templatesIds == null || templatesIds.isEmpty())
//        && (templatesParameters == null || templatesParameters.isEmpty())
//        && (messageText == null || messageText.isEmpty()))
//        {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                ErrorCodes.SEND_SMS.EMPTY_MESSAGE_TEXT, null);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
//            throw ibe;
//        }
//        if (((templatesIds == null || templatesIds.isEmpty()) && !(templatesParameters == null || templatesParameters.isEmpty()))
//        || (!(templatesIds == null || templatesIds.isEmpty()) && (templatesParameters == null || templatesParameters.isEmpty()))
//        || ((templatesIds != null && templatesIds.isEmpty()) && (templatesParameters != null && templatesParameters.isEmpty())))
//        {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                ErrorCodes.SEND_SMS.INVALID_TEMPLATE_INPUT, null);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
//            throw ibe;
//        }
//        if (templatesIds == null && templatesParameters == null && (messageText == null || messageText.isEmpty()))
//        {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                ErrorCodes.SEND_SMS.EMPTY_MESSAGE_TEXT, null);
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + csMsgId + " | " + ibe.getDetailedMessage());
//            throw ibe;
//        }
        if ((templatesIds == null && templatesParameters != null)
                || (templatesIds != null && templatesParameters == null)
                || (templatesIds != null && templatesParameters != null
                && ((templatesIds.isEmpty() && !templatesParameters.isEmpty())
                || (!templatesIds.isEmpty() && templatesParameters.isEmpty())
                || (templatesIds.isEmpty() && templatesParameters.isEmpty())))) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.INVALID_TEMPLATE_INPUT, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        if (templatesIds == null && templatesParameters == null && (messageText == null || messageText.isEmpty())) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.EMPTY_MESSAGE_TEXT, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        if (originatorTypeTemp == null || originatorTypeTemp.isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.ORIGINATOR_TYPE_IS_MISSING, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        if (messageTypeTemp == null || messageTypeTemp.isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.MESSAGE_TYPE_IS_MISSING, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        if (templatesIds == null && templatesParameters == null && messageText != null && (lang == null || lang.isEmpty())) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.LANGUGAGE_IS_MISSING, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        if (systemName == null || systemName.isEmpty()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.SYSTEM_NAME_IS_MISSING, null);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }

        try {
            if (lang != null)//Need to check language only if there is a messageText
            {
                language = Byte.valueOf(lang);
            }
        } catch (NumberFormatException e) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.LANGUAGE_NOT_DEFINED, e.getMessage());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }

        try {
            originatorType = Byte.valueOf(originatorTypeTemp);
        } catch (NumberFormatException e) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.ORIGINATOR_TYPE_NOT_DEFINED, e.getMessage());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }

        try {
            messageType = Byte.valueOf(messageTypeTemp);
        } catch (NumberFormatException e) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.MESSAGE_TYPE_NOT_DEFINED, e.getMessage());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }

        if (doNotApplyTemp != null) {
            if (doNotApplyTemp.equalsIgnoreCase(Defines.STRING_TRUE) || doNotApplyTemp.equalsIgnoreCase(Defines.STRING_FALSE)) {
                doNotApply = Boolean.valueOf(doNotApplyTemp.toLowerCase());
            } else {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                        ErrorCodes.SEND_SMS.INVALID_DO_NOT_APPLY_FLAG, "Entered Value:" + doNotApplyTemp);
                CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                throw ibe;
            }
        }

        if (messagePriorityTemp == null || messagePriorityTemp.isEmpty()) {
            messagePriority = 0;
        } else {
            switch (messagePriorityTemp.toLowerCase()) {
                case "high":
                    messagePriority = Defines.INTERFACES.MESSAGE_PRIORITY_HIGH;
                    break;
                case "normal":
                    messagePriority = Defines.INTERFACES.MESSAGE_PRIORITY_NORMAL;
                    break;
                default:
                    InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                            ErrorCodes.SEND_SMS.INVALID_MESSAGE_PRIORITY, "Entered MessagePriority: " + messagePriorityTemp);
                    CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                    throw ibe;
            }
        }

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Message Priority Parsed")
                .put(GeneralConstants.StructuredLogKeys.MESSAGE_PRIORITY, messagePriority)
                .build());

        if (submissionDateString != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(Defines.INTERFACES.SUBMISSION_DATE_FORMAT);
                submissionDate = sdf.parse(submissionDateString);
                CommonLogger.businessLogger.info("Submission Date Set with " + submissionDateString);
            } catch (Exception e) {
                CommonLogger.businessLogger.info("Invalid Submission Date therefore neglecting it... Submission Date Entered: " + submissionDateString);
            }
        }

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Submission Date Parsed")
                .put(GeneralConstants.StructuredLogKeys.SUBMISSION_DATE, submissionDate)
                .build());

//        CommonLogger.businessLogger.info("Interface Parameters :{originatorMSISDN: " + originatorMSISDN + ", destinationMSISDN: "
//                + destinationMSISDN + ", messageText: " + messageText + ", originatorType: " + originatorTypeTemp
//                + ", messageType: " + messageTypeTemp + ", lang: " + language + ", systemName: " + systemName
//                + ", doNotApply: " + doNotApplyTemp + ", messagePriority: " + messagePriorityTemp + ", optionalParam1: "
//                + optionalParam1 + ", optionalParam2: " + optionalParam2 + ", optionalParam3: " + optionalParam3
//                + ", optionalParam4: " + optionalParam4 + ", optionalParam5: " + optionalParam5 + "}");
        CommonLogger.businessLogger.info("Ended SendSmsInterface.getParameters()...");
        InputModel input = new InputModel(originatorMSISDN, destinationMSISDN, messageText, originatorType, messageType, language,
                systemName, doNotApply, messagePriority, false, optionalParam1, optionalParam2, optionalParam3,
                optionalParam4, optionalParam5, expirationDate, templatesIds, templatesParameters);
        input.setSubmissionDate(submissionDate);
        input.setCsMsgId(csMsgId);
        return input;
    }

    //Centralized Function
    public static void basicServiceValidation(ServicesModel service, String ipAddress) throws InterfacesBusinessException {
        //Check system existance
        if (service == null) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.SERVICE_NAME_NOT_DEFINED, "Service Not Found");
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Service...");

        //Check IP existance
        if (service.getHasWhitelist() == 1) {
            CommonLogger.businessLogger.info("Service Has Whitelist...");

            if (service.getWhiteListModel() == null) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                        ErrorCodes.SEND_SMS.IP_NOT_DEFINED, "ipAddress " + ipAddress + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }

            boolean ipExists = false;
            for (ServiceWhitelistModel whitelistIp : service.getWhiteListModel()) {
                if (whitelistIp.getIpAddress().equals(ipAddress)) {
                    CommonLogger.businessLogger.info("Valid IP Address...");
                    ipExists = true;
                    break;
                }
            }
            if (!ipExists) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                        ErrorCodes.SEND_SMS.IP_NOT_DEFINED, "ipAddress " + ipAddress + " Doesnt Exist in Whitelist");
                CommonLogger.businessLogger.error(ibe.getDetailedMessage());
                throw ibe;
            }
        } else {
            CommonLogger.businessLogger.info("Service Has No Whitelist...");
        }
    }

    //Centralized Function
    public static void basicParametersValidation(ServicesModel service, InputModel input, com.asset.contactstrategy.interfaces.service.MainService mainService) throws InterfacesBusinessException, CommonException, Exception {
        //Validate Service Privilege
        if (service.getPrivilegeLevel() == Defines.INTERFACES.DEDICATED) {
            if (service.getOriginatorType() != input.getOriginatorType()
                    || !SystemLookups.ORIGINATOR_VALUES_LK.get(service.getOriginator()).getLable().equalsIgnoreCase(input.getOriginatorMSISDN())) {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                        ErrorCodes.SEND_SMS.INVALID_ORIGINATOR_OR_ORIGINATOR_TYPE_FOR_DEDICATED_SERVICE, "Service is Dedicated and failed validation... Entered OriginatorType: "
                        + input.getOriginatorType() + ", Entered Originator MSISDN: " + input.getOriginatorMSISDN());
                CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                throw ibe;
            }
        }
        CommonLogger.businessLogger.info("Valid Service Privilege...");

        //Check destination MSISDN format
        try {
            //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
            input.setDestinationMSISDN(Utility.validateMSISDNFormatSMPP(input.getDestinationMSISDN()));
        } catch (CommonException ce) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.DESTINATION_MSISDN_NOT_VALID, "MSISDN doesnt match Regular Expression: " + input.getDestinationMSISDN());
            CommonLogger.businessLogger.error(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Destination MSISIDN Format...");

        //Validate Orginator type
        if (!SystemLookups.ORIGINATORS_LIST.containsKey(Integer.valueOf(input.getOriginatorType()))) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.ORIGINATOR_TYPE_NOT_DEFINED, "Entered Originator Type: " + input.getOriginatorType());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Originator Type...");

        //Check orginator MSISDN format
        if (!checkOrignatorType(input.getOriginatorMSISDN(), (int) input.getOriginatorType())) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.ORIGINATOR_MSISDN_NOT_VALID, "Originator Msisdn Violated Originator type's Rules: " + input.getOriginatorMSISDN());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Originator MSISIDN Format...");

        //Validate Message Type
        if (input.getMessageType() != Defines.INTERFACES.MESSAGE_TYPE_TEXT_MESSAGE) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.MESSAGE_TYPE_NOT_DEFINED, "Entered Message Type: " + input.getMessageType());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Message Type...");

        //Check Service Quota
        long timeLogging = System.currentTimeMillis();
        boolean hasQuota = com.asset.contactstrategy.interfaces.service.MainService.updateServiceQuota(service.getServiceId(), 1, service.getDailyQuota(), DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER);
        CommonLogger.businessLogger.info("Service Qouta: " + service.getDailyQuota() + " | Time: " + (System.currentTimeMillis() - timeLogging) + " msc");
        if (!hasQuota) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.SERVICE_QUOTA_VALIDATION_ERROR, "Daily Qouta For Service Has Expired... Service Name: " + service.getServiceName());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Service Qouta...");
        input.setUpdatedSystemQuota(true);
    }

    //Centralized Function
    public static void validateCustomerSendSMS(InputModel input, com.asset.contactstrategy.interfaces.service.MainService mainService) throws CommonException, InterfacesBusinessException {
        CustomersModel customer = mainService.getCustomer(input);
//        if (customer == null || customer.getMsisdn() == null || customer.getMsisdn().equals("")) {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                    ErrorCodes.SEND_SMS.DESTINATION_MSISDN_NOT_VALID, "Destination MSISDN doesnt Exist in DWH Table... MSISDN Entered: " + input.getDestinationMSISDN());
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
//            throw ibe;
        //Esmail.Anbar | 22/1/2018 | Updating to complete execution when customer is not in DWH Table
//            customer = new CustomersModel();
        customer.setMsisdn(input.getDestinationMSISDN());
        customer.setLastTwoDigits(Integer.valueOf(input.getDestinationMSISDN().substring(input.getDestinationMSISDN().length() - 2)));
        if (input.getLanguage() != null) {
            customer.setLanguage(input.getLanguage().toString());
        } else {
            customer.setLanguage(SystemLookups.SYSTEM_PROPERTIES.get(Defines.ARABIC_LANG));
        }
        CommonLogger.businessLogger.info("Customer not found in DWH Table... using default settings");
//        }
        input.setCustomer(customer);
        if (input.getTemplatesIds() != null && !input.getTemplatesIds().isEmpty() && input.getLanguage() == null)//if there is a template
        {
            if (customer.getLanguage().equalsIgnoreCase(SystemLookups.SYSTEM_PROPERTIES.get(Defines.ENGLISH_LANG))) {
                input.setLanguage((byte) Defines.LANGUAGE_ENGLISH);
            } else {
                input.setLanguage((byte) Defines.LANGUAGE_ARABIC);
            }
        }
        CommonLogger.businessLogger.info("Destination MSISDN Exists in DWH...");
    }

    //Centralized Function
    public static void validateCustomerSendSMSBulk(InputModel input, HashMap<String, CustomersModel> customersMap, com.asset.contactstrategy.interfaces.service.MainService mainService) throws CommonException, InterfacesBusinessException {
        if (customersMap.get(input.getDestinationMSISDN()) == null) {
//            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
//                    ErrorCodes.SEND_SMS.DESTINATION_MSISDN_NOT_VALID, "Destination MSISDN doesnt Exist in DWH Table... MSISDN Entered: " + input.getDestinationMSISDN());
//            CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | " + ibe.getDetailedMessage());
//            throw ibe;
            //Esmail.Anbar | 22/1/2018 | Updating to complete execution when customer is not in DWH Table
            CustomersModel customer = new CustomersModel();
            customer.setMsisdn(input.getDestinationMSISDN());
            customer.setLastTwoDigits(Integer.valueOf(input.getDestinationMSISDN().substring(input.getDestinationMSISDN().length() - 2)));
            if (input.getLanguage() != null) {
                customer.setLanguage(input.getLanguage().toString());
            } else {
                customer.setLanguage(SystemLookups.SYSTEM_PROPERTIES.get(Defines.ARABIC_LANG));
            }
            customersMap.put(customer.getMsisdn(), customer);
            CommonLogger.businessLogger.info("Customer not found in DWH Table... using default settings");
        }

        if (input.getTemplatesIds() != null && !input.getTemplatesIds().isEmpty() && input.getLanguage() == null)//if there is a template
        {
            if (customersMap.get(input.getDestinationMSISDN()).getLanguage().equalsIgnoreCase(SystemLookups.SYSTEM_PROPERTIES.get(Defines.ENGLISH_LANG))) {
                input.setLanguage((byte) Defines.LANGUAGE_ENGLISH);
            } else {
                input.setLanguage((byte) Defines.LANGUAGE_ARABIC);
            }
        }
        CommonLogger.businessLogger.info("Destination MSISDN Exists in DWH...");
    }

    //Centralized Function
    public static void templateAndMessageValidation(InputModel input, ServicesModel service) throws CommonException, InterfacesBusinessException {
        //Validate Language
        if (input.getLanguage() != null && input.getLanguage() != Defines.LANGUAGE_ENGLISH && input.getLanguage() != Defines.LANGUAGE_ARABIC) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.LANGUAGE_NOT_DEFINED, "Entered Language is not Defined... Language Entered: " + input.getLanguage());
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Language...");

        //Validate Message Template
        if (input.getTemplatesIds() != null && !input.getTemplatesIds().isEmpty()) {
            int templateId = -1;
            String templateMessage = "";
            String[] templatesIdsArray = input.getTemplatesIds().split("(?<!\\\\),");
            if (input.getTemplatesParameters().startsWith(";")) {
                input.setTemplatesParameters(" " + input.getTemplatesParameters());
            }
            while (input.getTemplatesParameters().contains(";;")) {
                input.setTemplatesParameters(input.getTemplatesParameters().replace(";;", "; ;"));
            }
            String[] templatesParametersArray = input.getTemplatesParameters().split("(?<!\\\\);");
            if (templatesIdsArray.length == templatesParametersArray.length) {
                for (int i = 0; i < templatesIdsArray.length; i++) {
                    try {//To prevent number format exception
                        templateId = Integer.parseInt(templatesIdsArray[i]);
                    } catch (Exception e) {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                ErrorCodes.SEND_SMS.INVALID_TEMPLATES_INPUT, "Invalid Parameters for Template");
                        CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                        throw ibe;
                    }
                    TemplateModel template = SystemLookups.TEMPLATES.get(templateId);
                    String templateText;

                    if (template != null) {
                        if (i == 0) {
                            //CSPhase1.5 | Esmail.Anbar | Adding Expiration Duration Update
                            if (template.getExpirationDuration() != null && template.getExpirationDuration() != 0) {
                                try {
                                    int expiration = template.getExpirationDuration();
                                    Calendar c = Calendar.getInstance();
                                    c.setTime(input.getSubmissionDate());
                                    c.add(Calendar.HOUR, expiration);
                                    input.setExpirationDate(c.getTime());
                                    input.setExpirationHours(expiration);

                                    if (input.getExpirationDate().getTime() < System.currentTimeMillis()) {
//                                        CommonLogger.businessLogger.info(transId + " || Expiration Duration Exceeded System Current Time, Expiration Date " + input.getExpirationDate() + ", for TemplateId: " + template.getTemplateId());
                                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                                ErrorCodes.SEND_SMS.MESSAGE_EXPIRATION_DURATION_WAS_EXCEEDED, "Expiration Duration Exceeded System Current Time Therefore Neglecting Message, Expiration Date " + input.getExpirationDate() + ", for TemplateId: " + template.getTemplateId());
                                        CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                                        throw ibe;
                                    }

                                    CommonLogger.businessLogger.info("Expiration Duration Set with " + expiration + "hrs from TemplateId: " + template.getTemplateId());
                                } catch (InterfacesBusinessException e) {
//                                    CommonLogger.businessLogger.info(transId + " || csMsgId: " + input.getCsMsgId() + " | Message Expiration Duration reached for TemplateId: " + template.getTemplateId() + " therefore Will dismiss the message... Expiration Duration Value: " + template.getExpirationDuration());
                                    throw e;
                                } catch (Exception e) {
                                    CommonLogger.businessLogger.info("Invalid Expiration Duration set for TemplateId: " + template.getTemplateId() + " therefore neglecting it... Expiration Duration Value: " + template.getExpirationDuration());
                                }
                            } else {
                                CommonLogger.businessLogger.info("No Expiration Duration set for TemplateId: " + template.getTemplateId());
                            }
                        }

                        if (input.getLanguage() == Defines.LANGUAGE_ENGLISH) {
                            templateText = template.getEnglishScript();
                        } else {
                            templateText = template.getArabicScript();
                        }

                        CommonLogger.businessLogger.info("Template before replacement: " + templateText);

                        if (i > 0) {
                            templateMessage += " ";
                        }
                        templateMessage += Utility.templateValidator(input, templateText, templatesParametersArray[i]);

                        CommonLogger.businessLogger.info("Template after replacement: " + templateMessage);
                        CommonLogger.businessLogger.info("Valid Message Template...");
                        CommonLogger.businessLogger.info("Setted Message with Template text...");
                    } else {
                        InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                                ErrorCodes.SEND_SMS.TEMPLATE_NOT_FOUND, "Template with id: " + templatesIdsArray[i] + " was not found");
                        CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                        throw ibe;
                    }
                }
                input.setMessageText(templateMessage);
            } else {
                InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                        ErrorCodes.SEND_SMS.INVALID_TEMPLATES_INPUT, "Invalid Parameters for Template");
                CommonLogger.businessLogger.info(ibe.getDetailedMessage());
                throw ibe;
            }
        }

        //Validate Text Message
        if (input.getMessageText() == null || input.getMessageText().equals("")) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.MESSAGE_TEXT_NULL_OR_EMPTY, "");
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        }
        CommonLogger.businessLogger.info("Valid Message Text...");

        //Validate allowed SMS
        int numOfMsgs = Utility.calcConcMsgCount(input.getMessageText().length(), input.getLanguage());
        CommonLogger.businessLogger.info("SMS Concat Count Needed: " + numOfMsgs);
        if (numOfMsgs > service.getAllowedSms()) {
            InterfacesBusinessException ibe = new InterfacesBusinessException(GeneralConstants.SRC_ID_SEND_SMSs_INTERFACE,
                    ErrorCodes.SEND_SMS.MAX_ALLOWED_SMSs_NOT_VALID, "Error No of Concat Messages Needed more than Available for Service..." + numOfMsgs);
            CommonLogger.businessLogger.info(ibe.getDetailedMessage());
            throw ibe;
        } else {
            input.setConcatNumber(numOfMsgs);
        }

        CommonLogger.businessLogger.info("Valid SMS Concat Count...");
    }

//    public static Object convertJSONStringToClassObject(String jsonString, Class classObject) throws IOException
//    {
//        //JSON from String to Object
//        return new ObjectMapper().readValue(jsonString, classObject);
//    }
//
//    public static String jacksonObjectToJSONString(Object javaObject) throws JsonProcessingException {
//        //Object to JSON in String
//        return new ObjectMapper().writeValueAsString(javaObject);
//    }
//
//    public static HashMap<String, Object> jacksonJSONStringToHashMapObject(String jsonString) throws IOException {
//        //JSON from String to Object
//        return new ObjectMapper().readValue(jsonString, new TypeReference<HashMap<String, Object>>() {
//        });
//    }
    //Centralized Function
    public static String gsonObjectToJSONString(Object javaObject) {
        //Object to JSON in String
        return new Gson().toJson(javaObject);
    }

    public static String gsonObjectToJSONStringWithDateFormat(Object javaObject, String dateFormat) {
        //Object to JSON in String
        Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
        return gson.toJson(javaObject);
    }

    //Centralized Function
    public static HashMap<String, Object> gsonJSONStringToHashMapObject(String jsonString) {
        //JSON from String to Object
        return new Gson().fromJson(jsonString, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

    //Centralized Function
    public static List<Object> gsonJSONStringToList(String jsonString) {
        //JSON from String to Object
        return new Gson().fromJson(jsonString, new TypeToken<List<Object>>() {
        }.getType());
    }

    public static List<TLVOptionalModel> gsonTlvsJSONStringToList(String jsonString) {
        //JSON from String to Object
//        TLVOptionalModel tlvs=new TLVOptionalModel();
        return new Gson().fromJson(jsonString, new TypeToken<List<TLVOptionalModel>>() {
        }.getType());
    }

    //Centralized Function
    public static RESTResponseModel gsonJSONStringToRESTResponseModel(String jsonString) {
        //JSON from String to Object
        return new Gson().fromJson(jsonString, new TypeToken<RESTResponseModel>() {
        }.getType());
    }

    public static DequeuerResponseModel gsonJSONStringToDequeuerResponseModel(String jsonString) {
        //JSON from String to Object
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        return gson.fromJson(jsonString, new TypeToken<DequeuerResponseModel>() {
        }.getType());
    }

    //2595 JSON Constructor in SMS Interface bridging engine.. Function used to prepare an object to hit to the HTTP Batch
    public static RequestPreparator transformSMSJSONStructureToJSONResult(SMSBridgeJSONStructure jsonStructure) throws CommonException {
        try {
            //ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            StringBuilder jsonString = null;
            RequestPreparator result = new RequestPreparator();
            HashMap<String, Object> obj = convertSMSBridgeJSONStructureToHashMap(jsonStructure);
            jsonString = new StringBuilder(gsonObjectToJSONString(obj));
            result.setJsonString(jsonString);
            result.setSms((ArrayList<SMSBridge>) jsonStructure.getSms().clone());
            return result;
        } catch (Exception ex) {
            throw new CommonException("Exception occurred while transform from SMSJSONStructure To JSONRequest", ErrorCodes.GENERAL_ERROR);
        }
    }

    public static RequestPreparator transformSMSToSimpleRequestPreparator(SMSBridgeJSONStructure jsonStructure) throws CommonException {
        try {
            RequestPreparator result = new RequestPreparator();
            StringBuilder simpleRequestParameters = convertSMSBridgeJSONStructureToSimpleRequest(jsonStructure);
            result.setSimpleRequest(simpleRequestParameters.toString());
            result.setSms((ArrayList<SMSBridge>) jsonStructure.getSms().clone());
            return result;
        } catch (Exception ex) {
            throw new CommonException("Exception occurred while transform from SMSJSONStructure To SimpleRequestPreparator", ErrorCodes.GENERAL_ERROR);
        }
    }

    private static HashMap<String, Object> convertSMSBridgeJSONStructureToHashMap(SMSBridgeJSONStructure jsonStructure) {
        HashMap<String, Object> obj = new HashMap<>();
        ArrayList<LinkedTreeMap<String, LinkedTreeMap<String, String>>> smss = new ArrayList<>();
        LinkedTreeMap<String, LinkedTreeMap<String, String>> smsObj = null;
        LinkedTreeMap<String, String> smsProperties = null;
        String dateString = null;
        obj.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SERVICE_NAME, jsonStructure.getServiceName());
        for (SMSBridge sms : jsonStructure.getSms()) {

            smsObj = new LinkedTreeMap<>();
            smsProperties = new LinkedTreeMap<>();

            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.DESTINATION_MSISDN, sms.getDestinationMSISDN());
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.DO_NOT_APPLY, sms.getDoNotApply());
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.LANGUAGE, sms.getLanguageId());
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_PRORITY, sms.getMessagePriority());
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TEXT, encodeParameter(sms.getMsgText()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TYPE, sms.getMsgType() + "");
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_1, encodeParameter(sms.getOptionalParam1()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_2, encodeParameter(sms.getOptionalParam2()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_3, encodeParameter(sms.getOptionalParam3()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_4, encodeParameter(sms.getOptionalParam4()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_5, encodeParameter(sms.getOptionalParam5()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_MSISDN, encodeParameter(sms.getOriginatorMSISDN()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_TYPE, sms.getOriginatorType() + "");
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_NAME, encodeParameter(sms.getServiceName()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_IDS, encodeParameter(sms.getTemplateId()));
            smsProperties.put(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_PARAMETERS, encodeParameter(sms.getTemplateParameters()));
            SimpleDateFormat sdfr = new SimpleDateFormat(Defines.INTERFACES.SUBMISSION_DATE_FORMAT);
            dateString = sdfr.format(sms.getSubmissionDate());
            smsProperties.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SUBMISSION_DATE, dateString);
            // CR 1901 | eslam.ahmed
            smsProperties.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SERVICE_PASSWORD, encodeParameter(sms.getServicePassword()));
            smsObj.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMS, smsProperties);
            smss.add(smsObj);

        }
        obj.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SMSs, smss);

        return obj;
    }

    private static StringBuilder convertSMSBridgeJSONStructureToSimpleRequest(SMSBridgeJSONStructure jsonStructure) {
        StringBuilder simpleRequestParameters = new StringBuilder();

        if (jsonStructure.getSms().size() > 0) {
            SMSBridge sms = jsonStructure.getSms().get(0);
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_MSISDN, sms.getOriginatorMSISDN(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.DESTINATION_MSISDN, sms.getDestinationMSISDN(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TEXT, sms.getMsgText(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_TYPE, String.valueOf(sms.getOriginatorType()), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TYPE, String.valueOf(sms.getMsgType()), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.LANGUAGE, sms.getLanguageId(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_NAME, sms.getServiceName(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.DO_NOT_APPLY, sms.getDoNotApply(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_PRORITY, sms.getMessagePriority(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_1, sms.getOptionalParam1(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_2, sms.getOptionalParam2(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_3, sms.getOptionalParam3(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_4, sms.getOptionalParam4(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_5, sms.getOptionalParam5(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_IDS, sms.getTemplateId(), false));
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_PARAMETERS, sms.getTemplateParameters(), false));
            SimpleDateFormat sdfr = new SimpleDateFormat(Defines.INTERFACES.SUBMISSION_DATE_FORMAT);
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.SUBMISSION_DATE, sdfr.format(sms.getSubmissionDate()), false));
            // CR 1901 | eslam.ahmed
            simpleRequestParameters.append(prepareParamForHTTPSimpleRequest(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_PASSWORD, sms.getServicePassword(), true));
        }
        return simpleRequestParameters;
    }

    private static String prepareParamForHTTPSimpleRequest(String parameterName, String parameterValue, boolean lastParameter) {
        String output = "";
        if (parameterValue != null) {
            try {
                output = parameterName.concat("=").concat(URLEncoder.encode(parameterValue, "UTF-8"));
                if (!lastParameter) {
                    output = output.concat("&");
                }
            } catch (UnsupportedEncodingException ex) {
                CommonLogger.errorLogger.error("UnsupportedEncodingException" + ex, ex);
            }
        }
        return output;
    }

    private static String encodeParameter(String parameterValue) {
        String output = parameterValue;
        if (parameterValue != null) {
            try {
                output = URLEncoder.encode(parameterValue, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                CommonLogger.errorLogger.error("UnsupportedEncodingException" + ex, ex);
            }
        }
        return output;
    }

    public static String submitSendBulkSMSHTTP(RequestPreparator jsonStringContainer, int maxHTTPHitTimes, int connTimeOut, int readTimeOut, String url) throws CommonException {
        DataOutputStream out = null;
        PrintWriter pwOutput = null;
        BufferedReader bufReader = null;
        StringBuilder responseStr = new StringBuilder();
        HttpURLConnection conn = null;
        for (int httpHitsDone = 0; httpHitsDone < maxHTTPHitTimes; httpHitsDone++) {
            try {
                long t1 = System.currentTimeMillis();
                URL httpUrl = new URL(url);

                disableSslVerification(); // CR 1901 | eslam.ahmed | disable the SSL verfification

                conn = (HttpURLConnection) httpUrl.openConnection();
                conn.setRequestMethod(Defines.HTTP.POST);
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                // conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                //con.setRequestProperty("Content-Type", contentType);
                //  conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(connTimeOut);
                conn.setReadTimeout(readTimeOut);

                pwOutput = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));
                pwOutput.println(jsonStringContainer.getJsonString());
                pwOutput.close();
                conn.connect();
                bufReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = bufReader.readLine()) != null) {
                    responseStr.append(inputLine);
                }
                bufReader.close();
                CommonLogger.businessLogger.info("Http submission and response retrieval was done in [" + (System.currentTimeMillis() - t1) + "]");
                CommonLogger.businessLogger.info("Response [" + responseStr.toString() + "]");
                return responseStr.toString();
            } catch (SocketTimeoutException ex) {
                CommonLogger.businessLogger.error("HTTP submitter Thread Caught Exception--->" + ex);
                CommonLogger.errorLogger.error("HTTP submitter Thread Caught Exception--->" + ex, ex);
                if (ex.getMessage().contains("Read timed out")) {
                    CommonLogger.businessLogger.info("Read timed out exception caught common exception will be thrown and no retries will be handled");
                    throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.HTTP_TIMEOUT_CONNECTION_ERROR, ex.getMessage());
                }
            } catch (IOException ex) {
                CommonLogger.businessLogger.error("HTTP submitter Thread Caught Exception--->" + ex);
                CommonLogger.errorLogger.error("HTTP submitter Thread Caught Exception--->" + ex, ex);
            } catch (Exception ex) {
                CommonLogger.businessLogger.error("HTTP submitter Thread Caught Exception--->" + ex);
                CommonLogger.errorLogger.error("HTTP submitter Thread Caught Exception--->" + ex, ex);
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException ex) {
                    CommonLogger.businessLogger.error("HTTP submitter Thread Caught Exception--->" + ex);
                    CommonLogger.errorLogger.error("HTTP submitter Thread Caught Exception--->" + ex, ex);
                }
            }
        }

        throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.HTTP_CONNECTION_ERROR, "Error Connecting and Retrieving from url [" + url + "]");
    }

    public static String inputModelToJSONString(InputModel inputModel) throws JsonProcessingException {

        LinkedTreeMap smsMap = new LinkedTreeMap();
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.DESTINATION_MSISDN, inputModel.getDestinationMSISDN());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.DO_NOT_APPLY, inputModel.isDoNotApply() ? "true" : "false");
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.LANGUAGE, inputModel.getLanguage() + "");
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_PRORITY, inputModel.getMessagePriorityText());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TEXT, inputModel.getMessageText());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.MESSAGE_TYPE, inputModel.getMessageType() + "");
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_1, inputModel.getOptionalParam1());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_2, inputModel.getOptionalParam2());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_3, inputModel.getOptionalParam3());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_4, inputModel.getOptionalParam4());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.OPTIONAL_PARAM_5, inputModel.getOptionalParam5());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_MSISDN, inputModel.getOriginatorMSISDN());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.ORIGINATOR_TYPE, inputModel.getOriginatorType() + "");
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.SERVICE_NAME, inputModel.getSystemName());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_IDS, inputModel.getTemplatesIds());
        smsMap.put(Defines.INTERFACES.SEND_SMSs.INPUTS.TEMPLATES_PARAMETERS, inputModel.getTemplatesParameters());
//        SimpleDateFormat sdfr = new SimpleDateFormat(Defines.INTERFACES.SUBMISSION_DATE_FORMAT);
//        String dateString = sdfr.format(new Date());
//        smsMap.put(Defines.INTERFACES.SEND_SMS_BULK_OFFLINE.INPUTS.SUBMISSION_DATE, dateString);
        String gsonObjectToJSONString = gsonObjectToJSONString(smsMap);
        return gsonObjectToJSONString;
    }

    public static boolean checkOrignatorType(String originatorValue, Integer originatorType) {
        if (Defines.numaricPattern == null) {
            Defines.numaricPattern = Pattern.compile("\\+?[0-9]+");
        }
        Collection<OriginatorTypeModel> originatorTypes = SystemLookups.ORIGINATORS_LIST.values();
        for (OriginatorTypeModel originator : originatorTypes) {
            if (originatorType.intValue() == originator.getOriginatorType()) {
                if (originator.getAllowedLength() >= originatorValue.length()) {
                    if (originatorType == Defines.INTERFACES.ORIGINATOR_TYPE_ALPHANUMERIC) {
                        return true;
                    } else {
                        return Defines.numaricPattern.matcher(originatorValue).matches();
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static String sendRestRequest(String urlString, String USER_AGENT, String requestBody, String contentType, String urlParams, String requestMethod, int connTimeOut, int readTimeOut, org.apache.logging.log4j.Logger commonLogger) throws CommonException {

        try {
//            URL obj = new URL(url);
            URL url = null;

            if (urlParams != null && !urlParams.isEmpty()) {
                url = new URL(urlString + "?" + urlParams);
            } else {
                url = new URL(urlString);
            }

//            commonLogger.info("Sending " + requestMethod + " request to URL : " + url + ",Body:" + requestBody);
            commonLogger.trace("Sending " + requestMethod + " request to URL : " + url + ",Body:" + requestBody);
            disableSslVerification(); // CR 1901 | eslam.ahmed | disable the SSL verfification

            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            //add reuqest header
            con.setConnectTimeout(connTimeOut);
            con.setReadTimeout(readTimeOut);
            con.setRequestMethod(requestMethod);
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", contentType);

            // Send post request
            if (requestMethod.equalsIgnoreCase("POST")) {
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(requestBody.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            int responseCode = con.getResponseCode();
//            commonLogger.info("Sending " + requestMethod + " request to URL : " + url);
//            commonLogger.info("Request Body : " + requestBody);
            commonLogger.info("Sent " + requestMethod + " request to URL : " + url);
            commonLogger.trace("Request Body : " + requestBody);
            commonLogger.info("Response Code : " + responseCode);

            String inputLine;
            StringBuilder response = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == 500) {
                throw new CommonException(response.toString(), ErrorCodes.UNKOWN_ERROR);
            }

            //return result
            return response.toString();
        } catch (MalformedURLException ex) {
            commonLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } catch (IOException ex) {
            commonLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } catch (Exception ex) {
            commonLogger.error(ex.getMessage());
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
    }

    public static boolean checkInterface(ArrayList<Boolean> flags, ConcurrentHashMap<String, ExecutorService> executors,
            ArrayList<Integer> poolSizes, ConcurrentHashMap<String, Thread> threads, org.apache.logging.log4j.Logger commonLogger) {
        commonLogger.info("Attempting DataSource Check");
        try {
            new MainService().checkDatasource();
        } catch (CommonException ex) {
            CommonLogger.businessLogger.error("DataSource returned error: " + ex.getErrorMsg());
            return false;
        }
        commonLogger.info("DataSource is Active and Running");

        commonLogger.info("Attempting Flags Check");
        if (flags != null) {
            for (Boolean flag : flags) {
                if (flag) {
                    commonLogger.error("Flag is unexpectedly set to: " + flag);
                    return false;
                }
            }
            commonLogger.info("All flags are set correctly as expected");
        }
        commonLogger.info("Attempting ThreadPools Check");
        if (executors != null && executors.size() > 0) {
            synchronized (executors) {
                Iterator<Map.Entry<String, ExecutorService>> it = executors.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ExecutorService> entry = it.next();
                    ThreadPoolExecutor threadExecutor = (ThreadPoolExecutor) entry.getValue();
                    if (threadExecutor.getActiveCount() != threadExecutor.getPoolSize()) {
//                        commonLogger.error("Threadpool: " + entry.getKey() + " is not running with the expected count of threads: "
//                                + threadExecutor.getActiveCount() + " | Expected: " + threadExecutor.getPoolSize());
                        commonLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threadpool: " + entry.getKey() + " is not running with the expected count of threads")
                                .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, threadExecutor.getActiveCount())
                                .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, threadExecutor.getPoolSize()).build());;
                        return false;
                    }
//                    commonLogger.info("Threadpool: " + entry.getKey() + " is  running with the expected count of active  : "
//                            + threadExecutor.getActiveCount() + " | Expected: " + threadExecutor.getPoolSize());

                    commonLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Threadpool: " + entry.getKey() + " is running with the expected count of threads")
                            .put(GeneralConstants.StructuredLogKeys.ACTIVE_THREAD_COUNT, threadExecutor.getActiveCount())
                            .put(GeneralConstants.StructuredLogKeys.THREAD_POOL_SIZE, threadExecutor.getPoolSize()).build());;
                }
            }
            commonLogger.info("All threadpools are active as expected");
        }
        commonLogger.info("Attempting Threads Check");
        if (threads != null) {
            Iterator<Map.Entry<String, Thread>> it = threads.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Thread> entry = it.next();

                if (entry.getValue().getState() == Thread.State.NEW || entry.getValue().getState() == Thread.State.TERMINATED) {
//                    commonLogger.error("Thread: " + entry.getKey() + " states is not as expected: "
//                            + entry.getValue().getState() + " | Expected: " + Thread.State.RUNNABLE + " or " + Thread.State.TIMED_WAITING);

                    commonLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread: " + entry.getKey() + " states is not as expected")
                            .put(GeneralConstants.StructuredLogKeys.THREAD_STATE, entry.getValue().getState())
                            .put(GeneralConstants.StructuredLogKeys.EXPECTED_STATE, Thread.State.RUNNABLE + " or " + Thread.State.TIMED_WAITING).build());
                    return false;
                }
//                commonLogger.info("Thread: " + entry.getKey() + " states is  as expected: "
//                        + entry.getValue().getState() + " | Expected: " + Thread.State.RUNNABLE + " or " + Thread.State.TIMED_WAITING);

                commonLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Thread: " + entry.getKey() + " states is as expected")
                        .put(GeneralConstants.StructuredLogKeys.THREAD_STATE, entry.getValue().getState()).build());
            }

            commonLogger.info("All threads are active as expected");
        }
        return true;
    }

    public static boolean readyCheck(AtomicLong concurrentRequests, long maxConccurentRequests,
            ConcurrentHashMap<String, ArrayBlockingQueue> queues, ArrayList<Float> queuesMaxPercentage, Logger logger) {

        logger.info("Attempting Application Concurrent Requests Check");

        if (concurrentRequests.get() >= maxConccurentRequests) {

//            logger.error("Application Concurrent Requests exceeded the Max count | Current Count: "
//                    + concurrentRequests.get() + " | Max Expected: " + maxConccurentRequests);
            logger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Application Concurrent Requests exceeded the Max count")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, concurrentRequests.get())
                    .put(GeneralConstants.StructuredLogKeys.MAX_CONCURRENT_COUNT, maxConccurentRequests).build());
            return false;

        }

        logger.info("Application Concurrent Requests count is in the accepted range");

        logger.info("Attempting Queue Occupancy Check");
        float queueOccupancy = 0;
        for (Map.Entry<String, ArrayBlockingQueue> queue : queues.entrySet()) {
            queueOccupancy = (Float.valueOf(queue.getValue().size()) / (Float.valueOf(queue.getValue().size()) + Float.valueOf(queue.getValue().remainingCapacity()))) * 100;
            if (queueOccupancy >= Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE) {

//                logger.error("Queue: " + queue.getKey() + " | Occupancy Reached: " + queueOccupancy + " | Max Allowed: " + Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);
                logger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue occupancy reached")
                        .put(GeneralConstants.StructuredLogKeys.QUEUE_OCCUPANCY, queueOccupancy)
                        .put(GeneralConstants.StructuredLogKeys.MAX_ALLOWED, Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE).build());
                return false;
            }
//            logger.info("Queue: " + queue.getKey() + " | Occupancy Safe: " + queueOccupancy + " | Max Allowed: " + Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE);
            logger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Queue occupancy is safe")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_OCCUPANCY, queueOccupancy)
                    .put(GeneralConstants.StructuredLogKeys.MAX_ALLOWED, Defines.INTERFACES.MAX_QUEUE_OCCUPANCY_RATE_VALUE).build());
        }
        logger.info("All Queues are occupancy safe");

        return true;
    }

//    public static String generateThreadDump(long date) {
//        final StringBuilder dump = new StringBuilder();
//        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//        final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
//        for (ThreadInfo threadInfo : threadInfos) {
//            dump.append(date + '"');
//            dump.append(threadInfo.getThreadName());
//            dump.append("\" ");
//            final Thread.State state = threadInfo.getThreadState();
//            dump.append("\n   java.lang.Thread.State: ");
//            dump.append(state);
//            final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
//            for (final StackTraceElement stackTraceElement : stackTraceElements) {
//                dump.append("\n        at ");
//                dump.append(stackTraceElement);
//            }
//            dump.append("\n\n");
//        }
//        return dump.toString();
//    }
    public static ArrayList<com.asset.contactstrategy.common.models.ThreadInfo> generateThreadDumpNew(long date) {
        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        final ThreadInfo[] threadInfoList = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);

        ArrayList<com.asset.contactstrategy.common.models.ThreadInfo> threadInfoArrayList = new ArrayList<>();

        for (ThreadInfo threadInfo : threadInfoList) {
            com.asset.contactstrategy.common.models.ThreadInfo threadInfos = new com.asset.contactstrategy.common.models.ThreadInfo(threadInfo);

            threadInfoArrayList.add(threadInfos);

        }
        return threadInfoArrayList;
    }

    public static String sendRestRequestWithRetries(int maxHTTPHitTimes, String urlString, String USER_AGENT, String requestBody, String contentType, String urlParams, String requestMethod, int connTimeOut, int readTimeOut, org.apache.logging.log4j.Logger commonLogger) throws CommonException {

//            URL obj = new URL(url);
        for (int httpHitsDone = 0; httpHitsDone < maxHTTPHitTimes; httpHitsDone++) {
            try {
                long time = System.currentTimeMillis();
                URL url = null;
                if (urlParams != null && !urlParams.isEmpty()) {
                    url = new URL(urlString + "?" + urlParams);
                } else {
                    url = new URL(urlString);
                }

                commonLogger.info("Sending " + requestMethod + " request to URL : " + url + ",Body:" + requestBody + " ,retries:" + httpHitsDone);

                disableSslVerification(); // CR 1901 | eslam.ahmed | disable the SSL verfification

                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                //add reuqest header
                con.setConnectTimeout(connTimeOut);
                con.setReadTimeout(readTimeOut);
                con.setRequestMethod(requestMethod);
                if (USER_AGENT != null) {
                    con.setRequestProperty("User-Agent", USER_AGENT);
                }
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                con.setRequestProperty("Content-Type", contentType);

                // Send post request
                if (requestMethod.equalsIgnoreCase("POST")) {
                    con.setDoOutput(true);
                    OutputStream os = con.getOutputStream();
                    os.write(requestBody.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                }

                int responseCode = con.getResponseCode();
                commonLogger.info("Sending " + requestMethod + " request to URL : " + url);
                commonLogger.info("Request Body : " + requestBody);

                String inputLine;
                StringBuilder response = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (responseCode == 500) {
                    throw new CommonException(response.toString(), ErrorCodes.UNKOWN_ERROR);
                }

                //return result
                commonLogger.info("Response Code : " + responseCode + " and request ended in " + (System.currentTimeMillis() - time) + " msecs");
                return response.toString();
            } catch (SocketTimeoutException ex) {
                commonLogger.error(ex.getMessage());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
                if (ex.getMessage().contains("Read timed out")) {
                    commonLogger.info("Read timed out exception caught common exception will be thrown and no retries will be handled");
                    throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.HTTP_TIMEOUT_CONNECTION_ERROR, ex.getMessage());
                }
            } catch (MalformedURLException ex) {
                commonLogger.error(ex.getMessage());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
                //throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            } catch (IOException ex) {
                commonLogger.error(ex.getMessage());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
                //throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            } catch (CommonException ex) {
                commonLogger.error(ex.getMessage());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
                //throw ex;
            } catch (Exception ex) {
                commonLogger.error(ex.getMessage());
                CommonLogger.errorLogger.error(ex.getMessage(), ex);
                //throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        }

        //throw new CommonException("Error Connecting and Retrieving from url [" + urlString + "]", ErrorCodes.GET_CONNECTION_ERROR);
        throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.HTTP_CONNECTION_ERROR, "Error Connecting and Retrieving from url [" + urlString + "]");
    }

    public static StringBuilder convertSMSBridgeListToString(ArrayList<SMSBridge> smsBridgeList) {

        StringBuilder output = new StringBuilder("");
        if (smsBridgeList != null && smsBridgeList.size() > 0) {
            for (int i = 0; i < smsBridgeList.size(); i++) {
                output.append(smsBridgeList.get(i).toString()).append(",");
            }

        }
        return output;
    }

    public static int getMsisdnModX(String MSISDN) {
        return Integer.parseInt(MSISDN.substring(2)) % Defines.MOD_X;
    }

    // CR 1901 | eslam.ahmed
    public static byte[] getMacSha512Hash(String text, String key) throws CommonException {
        Mac sha512_HMAC = null;
        final String HMAC_SHA512 = "HmacSHA512";
        final String ENCODING = "UTF-8";
        byte[] hashedPassword = null;
        try {

            byte[] byteKey = key.getBytes(ENCODING);
            sha512_HMAC = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512_HMAC.init(keySpec);
            hashedPassword = sha512_HMAC.doFinal(text.getBytes(ENCODING));

        } catch (UnsupportedEncodingException ex) {
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException("UnsupportedEndcodeingException | Encoding: " + ENCODING, ex.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException("NoSuchAlgorithmException | Algorithm: " + HMAC_SHA512, ex.getMessage());
        } catch (InvalidKeyException ex) {
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
            throw new CommonException("InvalidKeyException", ex.getMessage());
        }
        return hashedPassword;
    }

    // CR 1901 | eslam.ahmed
    public static boolean validateServicePassword(ServicesModel service, String inputPassword, String hashKey) throws CommonException {

        byte[] hashedInputPassword = getMacSha512Hash(inputPassword, hashKey);
        byte[] hashedServicePassword = service.getHashedPassword();
        if (!Arrays.equals(hashedServicePassword, hashedInputPassword)) {
            return false;
        }
        CommonLogger.businessLogger.info("Valid Service Password");
        return true;
    }

    public static boolean validateServicePassword(byte[] hashedServicePassword, String inputPassword, String hashKey) throws CommonException {

        byte[] hashedInputPassword = getMacSha512Hash(inputPassword, hashKey);
        if (!Arrays.equals(hashedServicePassword, hashedInputPassword)) {
            return false;
        }
        CommonLogger.businessLogger.info("Valid Service Password");
        return true;
    }

    // CR 1901 | eslam.ahmed
    private static void disableSslVerification() throws KeyManagementException, NoSuchAlgorithmException {

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    }

    public static byte[] convertSmsToJsonBytes(EnqueueModelREST sms) throws CommonException {
        //ObjectMapper objectMapper = new ObjectMapper();
        byte[] jsonMessage;
        String jsonStr;
        try {
            //jsonMessage = objectMapper.writeValueAsBytes(sms);
            Gson gson = new Gson();
            jsonStr = gson.toJson(sms);
            jsonMessage = jsonStr.getBytes("UTF-8");
        } catch (Exception ex) {
//            CommonLogger.debugLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex);
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        } catch (Throwable ex) {
//            CommonLogger.debugLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex);
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        }
//        catch (JsonProcessingException ex) {
//            CommonLogger.debugLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex);
//            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
//            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
//        }
        return jsonMessage;
    }


    public static byte[] convertEnqueueModelDeliverSMToJsonBytes(EnqueueModelDeliverSM enqueueModel) throws CommonException {
        byte[] jsonMessage;
        String jsonStr;
        try {
            Gson gson = new Gson();
            jsonStr = gson.toJson(enqueueModel);
            jsonMessage = jsonStr.getBytes();
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        } catch (Throwable ex) {
            CommonLogger.errorLogger.error("Exception---->  for [enqueueBatch] Failed To send message batch " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        }
        return jsonMessage;
    }

    public static SMS convertJsonBytesToSMS(byte[] jsonSms) throws CommonException {

        SMS sms = null;
        try {
            String msg = new String(jsonSms, "UTF-8");
            Gson gson = new Gson();
            EnqueueModelREST enqModelRest = gson.fromJson(msg, EnqueueModelREST.class);
            sms = mapEnqueueModelRestToSMS(enqModelRest);
        } catch (Exception ex) {
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Exception---->  for [convertJSONToSMS] Failed To JSON string into SMS model " + ex).build());
            CommonLogger.errorLogger.error("Exception---->  for [convertJSONToSMS] Failed To JSON string into SMS model " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        } catch (Throwable ex) {
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                    "Exception---->  for [convertJSONToSMS] Failed To JSON string into SMS model " + ex).build());
            CommonLogger.errorLogger.error("Exception---->  for [convertJSONToSMS] Failed To JSON string into SMS model " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        }

        return sms;
    }

    public static SMS mapEnqueueModelRestToSMS(EnqueueModelREST enqModelRest) throws SQLException {
        Timestamp requestTime = new Timestamp(System.currentTimeMillis());
        BigDecimal seq_id = BigDecimal.valueOf(enqModelRest.getCsMsgId());
        String appName = String.valueOf(enqModelRest.getQueueAppName());
        String origMsisdn = enqModelRest.getOriginatorMSISDN();
        String dstMsisdn = enqModelRest.getDestinationMSISDN();
        String msgTxt = enqModelRest.getMessageText();
        BigDecimal msgType = BigDecimal.valueOf(enqModelRest.getMessageType());
        BigDecimal origType = BigDecimal.valueOf(enqModelRest.getOriginatorType());
        BigDecimal langId = BigDecimal.valueOf(enqModelRest.getLanguage());
        BigDecimal nbtrials = BigDecimal.ZERO;
        BigDecimal concMsgSequeunce = BigDecimal.ZERO;
        BigDecimal concMsgCount = BigDecimal.valueOf(enqModelRest.getConcMsgCount());
        BigDecimal concSarRefNum = BigDecimal.ZERO;
        String ipAddress = enqModelRest.getIpAddress();
        BigDecimal deliveryRequest = BigDecimal.valueOf(enqModelRest.getDeliveryReport());
        String optionalParameter1 = enqModelRest.getOptionalParam1();
        String optionalParameter2 = enqModelRest.getOptionalParam2();
        String optionalParameter3 = enqModelRest.getOptionalParam3();
        String optionalParameter4 = enqModelRest.getOptionalParam4();
        String optionalParameter5 = enqModelRest.getOptionalParam5();
        Timestamp submissionDate =  enqModelRest.getSubmissionDate() == null ? requestTime : new Timestamp(enqModelRest.getSubmissionDate().getTime());
        BigDecimal expirationHours = BigDecimal.valueOf(enqModelRest.getExpirationHours());
        String tlvOptionalParams = Utility.gsonObjectToJSONString(enqModelRest.getTlvs());
        String inRequestId = enqModelRest.getRequestId();
        String serviceType = enqModelRest.getServiceType();
        BigDecimal esmClass = enqModelRest.getEsmClass() == null ? null : BigDecimal.valueOf(enqModelRest.getEsmClass());
        BigDecimal protocolId = enqModelRest.getProtocolId() == null ? null : BigDecimal.valueOf(enqModelRest.getProtocolId());
        BigDecimal priorityFlag = enqModelRest.getPriorityFlag() == null ? null : BigDecimal.valueOf(enqModelRest.getPriorityFlag());
        String scheduleDeliveryTime = enqModelRest.getScheduleDeliveryTime();
        String validityPeriod = enqModelRest.getValidityPeriod();
        BigDecimal smDefaultMsgId = enqModelRest.getSmDefaultMsgId() == null ? null : BigDecimal.valueOf(enqModelRest.getSmDefaultMsgId());

        SMS sms = new SMS(seq_id, appName, origMsisdn, dstMsisdn, msgTxt, msgType,
                origType, langId, nbtrials, concMsgSequeunce, concMsgCount,
                concSarRefNum, ipAddress, deliveryRequest, optionalParameter1,
                optionalParameter2, optionalParameter3, optionalParameter4,
                optionalParameter5, submissionDate, expirationHours, tlvOptionalParams,
                inRequestId, serviceType, esmClass, protocolId, priorityFlag,
                scheduleDeliveryTime, validityPeriod, smDefaultMsgId);

        return sms;
    }

    public static EnqueueModelDeliverSM convertJsonBytesToEnqueueModelDeliverSM(byte[] jsonSms) throws CommonException {

        EnqueueModelDeliverSM enqModel = null;
        try {
            String msg = new String(jsonSms, "UTF-8");
            Gson gson = new Gson();
            enqModel = gson.fromJson(msg, EnqueueModelDeliverSM.class);

        } catch (JsonSyntaxException | UnsupportedEncodingException ex) {
            CommonLogger.errorLogger.error("Exception---->  for [convertJSONToEnqueueModelDataSM] Failed To JSON string into EnqueueModelDataSM model " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        } catch (Throwable ex) {
            CommonLogger.errorLogger.error("Exception---->  for [convertJSONToEnqueueModelDataSM] Failed To JSON string into EnqueueModelDataSM model " + ex, ex);
            throw new CommonException(GeneralConstants.SRC_ID_GLOABL_SETTINGS, ErrorCodes.RABBITMQ.ENQUEUE_ERROR, ex.getMessage());
        }

        return enqModel;
    }

    public static EnqueueModelREST mapSMSToEnqueueModelRest(SMS sms) throws SQLException {
        EnqueueModelREST enqModelRest = new EnqueueModelREST();

        enqModelRest.setCsMsgId(sms.getSeqId().longValue());
        enqModelRest.setQueueAppName(sms.getAppId());
        enqModelRest.setOriginatorMSISDN(sms.getOrigMsisdn());
        enqModelRest.setDestinationMSISDN(sms.getDstMsisdn());
        enqModelRest.setMessageText(sms.getMsgTxt());
        enqModelRest.setMessageType(sms.getMsgType().intValue());
        enqModelRest.setOriginatorType(sms.getOrigType().intValue());
        enqModelRest.setLanguage(sms.getLangId().intValue());
        enqModelRest.setConcMsgCount(sms.getConcMsgCount().intValue());
        enqModelRest.setIpAddress(sms.getIPAddress());
        enqModelRest.setDeliveryReport(sms.getReceiptRequested().intValue());
        enqModelRest.setOptionalParam1(sms.getOptionalParameter1());
        enqModelRest.setOptionalParam2(sms.getOptionalParameter2());
        enqModelRest.setOptionalParam3(sms.getOptionalParameter3());
        enqModelRest.setOptionalParam4(sms.getOptionalParameter4());
        enqModelRest.setOptionalParam5(sms.getOptionalParameter5());
        enqModelRest.setSubmissionDate(sms.getSubmissionDate());
        enqModelRest.setExpirationHours(sms.getExpirationHours().intValue());
        enqModelRest.setTlvs(Utility.gsonTlvsJSONStringToList(sms.getTlvOptionalParams()));
        enqModelRest.setRequestId(sms.getRequestId());
        enqModelRest.setServiceType(sms.getServiceType());
        enqModelRest.setEsmClass(sms.getEsmClass().byteValue());
        enqModelRest.setProtocolId(sms.getProtocolId().byteValue());
        enqModelRest.setPriorityFlag(sms.getPriorityFlag().byteValue());
        enqModelRest.setScheduleDeliveryTime(sms.getScheduleDeliveryTime());
        enqModelRest.setValidityPeriod(sms.getValidityPeriod());
        enqModelRest.setSmDefaultMsgId(sms.getsmDefaultMsgId().byteValue());

        return enqModelRest;
    }
}
