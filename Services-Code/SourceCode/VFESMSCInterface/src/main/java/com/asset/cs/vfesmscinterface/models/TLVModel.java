/**
 * created on: Dec 14, 2017 8:19:56 AM created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models;

import com.asset.contactstrategy.common.models.TLVModelValueType;

/**
 * @author mohamed.morsy
 *
 */
public class TLVModel extends CommandModel {

    private short tag;

    private int length;

    private TLVModelValueType type;

    private Object value;

    public TLVModel(short tag, int length, Object value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    public TLVModel(short tag, int length, TLVModelValueType type, Object value) {
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

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TLVModel [tag=" + Integer.toHexString(tag) + ", length=" + length + ", value=" + value + "]";
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
        TLVModel other = (TLVModel) obj;
        if (tag != other.tag) {
            return false;
        }
        return true;
    }

}
