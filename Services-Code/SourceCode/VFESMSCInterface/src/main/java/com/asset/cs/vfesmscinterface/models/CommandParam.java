/**
 * created on: Dec 28, 2017 09:28:45 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.models;

import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author mohamed.morsy
 */
public abstract class CommandParam extends Command {

	public CommandParam() {
	}

	public CommandParam(BufferByte pdu) {
		parsePDU(pdu);
	}

	public abstract void parsePDU(BufferByte pdu);

	public abstract boolean validateModel();

	public abstract BufferByte generateResponse();

}
