/**
 * created on: Jan 2, 2018 11:04:01 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models.optional;

import com.asset.cs.vfesmscinterface.models.CommandParam;
import com.asset.cs.vfesmscinterface.models.TLVModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 * @author mohamed.morsy
 *
 */
public class PrivacyIndicator extends CommandParam {

	private Integer length;

	private Integer value;

	public PrivacyIndicator(BufferByte pdu) {
		super(pdu);
	}

	@Override
	public void parsePDU(BufferByte pdu) {
		length = (int) pdu.removeShort();
		value = (int) pdu.removeByte();
	}

	@Override
	public boolean validateModel() {
		return value >= 0 && value <= 3;
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
