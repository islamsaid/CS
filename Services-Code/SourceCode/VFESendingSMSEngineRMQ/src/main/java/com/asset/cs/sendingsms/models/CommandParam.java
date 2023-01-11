package com.asset.cs.sendingsms.models;

import client.BufferByte;

/**
 *
 * @author islam.said
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
