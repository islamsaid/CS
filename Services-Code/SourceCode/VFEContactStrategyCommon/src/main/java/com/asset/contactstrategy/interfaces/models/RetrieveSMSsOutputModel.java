/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.models;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 *
 * @author esmail.anbar
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RetrieveSMSsOutputModel {
    
    @XmlElement(name="ErrorCode")
    private String errorCode;
    @XmlElement(name="ErrorDescription")
    private String errorDescription;
    @XmlElementWrapper(name="Messages")
    @XmlElement(name="Message")
    private ArrayList<RetrieveSMSsMessageModel> messages;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public ArrayList<RetrieveSMSsMessageModel> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<RetrieveSMSsMessageModel> messages) {
        this.messages = messages;
    }
    
}
