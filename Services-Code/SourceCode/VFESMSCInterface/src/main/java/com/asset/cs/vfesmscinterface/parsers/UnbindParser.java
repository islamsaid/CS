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
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.models.UnbindRequestModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author mohamed.osman
 */
public class UnbindParser extends CommandRequestParser {

    @Override
    protected UnbindRequestModel parsePDUBody(HeaderModel headerModel, BufferByte pdu) {
        CommonLogger.businessLogger.debug("Unbind parser started...");
        long startTime = System.currentTimeMillis();

        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Unbind request parsed").build());
//        CommonLogger.businessLogger.debug("Unbind parser time :" + (System.currentTimeMillis() - startTime));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Unbind Parser Timing")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        CommonLogger.businessLogger.debug("Unbind parser finished...");
        return new UnbindRequestModel(headerModel);
    }

}
