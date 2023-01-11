package com.asset.contactstrategy.common.models;

import java.util.HashMap;

/**
 *
 * @author islam.said
 */
public class EnqueueModel {

    private String transId;
    private String serviceType;
    private byte soureceAddrTon;
    private byte sourceAddrNpi;
    private String sourceAddr;
    private byte destAddrTon;
    private byte destAddrNpi;
    private String destAddr;
    private byte esmClass;
    private byte registeredDelivery;
    private byte dataCoding;

    private HashMap<Short, TLVOptionalModel> tlvs;

    public EnqueueModel() {
    }

    public EnqueueModel(String serviceType, byte soureceAddrTon, byte sourceAddrNpi, String sourceAddr, byte destAddrTon, byte destAddrNpi, String destAddr, byte esmClass, byte registeredDelivery, byte dataCoding, HashMap<Short, TLVOptionalModel> tlvs, String transId) {
        this.serviceType = serviceType;
        this.soureceAddrTon = soureceAddrTon;
        this.sourceAddrNpi = sourceAddrNpi;
        this.sourceAddr = sourceAddr;
        this.destAddrTon = destAddrTon;
        this.destAddrNpi = destAddrNpi;
        this.destAddr = destAddr;
        this.esmClass = esmClass;
        this.registeredDelivery = registeredDelivery;
        this.dataCoding = dataCoding;
        this.tlvs = tlvs;
        this.transId = transId;
    }

    public EnqueueModel(String serviceType, byte soureceAddrTon, byte sourceAddrNpi, String sourceAddr, byte destAddrTon, byte destAddrNpi, String destAddr, byte esmClass, byte registeredDelivery, byte dataCoding, String transId) {

        this.serviceType = serviceType;
        this.soureceAddrTon = soureceAddrTon;
        this.sourceAddrNpi = sourceAddrNpi;
        this.sourceAddr = sourceAddr;
        this.destAddrTon = destAddrTon;
        this.destAddrNpi = destAddrNpi;
        this.destAddr = destAddr;
        this.esmClass = esmClass;
        this.registeredDelivery = registeredDelivery;
        this.dataCoding = dataCoding;
        this.transId = transId;
        this.tlvs = new HashMap<>();
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public byte getSoureceAddrTon() {
        return soureceAddrTon;
    }

    public void setSoureceAddrTon(byte soureceAddrTon) {
        this.soureceAddrTon = soureceAddrTon;
    }

    public byte getSourceAddrNpi() {
        return sourceAddrNpi;
    }

    public void setSourceAddrNpi(byte sourceAddrNpi) {
        this.sourceAddrNpi = sourceAddrNpi;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public byte getDestAddrTon() {
        return destAddrTon;
    }

    public void setDestAddrTon(byte destAddrTon) {
        this.destAddrTon = destAddrTon;
    }

    public byte getDestAddrNpi() {
        return destAddrNpi;
    }

    public void setDestAddrNpi(byte destAddrNpi) {
        this.destAddrNpi = destAddrNpi;
    }

    public String getDestAddr() {
        return destAddr;
    }

    public void setDestAddr(String destAddr) {
        this.destAddr = destAddr;
    }

    public byte getEsmClass() {
        return esmClass;
    }

    public void setEsmClass(byte esmClass) {
        this.esmClass = esmClass;
    }

    public byte getRegisteredDelivery() {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(byte registeredDelivery) {
        this.registeredDelivery = registeredDelivery;
    }

    public byte getDataCoding() {
        return dataCoding;
    }

    public void setDataCoding(byte dataCoding) {
        this.dataCoding = dataCoding;
    }

    public HashMap<Short, TLVOptionalModel> getTlvs() {
        return tlvs;
    }

    public void setTlvs(HashMap<Short, TLVOptionalModel> tlvs) {
        this.tlvs = tlvs;
    }
}
