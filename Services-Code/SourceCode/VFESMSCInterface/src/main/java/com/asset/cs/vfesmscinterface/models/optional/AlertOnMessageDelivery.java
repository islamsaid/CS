/**

 * created on: Jan 2, 2018 01:00:01 PM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models.optional;

import com.asset.cs.vfesmscinterface.models.CommandParam;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class AlertOnMessageDelivery extends CommandParam {

	private Integer length;

	private Integer value;

	public AlertOnMessageDelivery(BufferByte pdu) {
		super(pdu);
	}

	@Override
	public void parsePDU(BufferByte pdu) {
		length = (int) pdu.removeShort();
		value = 0;
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
