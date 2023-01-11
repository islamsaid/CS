/**
 * created on: Jan 2, 2018 10:40:01 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models.optional;

import com.asset.cs.vfesmscinterface.models.CommandParam;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class MoreMessagesToSend extends CommandParam {

	private Integer length;

	private Integer value;

	public MoreMessagesToSend(BufferByte pdu) {
		super(pdu);
	}

	@Override
	public void parsePDU(BufferByte pdu) {
		length = (int) pdu.removeShort();
		value = (int) pdu.removeByte();
	}

	@Override
	public boolean validateModel() {
		return (value >= 0 && value <= 1);
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
