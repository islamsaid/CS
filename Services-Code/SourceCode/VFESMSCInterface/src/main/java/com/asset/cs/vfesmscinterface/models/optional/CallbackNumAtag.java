/**
 * created on: Jan 2, 2018 10:40:01 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models.optional;

import java.io.UnsupportedEncodingException;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.vfesmscinterface.models.CommandParam;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class CallbackNumAtag extends CommandParam {

	private Integer length;

	private String value;

	public CallbackNumAtag(BufferByte pdu) {
		super(pdu);
	}

	@Override
	public void parsePDU(BufferByte pdu) {
		length = (int) pdu.removeShort();
		try {
			value = pdu.removeString(length, null);
		} catch (UnsupportedEncodingException e) {
			CommonLogger.businessLogger.error(e.getMessage());
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
