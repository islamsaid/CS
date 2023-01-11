package com.asset.contactstrategy.common.models;

/**
 *
 * @author aya.moawed
 * 2595
 */
public class OriginatorTypeModel {
    private Integer originatorType;
    private Integer allowedLength;
    private String originatorName;

    public Integer getOriginatorType() {
        return originatorType;
    }

    public void setOriginatorType(Integer originatorType) {
        this.originatorType = originatorType;
    }

    public Integer getAllowedLength() {
        return allowedLength;
    }

    public void setAllowedLength(Integer allowedLength) {
        this.allowedLength = allowedLength;
    }

    public String getOriginatorName() {
        return originatorName;
    }

    public void setOriginatorName(String originatorName) {
        this.originatorName = originatorName;
    }
}
