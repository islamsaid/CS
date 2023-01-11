/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.parsers;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.models.AddressModel;
import com.asset.cs.vfesmscinterface.models.BindRequestModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author mohamed.osman
 */
public class BindParser extends CommandRequestParser {

    public BindParser() {
    }

    @Override
    protected BindRequestModel parsePDUBody(HeaderModel headerModel, BufferByte pdu) {
        CommonLogger.businessLogger.debug("Bind parser started...");
        long startTime = System.currentTimeMillis();
        BindRequestModel reqBody = new BindRequestModel(headerModel);
        try {
            reqBody.setSystemID(pdu.removeCString());
            reqBody.setPassword(pdu.removeCString());
            reqBody.setSystemType(pdu.removeCString());
            reqBody.setInterfaceVersion(pdu.removeByte());
            reqBody.setAddressModel(new AddressModel(pdu));
        } catch (Exception e) {
            CommonLogger.businessLogger.error("Error while parsing bind pdu body, " + e.getMessage());
            CommonLogger.errorLogger.error("Error while parsing bind pdu body, ", e);
            reqBody.getParserStatus().append("Error while parsing bind pdu body, " + e.getMessage());
        }
//		CommonLogger.businessLogger.debug("Bind parser time :" + (System.currentTimeMillis() - startTime));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Binding Parser Timing")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        CommonLogger.businessLogger.debug("Bind parser finished...");

        return reqBody;

    }

}
