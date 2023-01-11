package com.asset.contactstrategy.common.models;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import oracle.jdbc.OracleTypes;
import oracle.jpub.runtime.MutableStruct;
import oracle.sql.Datum;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.STRUCT;
import java.sql.Timestamp;
import sqlj.runtime.ConnectionContext;
import sqlj.runtime.ref.DefaultContext;

/**
 *
 * @author aya.moawed 2595
 */
public class SMSBridgeDBObject implements ORAData, ORADataFactory {

    public static final String _SQL_NAME = "SMS_BRIDGE";//2595
    public static final int _SQL_TYPECODE = OracleTypes.STRUCT;

    /* connection management */
    protected DefaultContext __tx = null;
    protected Connection __onn = null;

    //2595
    private Long msgId;
    private String queueName;
    private String serviceName;
    private String originatorMSISDN;
    private String destinationMSISDN;
    private String msgText;
    private Integer msgType;
    private Integer originatorType;
    private Integer languageId;
    private String ipAddress;
    private String doNotApply;
    private String messagePriority;
    private String templateId;
    private String templateParameters;
    private String optionalParam1;
    private String optionalParam2;
    private String optionalParam3;
    private String optionalParam4;
    private String optionalParam5;
    private Date submissionDate;
    private String status;// status after it is sent to http

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOriginatorMSISDN() {
        return originatorMSISDN;
    }

    public void setOriginatorMSISDN(String originatorMSISDN) {
        this.originatorMSISDN = originatorMSISDN;
    }

    public String getDestinationMSISDN() {
        return destinationMSISDN;
    }

    public void setDestinationMSISDN(String destinationMSISDN) {
        this.destinationMSISDN = destinationMSISDN;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(Integer originatorType) {
        this.originatorType = originatorType;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDoNotApply() {
        return doNotApply;
    }

    public void setDoNotApply(String doNotApply) {
        this.doNotApply = doNotApply;
    }

    public String getMessagePriority() {
        return messagePriority;
    }

    public void setMessagePriority(String messagePriority) {
        this.messagePriority = messagePriority;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateParameters() {
        return templateParameters;
    }

    public void setTemplateParameters(String templateParameters) {
        this.templateParameters = templateParameters;
    }

    public String getOptionalParam1() {
        return optionalParam1;
    }

    public void setOptionalParam1(String optionalParam1) {
        this.optionalParam1 = optionalParam1;
    }

    public String getOptionalParam2() {
        return optionalParam2;
    }

    public void setOptionalParam2(String optionalParam2) {
        this.optionalParam2 = optionalParam2;
    }

    public String getOptionalParam3() {
        return optionalParam3;
    }

    public void setOptionalParam3(String optionalParam3) {
        this.optionalParam3 = optionalParam3;
    }

    public String getOptionalParam4() {
        return optionalParam4;
    }

    public void setOptionalParam4(String optionalParam4) {
        this.optionalParam4 = optionalParam4;
    }

    public String getOptionalParam5() {
        return optionalParam5;
    }

    public void setOptionalParam5(String optionalParam5) {
        this.optionalParam5 = optionalParam5;
    }

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
    private static int[] _sqlType = {2, 12, 12, 12, 12, 12, 2, 2, 2, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 93};
    private static ORADataFactory[] _factory = new ORADataFactory[20];
    protected static final SMSBridgeDBObject _SMSFactory = new SMSBridgeDBObject(false);

    public static ORADataFactory getORADataFactory() {
        return _SMSFactory;
    }
    /* constructors */

    protected SMSBridgeDBObject(boolean init) {
        if (init) {
            _struct = new MutableStruct(new Object[20], _sqlType, _factory);
        }
    }

    public SMSBridgeDBObject() {
        this(true);
        __tx = DefaultContext.getDefaultContext();
    }

    public SMSBridgeDBObject(DefaultContext c) /*throws SQLException*/ {
        this(true);
        __tx = c;
    }

    public SMSBridgeDBObject(Connection c) /*throws SQLException*/ {
        this(true);
        __onn = c;
    }

    //2595
    public SMSBridgeDBObject(java.math.BigDecimal msgID,//0
            String queueName,//1
            String serviceName,//2
            String origMsisdn,//3
            String dstMsisdn,//4
            String msgTxt,//5
            java.math.BigDecimal msgType,//6
            java.math.BigDecimal origType,//7
            java.math.BigDecimal langId,//8
            String ipAddress,//9
            String doNotApply,//10
            String msgPriority,//11
            String templateId,//12
            String templateParams,//13
            String optionalParameter1,//14
            String optionalParameter2,//15
            String optionalParameter3,//16
            String optionalParameter4,//17
            String optionalParameter5,/*18*/
            Timestamp submissionDate
    ) throws SQLException {
        this(true);
        setDBMsgId(msgID);
        setDBQueueName(queueName);
        setDBServiceName(serviceName);
        setDBOrigMsisdn(origMsisdn);
        setDBDstMsisdn(dstMsisdn);
        setDBMsgTxt(msgTxt);
        setDBMsgType(msgType);
        setDBOrigType(origType);
        setDBLangId(langId);
        setDBIPAddress(ipAddress);
        setDBDoNotApply(doNotApply);
        setDBMsgPriority(msgPriority);
        setDBTemplateID(templateId);
        setDBTemplateParams(templateParams);
        setDBOptionalParameter1(optionalParameter1);
        setDBOptionalParameter2(optionalParameter2);
        setDBOptionalParameter3(optionalParameter3);
        setDBOptionalParameter4(optionalParameter4);
        setDBOptionalParameter5(optionalParameter5);
        setDBSubmitionDate(submissionDate);
    }

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

    public void setFrom(SMSBridgeDBObject o) throws SQLException {
        setContextFrom(o);
        setValueFrom(o);
    }

    protected void setContextFrom(SMSBridgeDBObject o) throws SQLException {
        release();
        __tx = o.__tx;
        __onn = o.__onn;
    }

    protected void setValueFrom(SMSBridgeDBObject o) {
        _struct = o._struct;
    }

    protected ORAData create(SMSBridgeDBObject o, Datum d, int sqlType) throws SQLException {
        if (d == null) {
            if (o != null) {
                o.release();
            }
            ;
            return null;
        }
        if (o == null) {
            o = new SMSBridgeDBObject(false);
        }
        o._struct = new MutableStruct((STRUCT) d, _sqlType, _factory);
        o.__onn = ((STRUCT) d).getJavaSqlConnection();
        return o;
    }
    /* accessor methods */

    public void setDBMsgId(java.math.BigDecimal seqId) throws SQLException {
        _struct.setAttribute(0, seqId);
    }

    public java.math.BigDecimal getDBMsgId() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(0);
    }

    public String getDBQueueName() throws SQLException {
        return (String) _struct.getAttribute(1);
    }

    public void setDBQueueName(String appId) throws SQLException {
        _struct.setAttribute(1, appId);
    }

    public String getDBServiceName() throws SQLException {
        return (String) _struct.getAttribute(2);
    }

    public void setDBServiceName(String appId) throws SQLException {
        _struct.setAttribute(2, appId);
    }

    public String getDBOrigMsisdn() throws SQLException {
        return (String) _struct.getAttribute(3);
    }

    public void setDBOrigMsisdn(String origMsisdn) throws SQLException {
        _struct.setAttribute(3, origMsisdn);
    }

    public String getDBDstMsisdn() throws SQLException {
        return (String) _struct.getAttribute(4);
    }

    public void setDBDstMsisdn(String dstMsisdn) throws SQLException {
        _struct.setAttribute(4, dstMsisdn);
    }

    public String getDBMsgTxt() throws SQLException {
        return (String) _struct.getAttribute(5);
    }

    public void setDBMsgTxt(String msgTxt) throws SQLException {
        _struct.setAttribute(5, msgTxt);
    }

    public java.math.BigDecimal getDBMsgType() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(6);
    }

    public void setDBMsgType(java.math.BigDecimal msgType) throws SQLException {
        _struct.setAttribute(6, msgType);
    }

    public java.math.BigDecimal getDBOrigType() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(7);
    }

    public void setDBOrigType(java.math.BigDecimal origType) throws SQLException {
        _struct.setAttribute(7, origType);
    }

    public java.math.BigDecimal getDBLangId() throws SQLException {
        return (java.math.BigDecimal) _struct.getAttribute(8);
    }

    public void setDBLangId(java.math.BigDecimal langId) throws SQLException {
        _struct.setAttribute(8, langId);
    }

    public void setDBIPAddress(String ipAddress) throws SQLException {
        _struct.setAttribute(9, ipAddress);
    }

    public String getDBIPAddress() throws SQLException {
        return (String) _struct.getAttribute(9);
    }

    public void setDBDoNotApply(String ipAddress) throws SQLException {
        _struct.setAttribute(10, ipAddress);
    }

    public String getDBDoNotApply() throws SQLException {
        return (String) _struct.getAttribute(10);
    }

    public void setDBMsgPriority(String msgPriority) throws SQLException {
        _struct.setAttribute(11, msgPriority);
    }

    public String getDBMsgPriority() throws SQLException {
        return (String) _struct.getAttribute(11);
    }

    public void setDBTemplateID(String tempId) throws SQLException {
        _struct.setAttribute(12, tempId);
    }

    public String getDBTemplateID() throws SQLException {
        return (String) _struct.getAttribute(12);
    }

    public void setDBTemplateParams(String tempparam) throws SQLException {
        _struct.setAttribute(13, tempparam);
    }

    public String getDBTemplateParams() throws SQLException {
        return (String) _struct.getAttribute(13);
    }

    public void setDBOptionalParameter1(String optionalParameter1) throws SQLException {
        _struct.setAttribute(14, optionalParameter1);
    }

    public String getDBOptionalParameter1() throws SQLException {
        return (String) _struct.getAttribute(14);
    }

    public void setDBOptionalParameter2(String optionalParameter2) throws SQLException {
        _struct.setAttribute(15, optionalParameter2);
    }

    public String getDBOptionalParameter2() throws SQLException {
        return (String) _struct.getAttribute(15);
    }

    public void setDBOptionalParameter3(String optionalParameter3) throws SQLException {
        _struct.setAttribute(16, optionalParameter3);
    }

    public String getDBOptionalParameter3() throws SQLException {
        return (String) _struct.getAttribute(16);
    }

    public void setDBOptionalParameter4(String optionalParameter4) throws SQLException {
        _struct.setAttribute(17, optionalParameter4);
    }

    public String getDBOptionalParameter4() throws SQLException {
        return (String) _struct.getAttribute(17);
    }

    public void setDBOptionalParameter5(String optionalParameter5) throws SQLException {
        _struct.setAttribute(18, optionalParameter5);
    }

    public String getDBOptionalParameter5() throws SQLException {
        return (String) _struct.getAttribute(18);
    }

    public void setDBSubmitionDate(Timestamp sD) throws SQLException {
        _struct.setAttribute(19, sD);
    }

    public Timestamp getDBSubmitionDate() throws SQLException {
        return (Timestamp) _struct.getAttribute(19);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        SMSBridgeDBObject obj = new SMSBridgeDBObject();
        obj.setMsgId(msgId);
        obj.setTemplateParameters(templateParameters);
        obj.setTemplateId(templateId);
        obj.setStatus(status);
        obj.setServiceName(serviceName);
        obj.setQueueName(queueName);
        obj.setOriginatorType(originatorType);
        obj.setOriginatorMSISDN(originatorMSISDN);
        obj.setOptionalParam5(optionalParam5);
        obj.setMsgType(msgType);
        obj.setMsgText(msgText);
        obj.setMessagePriority(messagePriority);
        obj.setLanguageId(languageId);
        obj.setIpAddress(ipAddress);
        obj.setDoNotApply(doNotApply);
        obj.setDestinationMSISDN(destinationMSISDN);
        obj.setSubmissionDate(submissionDate);
        return obj;
    }

    @Override
    public String toString() {
        return "{" + "msgId=" + msgId + ", queueName=" + queueName + ", serviceName=" + serviceName + "}";//+ ", originatorMSISDN=" + originatorMSISDN + ", destinationMSISDN=" + destinationMSISDN + ", msgText=" + msgText + ", msgType=" + msgType + ", originatorType=" + originatorType + ", languageId=" + languageId + ", ipAddress=" + ipAddress + ", doNotApply=" + doNotApply + ", messagePriority=" + messagePriority + ", templateId=" + templateId + ", templateParameters=" + templateParameters + ", optionalParam1=" + optionalParam1 + ", optionalParam2=" + optionalParam2 + ", optionalParam3=" + optionalParam3 + ", optionalParam4=" + optionalParam4 + ", optionalParam5=" + optionalParam5 + ", submissionDate=" + submissionDate + "}\n";
    }

}
