/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.parsers;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import com.asset.cs.vfesmscinterface.utils.ParserFactory;

/**
 *
 * @author mostafa.kashif
 */
public abstract class CommandRequestParser {

    public static CommandRequestModel parsePDU(BufferByte pdu, HeaderModel headerModel) throws Exception {

        CommandRequestParser commandRequestParser = ParserFactory.getCommandParser(headerModel);

        CommandRequestModel commandRequestModel = commandRequestParser.parsePDUBody(headerModel, pdu);

        return commandRequestModel;
    }

    protected abstract CommandRequestModel parsePDUBody(HeaderModel headerModel, BufferByte pdu);

    public static final HeaderModel parsePDUHeader(BufferByte pdu) throws Exception {
        CommonLogger.businessLogger.debug("HeaderParser started...");
        long startTime = System.currentTimeMillis();
        HeaderModel headerModel = null;
        try {

            headerModel = new HeaderModel();
            headerModel.setCommandLength(pdu.removeInt());
            headerModel.setCommandID(pdu.removeInt());
            headerModel.setCommandStatus(pdu.removeInt());
            //pdu.removeInt();
            headerModel.setSequenceNumber(pdu.removeInt());

            String commandLengthValue = headerModel.getCommandLength() + "";
            String commandIdValue = Integer.toHexString(headerModel.getCommandID());
            String commandStatusValue = "NULL";
//            CommonLogger.businessLogger.debug("Request-CommandLength || " + commandLengthValue);
//            CommonLogger.businessLogger.debug("Request-Command_id || 0x" + commandIdValue);
//            CommonLogger.businessLogger.debug("Request-CommandStatus || " + commandStatusValue);
//            CommonLogger.businessLogger.debug("Request-SequenceNumber || " + headerModel.getSequenceNumber());
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Request-Command Stats")
                    .put(GeneralConstants.StructuredLogKeys.LENGTH, commandLengthValue)
                    .put(GeneralConstants.StructuredLogKeys.ID, commandIdValue)
                    .put(GeneralConstants.StructuredLogKeys.STATUS, commandStatusValue)
                    .put(GeneralConstants.StructuredLogKeys.SEQUENCE_NUMBER, headerModel.getSequenceNumber()));

            CommonLogger.businessLogger.debug("HeaderParser finished...");
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
            throw new Exception("Corrupted request header, " + e.getMessage());
        }
//		CommonLogger.businessLogger.debug("HeaderParser time :" + (System.currentTimeMillis() - startTime));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Header Parser Timing")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        return headerModel;
    }
}
