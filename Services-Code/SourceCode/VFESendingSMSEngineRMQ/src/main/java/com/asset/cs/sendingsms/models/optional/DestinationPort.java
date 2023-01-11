package com.asset.cs.sendingsms.models.optional;

import client.BufferByte;
import com.asset.cs.sendingsms.models.CommandParam;
import com.asset.cs.sendingsms.util.Utility;

/**
 *
 * @author islam.said
 */
public class DestinationPort extends CommandParam {

    private Integer length;

    private Integer value;

    public DestinationPort(BufferByte pdu) {
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
