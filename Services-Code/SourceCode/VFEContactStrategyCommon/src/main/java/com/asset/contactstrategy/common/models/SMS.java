package com.asset.contactstrategy.common.models;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.jdbc.OracleTypes;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.Datum;
import oracle.sql.STRUCT;
import oracle.jpub.runtime.MutableStruct;
import sqlj.runtime.ref.DefaultContext;
import sqlj.runtime.ConnectionContext;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SMS implements ORAData, ORADataFactory {

    public static final String _SQL_NAME = "SMS";
    public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

    /* connection management */
    protected DefaultContext __tx = null;
    protected Connection __onn = null;

    // Kerollos Asaad
    //private boolean receiptRequested;
    private String smsc_msg_id; // used for adding smsc mssg id in databse
    private int smsc_seq_num; // used for deliver_sm_resp thread to send seq number in the header.
    private int smsc_id; // used for tracking the id of contacted smsc to send message.
    private int smsc_sms_status = -1;
    private int smsc_msg_count;
    private String smsc_msg_id_trimmed; // used for adding trimmed smsc mssg id in databse

    public int getSmsc_msg_count() {
        return smsc_msg_count;
    }

    public void setSmsc_msg_count(int smsc_msg_count) {
        this.smsc_msg_count = smsc_msg_count;
    }

    public int getSmsc_sms_status() {
        return smsc_sms_status;
    }

    public void setSmsc_sms_status(int smsc_sms_status) {
        this.smsc_sms_status = smsc_sms_status;
    }

    public int getSmsc_id() {
        return smsc_id;
    }

    public void setSmsc_id(int sms_id) {
        this.smsc_id = sms_id;
    }

    public String getSmsc_msg_id() {
        return smsc_msg_id;
    }

    public void setSmsc_msg_id(String smsc_msg_id) {
        this.smsc_msg_id = smsc_msg_id;
    }

    public String getSmsc_msg_id_trimmed() {
        return smsc_msg_id_trimmed;
    }

    public void setSmsc_msg_id_trimmed(String smsc_msg_id_trimmed) {
        this.smsc_msg_id_trimmed = smsc_msg_id_trimmed;
    }

    public int getSmsc_seq_num() {
        return smsc_seq_num;
    }

    public void setSmsc_seq_num(int smsc_seq_num) {
        this.smsc_seq_num = smsc_seq_num;
    }

//    public boolean getReceiptRequested() {
//        return receiptRequested;
//    }
//
//    public void setReceiptRequested(boolean receiptRequested) {
//        this.receiptRequested = receiptRequested;
//    }
    public void setConnectionContext(DefaultContext ctx) throws SQLException {
        release();
        __tx = ctx;
    }

    public DefaultContext getConnectionContext() throws SQLException {
        if (__tx == null) {
            __tx = (__onn == null) ? DefaultContext.getDefaultContext() : new DefaultContext(__onn);
        }
        return __tx;
    }

    ;

    public Connection getConnection() throws SQLException {
        return (__onn == null) ? ((__tx == null) ? null : __tx.getConnection()) : __onn;
    }

    public void release() throws SQLException {
        if (__tx != null && __onn != null) {
            __tx.close(ConnectionContext.KEEP_CONNECTION);
        }
        __onn = null;
        __tx = null;
    }
    protected MutableStruct _struct;
    //private static int[] _sqlType = {12, 12, 12, 12, 2, 2, 2, 2, 2, 2, 2, 2004};
    //private static ORADataFactory[] _factory = new ORADataFactory[12];
    //private static int[] _sqlType = {12, 12, 12, 12, 2, 2, 2, 2, 2, 2, 2};
    //private static int[] _sqlType = {12, 12, 12, 12, 2, 2, 2, 2, 2, 2, 2, 12, 12};
    //private static ORADataFactory[] _factory = new ORADataFactory[13];
    private static int[] _sqlType = {2, 12, 12, 12, 12, 2, 2, 2, 2, 2, 2, 2, 12, 2, 12, 12, 12, 12, 12, 93, 2, 12, 12, 12, 2, 2, 2, 12, 12, 2};
    private static ORADataFactory[] _factory = new ORADataFactory[30];
    protected static final SMS _SMSFactory = new SMS(false);

    public static ORADataFactory getORADataFactory() {
        return _SMSFactory;
    }
    /* constructors */

    protected SMS(boolean init) {
        if (init) {
            //_struct = new MutableStruct(new Object[12], _sqlType, _factory);
            //_struct = new MutableStruct(new Object[13], _sqlType, _factory);
            //_struct = new MutableStruct(new Object[19], _sqlType, _factory);
            _struct = new MutableStruct(new Object[30], _sqlType, _factory);
        }
    }

    public SMS() {
        this(true);
        __tx = DefaultContext.getDefaultContext();
    }

    public SMS(DefaultContext c) /*throws SQLException*/ {
        this(true);
        __tx = c;
    }

    public SMS(Connection c) /*throws SQLException*/ {
        this(true);
        __onn = c;
    }

    public SMS(java.math.BigDecimal seq_id,
            String appName,
            String origMsisdn,
            String dstMsisdn,
            String msgTxt,
            java.math.BigDecimal msgType,
            java.math.BigDecimal origType,
            java.math.BigDecimal langId,
            java.math.BigDecimal nbtrials,
            java.math.BigDecimal concMsgSequeunce,
            java.math.BigDecimal concMsgCount,
            java.math.BigDecimal concSarRefNum,
            String ipAddress,
            java.math.BigDecimal deliveryRequest,
            String optionalParameter1,
            String optionalParameter2,
            String optionalParameter3,
            String optionalParameter4,
            String optionalParameter5,
            Timestamp submissionDate,
            java.math.BigDecimal expirationHours,
            String tlvOptionalParams,
            String requestId,
            String serviceType,
            java.math.BigDecimal esmClass,
            java.math.BigDecimal protocolId,
            java.math.BigDecimal priorityFlag,
            String scheduleDeliveryTime,
            String validityPeriod,
            java.math.BigDecimal smDefaultMsgId) throws SQLException {
        this(true);
        setSeqId(seq_id);
        setAppId(appName);
        setOrigMsisdn(origMsisdn);
        setDstMsisdn(dstMsisdn);
        setMsgTxt(msgTxt);
        setMsgType(msgType);
        setOrigType(origType);
        setLangId(langId);
        setNbtrials(nbtrials);
        setConcMsgSequeunce(concMsgSequeunce);
        setConcMsgCount(concMsgCount);
        setConcSarRefNum(concSarRefNum);
        setIPAddress(ipAddress);
        setReceiptRequested(deliveryRequest);
        setOptionalParameter1(optionalParameter1);
        setOptionalParameter2(optionalParameter2);
        setOptionalParameter3(optionalParameter3);
        setOptionalParameter4(optionalParameter4);
        setOptionalParameter5(optionalParameter5);
        setSubmissionDate(submissionDate);
        setExpirationHours(expirationHours);
        setTlvOptionalParams(tlvOptionalParams);
        setRequestId(requestId);
        setServiceType(serviceType);
        setEsmClass(esmClass);
        setProtocolId(protocolId);
        setPriorityFlag(priorityFlag);
        setScheduleDeliveryTime(scheduleDeliveryTime);
        setValidityPeriod(validityPeriod);
        setsmDefaultMsgId(smDefaultMsgId);
        
    }

//    public SMS(String appId, 
//            String origMsisdn, 
//            String dstMsisdn, 
//            String msgTxt, 
//            java.math.BigDecimal msgType, 
//            java.math.BigDecimal origType, 
//            java.math.BigDecimal langId, 
//            java.math.BigDecimal nbtrials, 
//            java.math.BigDecimal concMsgSequeunce, 
//            java.math.BigDecimal concMsgCount, 
//            java.math.BigDecimal concSarRefNum, 
//            BLOB messageBlob) throws SQLException 
//    {
//        this(true);
//        setAppId(appId);
//        setOrigMsisdn(origMsisdn);
//        setDstMsisdn(dstMsisdn);
//        setMsgTxt(msgTxt);
//        setMsgType(msgType);
//        setOrigType(origType);
//        setLangId(langId);
//        setNbtrials(nbtrials);
//        setConcMsgSequeunce(concMsgSequeunce);
//        setConcMsgCount(concMsgCount);
//        setConcSarRefNum(concSarRefNum);
//        //setMessageBlob(messageBlob);
//    }

    /* ORAData interface */
    public Datum toDatum(Connection c) throws SQLException {
        if (__tx != null && __onn != c) {
            release();
        }
        __onn = c;
        return _struct.toDatum(c, _SQL_NAME);
    }


    /* ORADataFactory interface */
    public ORAData create(Datum d, int sqlType) throws SQLException {
        return create(null, d, sqlType);
    }

    public void setFrom(SMS o) throws SQLException {
        setContextFrom(o);
        setValueFrom(o);
    }

    protected void setContextFrom(SMS o) throws SQLException {
        release();
        __tx = o.__tx;
        __onn = o.__onn;
    }

    protected void setValueFrom(SMS o) {
        _struct = o._struct;
    }

    protected ORAData create(SMS o, Datum d, int sqlType) throws SQLException {
        if (d == null) {
            if (o != null) {
                o.release();
            }
            ;
            return null;
        }
        if (o == null) {
            o = new SMS(false);
        }
        o._struct = new MutableStruct((STRUCT) d, _sqlType, _factory);
        o.__onn = ((STRUCT) d).getJavaSqlConnection();
        return o;
    }
    /* accessor methods */

    public void setSeqId(java.math.BigDecimal seqId) throws SQLException {
        _struct.setAttribute(0, seqId);
    }

    public java.math.BigDecimal getSeqId() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(0);
    }

    public String getAppId() throws SQLException {
        return (String) _struct.getAttribute(1);
    }

    public void setAppId(String appId) throws SQLException {
        _struct.setAttribute(1, appId);
    }

    public String getOrigMsisdn() throws SQLException {
        return (String) _struct.getAttribute(2);
    }

    public void setOrigMsisdn(String origMsisdn) throws SQLException {
        _struct.setAttribute(2, origMsisdn);
    }

    public String getDstMsisdn() throws SQLException {
        return (String) _struct.getAttribute(3);
    }

    public void setDstMsisdn(String dstMsisdn) throws SQLException {
        _struct.setAttribute(3, dstMsisdn);
    }

    public String getMsgTxt() throws SQLException {
        return (String) _struct.getAttribute(4);
    }

    public void setMsgTxt(String msgTxt) throws SQLException {
        _struct.setAttribute(4, msgTxt);
    }

    public java.math.BigDecimal getMsgType() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(5);
    }

    public void setMsgType(java.math.BigDecimal msgType) throws SQLException {
        _struct.setAttribute(5, msgType);
    }

    public java.math.BigDecimal getOrigType() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(6);
    }

    public void setOrigType(java.math.BigDecimal origType) throws SQLException {
        _struct.setAttribute(6, origType);
    }

    public java.math.BigDecimal getLangId() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(7);
    }

    public void setLangId(java.math.BigDecimal langId) throws SQLException {
        _struct.setAttribute(7, langId);
    }

    public java.math.BigDecimal getNbtrials() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(8);
    }

    public void setNbtrials(java.math.BigDecimal nbtrials) throws SQLException {
        _struct.setAttribute(8, nbtrials);
    }

    public java.math.BigDecimal getConcMsgSequeunce() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(9);
    }

    public void setConcMsgSequeunce(java.math.BigDecimal concMsgSequeunce) throws SQLException {
        _struct.setAttribute(9, concMsgSequeunce);
    }

    public java.math.BigDecimal getConcMsgCount() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(10);
    }

    public void setConcMsgCount(java.math.BigDecimal concMsgCount) throws SQLException {
        _struct.setAttribute(10, concMsgCount);
    }

    //public void setMessageBlob(BLOB messageBlob) throws SQLException {
    //    _struct.setOracleAttribute(12, messageBlob);
    //}
    //public BLOB getMessageBlob() throws SQLException {
    //    return (BLOB) _struct.getOracleAttribute(12);
    // }
    public java.math.BigDecimal getConcSarRefNum() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(11);
    }

    public void setConcSarRefNum(java.math.BigDecimal concSarRefNum) throws SQLException {
        _struct.setAttribute(11, concSarRefNum);
    }

    public void setIPAddress(String ipAddress) throws SQLException {
        _struct.setAttribute(12, ipAddress);
    }

    public String getIPAddress() throws SQLException {
        return (String) _struct.getAttribute(12);
    }

//    public void setTrackingId(String trackingId) throws SQLException {
//        _struct.setAttribute(13, trackingId);
//    }
//
//    public String getTrackingId() throws SQLException {
//        return (String) _struct.getAttribute(13);
//    }
    public void setReceiptRequested(java.math.BigDecimal receiptRequested) throws SQLException {
        _struct.setAttribute(13, receiptRequested);
    }

    public java.math.BigDecimal getReceiptRequested() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(13);
    }

    public void setOptionalParameter1(String optionalParameter1) throws SQLException {
        _struct.setAttribute(14, optionalParameter1);
    }

    public String getOptionalParameter1() throws SQLException {
        return (String) _struct.getAttribute(14);
    }

    public void setOptionalParameter2(String optionalParameter2) throws SQLException {
        _struct.setAttribute(15, optionalParameter2);
    }

    public String getOptionalParameter2() throws SQLException {
        return (String) _struct.getAttribute(15);
    }

    public void setOptionalParameter3(String optionalParameter3) throws SQLException {
        _struct.setAttribute(16, optionalParameter3);
    }

    public String getOptionalParameter3() throws SQLException {
        return (String) _struct.getAttribute(16);
    }

    public void setOptionalParameter4(String optionalParameter4) throws SQLException {
        _struct.setAttribute(17, optionalParameter4);
    }

    public String getOptionalParameter4() throws SQLException {
        return (String) _struct.getAttribute(17);
    }

    public void setOptionalParameter5(String optionalParameter5) throws SQLException {
        _struct.setAttribute(18, optionalParameter5);
    }

    public String getOptionalParameter5() throws SQLException {
        return (String) _struct.getAttribute(18);
    }

    public void setSubmissionDate(Timestamp submissionDate) throws SQLException {
        _struct.setAttribute(19, submissionDate);
    }

    public Timestamp getSubmissionDate() throws SQLException {
        return (Timestamp) _struct.getAttribute(19);
    }

    public void setExpirationHours(java.math.BigDecimal expirationHours) throws SQLException {
        _struct.setAttribute(20, expirationHours);
    }

    public java.math.BigDecimal getExpirationHours() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(20);
    }

    public void setTlvOptionalParams(String tlvOptionalParams) throws SQLException {
        _struct.setAttribute(21, tlvOptionalParams);
    }

    public String getTlvOptionalParams() throws SQLException {
        return (String) _struct.getAttribute(21);
    }

    public void setRequestId(String requestId) throws SQLException {
        _struct.setAttribute(22, requestId);
    }

    public String getRequestId() throws SQLException {
        return (String) _struct.getAttribute(22);
    }

    public void setServiceType(String serviceType) throws SQLException {
        _struct.setAttribute(23, serviceType);
    }

    public String getServiceType() throws SQLException {
        return (String) _struct.getAttribute(23);
    }

    public void setEsmClass(java.math.BigDecimal esmClass) throws SQLException {
        _struct.setAttribute(24, esmClass);
    }

    public java.math.BigDecimal getEsmClass() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(24);
    }

    public void setProtocolId(java.math.BigDecimal protocolId) throws SQLException {
        _struct.setAttribute(25, protocolId);
    }

    public java.math.BigDecimal getProtocolId() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(25);
    }

    public void setPriorityFlag(java.math.BigDecimal priorityFlag) throws SQLException {
        _struct.setAttribute(26, priorityFlag);
    }

    public java.math.BigDecimal getPriorityFlag() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(26);
    }

    public void setScheduleDeliveryTime(String scheduleDeliveryTime) throws SQLException {
        _struct.setAttribute(27, scheduleDeliveryTime);
    }

    public String getScheduleDeliveryTime() throws SQLException {
        return (String) _struct.getAttribute(27);
    }

    public void setValidityPeriod(String validityPeriod) throws SQLException {
        _struct.setAttribute(28, validityPeriod);
    }

    public String getValidityPeriod() throws SQLException {
        return (String) _struct.getAttribute(28);
    }

    public void setsmDefaultMsgId(java.math.BigDecimal smDefaultMsgId) throws SQLException {
        _struct.setAttribute(29, smDefaultMsgId);
    }

    public java.math.BigDecimal getsmDefaultMsgId() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(29);
    }
    // Only new params are logged with the orginator, destination and msg text
    @Override
    public String toString() {
        try {
            return "Originator: " + getOrigMsisdn() + " || Destination: " + getDstMsisdn()
                    + " || Msg text: " + getMsgTxt() + " || tlvs: " + getTlvOptionalParams()
                    + " || service type: " + getServiceType() + " || ESM Class: " + getEsmClass()
                    + " || Protocol id: " + getProtocolId() + " || Priority Flag: " + getPriorityFlag()
                    + " || Scheduled Delivery Time: " + getScheduleDeliveryTime() + " || Validity Period: " + getValidityPeriod()
                    + " || SM default msg id: " + getsmDefaultMsgId();
        } catch (SQLException ex) {
            return "UN-PARSEABLE";
        }
    }
}
