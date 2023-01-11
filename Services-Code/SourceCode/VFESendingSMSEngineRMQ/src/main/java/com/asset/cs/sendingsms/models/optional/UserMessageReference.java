package com.asset.cs.sendingsms.models.optional;

import client.BufferByte;
import com.asset.cs.sendingsms.models.CommandParam;
import com.asset.cs.sendingsms.util.Utility;

/**
 *
 * @author islam.said
 */
public class UserMessageReference extends CommandParam {

    private Integer length;

    private Integer value;

    public UserMessageReference(BufferByte pdu) {
        super(pdu);
    }

    @Override
    public void parsePDU(BufferByte pdu) {
        length = (int) pdu.removeShort();
        value = Utility.convertSignedShortToInt(pdu.removeShort());
    }

    @Override
    public boolean validateModel() {
        return true;
    }

    @Override
    public BufferByte generateResponse() {
        throw new UnsupportedOperationException("generateResponse UnsupportedOperation");
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
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
        return getClass().getSimpleName() + "[value=" + value + "]";
    }
}
