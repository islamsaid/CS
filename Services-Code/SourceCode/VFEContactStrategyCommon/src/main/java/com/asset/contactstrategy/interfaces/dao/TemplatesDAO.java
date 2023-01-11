/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.dao;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.interfaces.models.TemplateModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author esmail.anbar
 */
public class TemplatesDAO {
    public HashMap<Integer, TemplateModel> getAllTemplates(Connection connection) throws CommonException 
    {
        PreparedStatement statement = null;
        StringBuilder sql = new StringBuilder();
        ResultSet resultSet = null;
        TemplateModel templateModel = null;
        HashMap<Integer, TemplateModel> templates = new HashMap<>();
        try 
        {
            sql.append("SELECT * FROM ")
               .append(DBStruct.VFE_CS_TEMPLATES_LK.TABLE_NAME);
//               .append(" Inner Join ")
//               .append(DBStruct.VFE_CS_SERVICES_TEMPLATES.TABLE_NAME)
//               .append(" ON ")
//               .append(DBStruct.VFE_CS_TEMPLATES_LK.TABLE_NAME)
//               .append(".")
//               .append(DBStruct.VFE_CS_TEMPLATES_LK.TEMPLATE_ID)
//               .append("=")
//               .append(DBStruct.VFE_CS_SERVICES_TEMPLATES.TABLE_NAME)
//               .append(".")
//               .append(DBStruct.VFE_CS_SERVICES_TEMPLATES.TEMPLATE_ID);
            
            statement = connection.prepareStatement(sql.toString());
            resultSet = statement.executeQuery();
            
            while (resultSet.next())
            {
                templateModel = new TemplateModel();
                templateModel.setTemplateId(resultSet.getInt(DBStruct.VFE_CS_TEMPLATES_LK.TEMPLATE_ID));
                templateModel.setTemplateDescription(resultSet.getString(DBStruct.VFE_CS_TEMPLATES_LK.TEMPLATE_DECRIPTION));
                templateModel.setArabicScript(resultSet.getString(DBStruct.VFE_CS_TEMPLATES_LK.ARABIC_SCRIPT));
                templateModel.setEnglishScript(resultSet.getString(DBStruct.VFE_CS_TEMPLATES_LK.ENGLISH_SCRIPT));
                templateModel.setExpirationDuration(resultSet.getInt(DBStruct.VFE_CS_TEMPLATES_LK.EXPIRATION_DURATION));
                
                templates.put(templateModel.getTemplateId(), templateModel);
            }
            return templates;
        }
        catch (SQLException e) 
        {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.ERROR_SQL);
        }
        catch (Exception e) 
        {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
        }
        finally 
        {
            try 
            {
                if (resultSet != null) 
                {
                    resultSet.close();
                }
            } 
            catch (SQLException e) 
            {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
            try 
            {
                if (statement != null) 
                {
                    statement.close();
                }
            } 
            catch (SQLException e) 
            {
                CommonLogger.businessLogger.error(e.getMessage());
                CommonLogger.errorLogger.error(e.getMessage(), e);
            }
        }
    }
}
