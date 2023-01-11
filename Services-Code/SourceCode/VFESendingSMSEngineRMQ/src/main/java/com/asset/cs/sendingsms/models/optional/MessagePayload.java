package com.asset.cs.sendingsms.models.optional;

import client.BufferByte;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.sendingsms.models.CommandParam;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author islam.said
 */
public class MessagePayload extends CommandParam {
    private Integer length;

    private String value;

    public MessagePayload(BufferByte pdu) {
        super(pdu);
    }

    @Override
    public void parsePDU(BufferByte pdu) {
        length = (int) pdu.removeShort();
        try {
            value = pdu.removeString(length, null);
        } catch (UnsupportedEncodingException e) {
//			CommonLogger.debugLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [value=" + value + "]";
    }
}
