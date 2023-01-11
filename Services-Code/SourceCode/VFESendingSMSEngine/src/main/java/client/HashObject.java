package client;

import com.asset.contactstrategy.common.models.SMS;

/**
 * *****************************************************************************************
 */
/**
 * ******************************************************************************************
 * Maintaining a fixed structure of data enqueued into the hashtable
 *
 *****************************************************************
 */
/**
 * ***************************************************************************************
 */
public class HashObject {

    SMS SMS = null;
    int instanceId;
    int priority;
    String log = null;
    String enqueueTime = null;//        boolean is_concatenated = false;
    //following variables are to be used
    //when identifying a batch sms
    int id;
    int type;
    private String dequeueTime;
    private boolean optionalMsgId = false;
    private String msgid = null;
    private String batchId = null;

    private String sendingTime;
    private String submitSMRespTime;
    private int messageStatus;

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getSubmitSMRespTime() {
        return submitSMRespTime;
    }

    public void setSubmitSMRespTime(String submitSMRespTime) {
        this.submitSMRespTime = submitSMRespTime;
    }

    public String getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(String sendingTime) {
        this.sendingTime = sendingTime;
    }

    public boolean isOptionalMsgId() {
        return optionalMsgId;
    }

    public void setOptionalMsgId(boolean optionalMsgId) {
        this.optionalMsgId = optionalMsgId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getDequeueTime() {
        return dequeueTime;
    }

    public void setDequeueTime(String dequeueTime) {
        this.dequeueTime = dequeueTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SMS getSMS() {
        return SMS;
    }

    public void setSMS(SMS SMS) {
        this.SMS = SMS;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getEnqueueTime() {
        return enqueueTime;
    }

    public void setEnqueueTime(String enqueueTime) {
        this.enqueueTime = enqueueTime;
    }

    public HashObject() {
        SMS = new SMS();
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    @Override
    public String toString() {
        if (SMS != null) {
            return "QUEUE_MSG_ID || " + getMsgid() + " || Request: {" + SMS.toString() + "}";
        } else {
            return "QUEUE_MSG_ID || " + getMsgid() + " || Request: UNPARSEABLE ";
        }
    }
}
