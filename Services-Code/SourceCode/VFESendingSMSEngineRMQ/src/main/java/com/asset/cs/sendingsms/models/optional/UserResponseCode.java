package com.asset.cs.sendingsms.models.optional;

import client.BufferByte;
import com.asset.cs.sendingsms.models.CommandParam;

/**
 *
 * @author islam.said
 */
public class UserResponseCode extends CommandParam {

    private Integer length;

    private Integer value;

    public UserResponseCode(BufferByte pdu) {
        super(pdu);
    }

    @Override
    public void parsePDU(BufferByte pdu) {
        length = (int) pdu.removeShort();
        value = (int) pdu.removeByte();
    }

    @Override
    public boolean validateModel() {
        return true;
    }

    @Override
    public BufferByte generateResponse() {
        throw new UnsupportedOperationException("generateResponse UnsupportedOperation");
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [value=" + value + "]";
    }
}
