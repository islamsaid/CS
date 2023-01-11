package com.asset.cs.sendingsms.models.optional;

import client.BufferByte;
import com.asset.cs.sendingsms.models.CommandParam;

/**
 *
 * @author islam.said
 */
public class ReceiptedMessageId extends CommandParam {

    private Integer length;

    private String value;

    public ReceiptedMessageId(BufferByte pdu) {
        super(pdu);
    }

    @Override
    public void parsePDU(BufferByte pdu) {
        length = (int) pdu.removeShort();
        value = pdu.removeCString();
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[value=" + value + "]";
    }
}
