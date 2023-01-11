/**
 * created on: Jan 2, 2018 10:38:01 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models.optional;

import com.asset.cs.vfesmscinterface.models.CommandParam;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class DestAddrSubunit extends CommandParam {

	private Integer length;

	private Integer value;

	public DestAddrSubunit(BufferByte pdu) {
		super(pdu);
	}

	@Override
	public void parsePDU(BufferByte pdu) {
		length = (int) pdu.removeShort();
		value = (int) pdu.removeByte();
	}

	@Override
	public boolean validateModel() {
		return (value >= 0 && value <= 4);
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
