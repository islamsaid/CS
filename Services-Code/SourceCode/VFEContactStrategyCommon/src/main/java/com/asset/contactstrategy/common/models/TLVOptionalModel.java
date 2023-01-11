package com.asset.contactstrategy.common.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;

/**
 * @author mohamed.morsy
 *
 */
public class TLVOptionalModel implements Serializable{

    private short tag;

    private int length;

    private TLVModelValueType type;

    private String value;

    public TLVOptionalModel() {
    }

    public TLVOptionalModel(short tag, int length, TLVModelValueType type, String value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
        this.type = type;
    }

    public short getTag() {
        return tag;
    }

    public void setTag(short tag) {
        this.tag = tag;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public TLVModelValueType getType() {
        return type;
    }

    public void setType(TLVModelValueType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TLVModel [tag=" + tag + ", length=" + length + ", type=" + type + ", value=" + value + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + tag;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TLVOptionalModel other = (TLVOptionalModel) obj;
        if (tag != other.tag) {
            return false;
        }
        return true;
    }

}
