/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.parsers;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.models.CommandModel;
import com.asset.cs.vfesmscinterface.models.EnquireRequestModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author Mohamed
 */
public class EnquireParser extends CommandRequestParser {

    @Override
    protected EnquireRequestModel parsePDUBody(HeaderModel headerModel, BufferByte pdu) {
        CommonLogger.businessLogger.debug("Enquire parser started...");
        long startTime = System.currentTimeMillis();

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Enquire request parsed").build());

//        CommonLogger.businessLogger.debug("Enquire parser time :" + (System.currentTimeMillis() - startTime));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Enquire Parser Timing")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        CommonLogger.businessLogger.debug("Enquire parser finished...");
        return new EnquireRequestModel(headerModel);
    }

}
