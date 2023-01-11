/**
 * created on: Dec 14, 2017 8:22:08 AM created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.parsers;

import java.io.UnsupportedEncodingException;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.vfesmscinterface.models.TLVModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class TLVParser {

    public TLVModel parse(BufferByte pdu) {
        try {

            short tag = pdu.removeShort();
            int length = pdu.removeShort();
            String value = pdu.removeString(length, null);

            return new TLVModel(tag, length, value);
        } catch (UnsupportedEncodingException e) {
            CommonLogger.businessLogger.error("Error while parsing TLV pdu body, " + e.getMessage());
            CommonLogger.errorLogger.error("Error while parsing TLV pdu body, ", e);
        }
        return null;
    }
}
