/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.dao;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.utils.Utility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author rania.magdy
 */
public class CommonDAO {

    public static int getNextId(Connection con, String strSeqName) throws CommonException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            String strSqStmt = "Select " + strSeqName + ".NEXTVAL" + " from dual";
            pstmt = con.prepareStatement(strSqStmt);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }
        } catch (SQLException sqle) {
            throw new CommonException(sqle.getMessage(), sqle.getErrorCode());
        }catch (Exception ex) {
            CommonLogger.errorLogger.error(CampaignDAO.class.getName() + " || " + "Getting Caught Exception---->  for [getNextId]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            throw new CommonException(e.getMessage(), e.getErrorCode());
            }
        }
    }
    
    public Timestamp getMidNightDatabaseTime(Connection conn) throws CommonException{
        CommonLogger.businessLogger.info(" getMidNightDatabaseTime() Invoked...");
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        StringBuilder SQLquery = new StringBuilder();
        Timestamp value=null;
        try{
            SQLquery.append("SELECT TRUNC(sysdate + 1) - INTERVAL '2' MINUTE FROM DUAL");
            preStmt = conn.prepareStatement(SQLquery.toString());

            resultSet = preStmt.executeQuery();
            while(resultSet.next())
            {
                value= resultSet.getTimestamp(1);
            }
        }catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getMidNightDatabaseTime] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR); 
        } catch (Exception e) {
       CommonLogger.errorLogger.error("Exception---->  for [getMidNightDatabaseTime] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(resultSet, preStmt);
        }
        return value;
    }
    
    public Timestamp getCurrentDatabaseTime(Connection conn) throws CommonException
    {
        
        CommonLogger.businessLogger.info(" getCurrentDatabaseTime() Invoked...");
        PreparedStatement preStmt = null;
        ResultSet resultSet = null;
        StringBuilder SQLquery = new StringBuilder();
        Timestamp value=null;
        try{
            SQLquery.append("Select sysdate From DUAL");
            preStmt = conn.prepareStatement(SQLquery.toString());

            resultSet = preStmt.executeQuery();
            while(resultSet.next())
            {
                value= resultSet.getTimestamp(1);
            }
        }catch (SQLException e) {
            CommonLogger.errorLogger.error("Exception---->  for [getCurrentDatabaseTime] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
        } catch (Exception e) {
       CommonLogger.errorLogger.error("Exception---->  for [getCurrentDatabaseTime] " + e, e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            DataSourceManger.closeDBResources(resultSet, preStmt);
        }
        return value;
        
    }      
}
