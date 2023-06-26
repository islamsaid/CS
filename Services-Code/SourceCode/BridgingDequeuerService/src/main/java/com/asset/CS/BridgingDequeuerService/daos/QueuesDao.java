/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.daos;

import com.asset.contactstrategy.common.models.SMSBridge;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.QueueModel;
import java.math.BigDecimal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import oracle.sql.ARRAY;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Component;

/**
 *
 * @author mostafa.kashif
 */
@Component
public class QueuesDao {

    // ArrayList<SMSBridge>
    public ArrayList<SMSBridge> getSMSs(JdbcTemplate jdbcTemplate, String queueName, int batchSize, int waitTime, long threadNumber) throws CommonException {
//        CommonLogger.businessLogger.info("Starting calling procedures with values queueName:" + queueName + ",batchSize:" + batchSize + ",waitTime:" + waitTime + ",threadNumber:" + threadNumber);
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting calling procedure")
                .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName)
                .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, batchSize)
                .put(GeneralConstants.StructuredLogKeys.WAIT_TIME, waitTime)
                .put(GeneralConstants.StructuredLogKeys.THREAD_NUMBER, threadNumber).build());
        StringBuilder batchId = new StringBuilder(queueName);
        ArrayList<SMSBridge> smsObjectList = null;
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(Defines.SMS_BRIDGING_ENGINE.DEQ_BATCH_PROCEDURE)
                .declareParameters(new SqlParameter("APP_QUEUE_NAME", Types.VARCHAR), new SqlParameter("waittime", Types.NUMERIC), new SqlParameter("array_size", Types.NUMERIC),
                        new SqlOutParameter("outputmessage", Types.ARRAY, "SMS_BATCH"), new SqlOutParameter("priority_arr", Types.ARRAY, "PRIORITY_ARRAY"), new SqlOutParameter("enqtime_arr", Types.ARRAY, "ENQTIME_ARRAY"));

        MapSqlParameterSource values = new MapSqlParameterSource();
        values.addValue("APP_QUEUE_NAME", queueName);
        values.addValue("waittime", waitTime);
        values.addValue("array_size", batchSize);

        Map<String, Object> execute = call.execute(values);
        smsObjectList = new ArrayList<SMSBridge>();

        if (execute != null && execute.get("outputmessage") != null) {
            batchId.append("_" + threadNumber);
            batchId.append("_" + String.valueOf(System.currentTimeMillis()));
            try {
                Object[] objs_SMS = (Object[]) ((ARRAY) execute.get("outputmessage")).getArray();
                Object[] objs_EnqTime = (Object[]) ((ARRAY) execute.get("enqtime_arr")).getArray();
                Object[] objs_Priority = (Object[]) ((ARRAY) execute.get("priority_arr")).getArray();

                for (int i = 0; i < objs_SMS.length; i++) {
                    if (objs_SMS[i] != null) {

                        Struct struct = (Struct) objs_SMS[i];
                        Object[] attrs = struct.getAttributes();
                        SMSBridge smsObj = new SMSBridge(
                                ((BigDecimal) attrs[0]),
                                ((attrs.length >= 2 && attrs[1] != null) ? attrs[1].toString() : null),
                                ((attrs.length >= 3 && attrs[2] != null) ? attrs[2].toString() : null),
                                ((attrs.length >= 4 && attrs[3] != null) ? attrs[3].toString() : null),
                                ((attrs.length >= 5 && attrs[4] != null) ? attrs[4].toString() : null),
                                ((attrs.length >= 6 && attrs[5] != null) ? attrs[5].toString() : null),
                                ((BigDecimal) attrs[6]), ((BigDecimal) attrs[7]),
                                ((attrs.length >= 9 && attrs[8] != null) ? String.valueOf(attrs[8]) : null),
                                ((attrs.length >= 10 && attrs[9] != null) ? attrs[9].toString() : null),
                                ((attrs.length >= 11 && attrs[10] != null) ? attrs[10].toString() : null),
                                ((attrs.length >= 12 && attrs[11] != null) ? attrs[11].toString() : null),
                                ((attrs.length >= 13 && attrs[12] != null) ? attrs[12].toString() : null),
                                ((attrs.length >= 14 && attrs[13] != null) ? attrs[13].toString() : null),
                                ((attrs.length >= 15 && attrs[14] != null) ? attrs[14].toString() : null),
                                ((attrs.length >= 16 && attrs[15] != null) ? ((String) attrs[15]) : null),
                                ((attrs.length >= 17 && attrs[16] != null) ? ((String) attrs[16]) : null),
                                ((attrs.length >= 18 && attrs[17] != null) ? ((String) attrs[17]) : null),
                                ((attrs.length == 19 && attrs[18] != null) ? ((String) attrs[18]) : null),
                                ((attrs.length == 20 && attrs[19] != null) ? ((Timestamp) attrs[19]) : null));
                        smsObj.setMsgId(((attrs.length >= 1 && attrs[0] != null) ? Long.parseLong(attrs[0].toString()) : null));
                        smsObj.setQueueName(((attrs.length >= 2 && attrs[1] != null) ? attrs[1].toString() : null));
                        smsObj.setServiceName(((attrs.length >= 3 && attrs[2] != null) ? attrs[2].toString() : null));
                        smsObj.setOriginatorMSISDN(((attrs.length >= 4 && attrs[3] != null) ? attrs[3].toString() : null));
                        smsObj.setDestinationMSISDN(((attrs.length >= 5 && attrs[4] != null) ? attrs[4].toString() : null));
                        smsObj.setMsgText(((attrs.length >= 6 && attrs[5] != null) ? attrs[5].toString() : null));
                        smsObj.setMsgType(((attrs.length >= 7 && attrs[6] != null) ? Integer.parseInt(attrs[6].toString()) : null));
                        smsObj.setOriginatorType(((attrs.length >= 8 && attrs[7] != null) ? Integer.parseInt(attrs[7].toString()) : null));
                        smsObj.setLanguageId(((attrs.length >= 9 && attrs[8] != null) ? String.valueOf(attrs[8]) : null));
                        smsObj.setIpAddress(((attrs.length >= 10 && attrs[9] != null) ? attrs[9].toString() : null));
                        smsObj.setDoNotApply(((attrs.length >= 11 && attrs[10] != null) ? attrs[10].toString() : null));
                        smsObj.setMessagePriority(((attrs.length >= 12 && attrs[11] != null) ? attrs[11].toString() : null));
                        smsObj.setTemplateId(((attrs.length >= 13 && attrs[12] != null) ? attrs[12].toString() : null));
                        smsObj.setTemplateParameters(((attrs.length >= 14 && attrs[13] != null) ? attrs[13].toString() : null));
                        smsObj.setOptionalParam1(((attrs.length >= 15 && attrs[14] != null) ? attrs[14].toString() : null));
                        smsObj.setOptionalParam2(((attrs.length >= 16 && attrs[15] != null) ? ((String) attrs[15]) : null));
                        smsObj.setOptionalParam3(((attrs.length >= 17 && attrs[16] != null) ? ((String) attrs[16]) : null));
                        smsObj.setOptionalParam4(((attrs.length >= 18 && attrs[17] != null) ? ((String) attrs[17]) : null));
                        smsObj.setOptionalParam5(((attrs.length >= 19 && attrs[18] != null) ? ((String) attrs[18]) : null));
                        smsObj.setSubmissionDate(((attrs.length >= 20 && attrs[19] != null) ? new java.util.Date(((Timestamp) attrs[19]).getTime()) : null));
                        // CR 1901 | eslam.ahmed
                        smsObj.setServicePassword(((attrs.length == 21 && attrs[20] != null) ? ((String) attrs[20]) : null));
                        String setMsgText= new String(smsObj.getMsgText().getBytes("Cp1252"),"Cp1256");
                        smsObj.setMsgText(setMsgText);
                        smsObjectList.add(smsObj);

                    }
                }
            } catch (SQLException e) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", e);
                throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
            } catch (Exception e) {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.DATABASE_ERROR + "---- > [getHashedApplicationQueues]", e);
                throw new CommonException(e.getMessage(), ErrorCodes.DATABASE_ERROR);
            }

        }
        return smsObjectList;
    }

    public List<QueueModel> getProceduresQueues(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query("select APP_NAME,SCHEMA_NAME,SCHEMA_PASSWORD,DATABASE_URL from VFE_CS_APP_QUEUES where queue_type=2", new BeanPropertyRowMapper(QueueModel.class));
    }

}
