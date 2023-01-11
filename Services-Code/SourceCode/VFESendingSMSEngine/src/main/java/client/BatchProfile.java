package client;

import java.io.Serializable;

public class BatchProfile implements Serializable {
    
    public int generatedID = -1;
    public String messageText;
    public int languageID;
    public int messageType;
    public int priv_level; //not used
    public String sender;
    public int senderType;
    public long numberOfMessagesInQ;
    
    public int getGeneratedID() {
        return generatedID;
    }
    
    public void setGeneratedID(int generatedID) {
        this.generatedID = generatedID;
    }
    
    public String getMessageText() {
        return messageText;
    }
    
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    public int getLanguageID() {
        return languageID;
    }
    
    public void setLanguageID(int languageID) {
        this.languageID = languageID;
    }
    
    public int getMessageType() {
        return messageType;
    }
    
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
    
    public int getPriv_level() {
        return priv_level;
    }
    
    public void setPriv_level(int priv_level) {
        this.priv_level = priv_level;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public int getSenderType() {
        return senderType;
    }
    
    public void setSenderType(int senderType) {
        this.senderType = senderType;
    }
    
    public long getNumberOfMessagesInQ() {
        return numberOfMessagesInQ;
    }
    
    public void setNumberOfMessagesInQ(long numberOfMessagesInQ) {
        this.numberOfMessagesInQ = numberOfMessagesInQ;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        BatchProfile clonnedModel = new BatchProfile();
        clonnedModel.setGeneratedID(this.generatedID);
        clonnedModel.setLanguageID(this.languageID);
        clonnedModel.setMessageText(this.messageText);
        clonnedModel.setMessageType(this.messageType);
        clonnedModel.setNumberOfMessagesInQ(this.numberOfMessagesInQ);
        clonnedModel.setPriv_level(this.priv_level);
        clonnedModel.setSender(this.sender);
        clonnedModel.setSenderType(this.senderType);
        return clonnedModel;
    }
}
