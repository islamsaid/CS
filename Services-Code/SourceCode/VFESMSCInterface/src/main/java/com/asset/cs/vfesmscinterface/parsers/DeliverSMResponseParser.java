package com.asset.cs.vfesmscinterface.parsers;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.DeliverSMResponseModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
/**
 *
 * @author islam.said
 */
public class DeliverSMResponseParser extends CommandRequestParser{
    @Override
    protected CommandRequestModel parsePDUBody(HeaderModel headerModel, BufferByte pdu) {
        DeliverSMResponseModel deliverSmResponseModel = new DeliverSMResponseModel(headerModel);

        try {
            deliverSmResponseModel.setMessageId(pdu.removeCString());
            
           
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("general exception" + ex.getMessage(), ex);
            deliverSmResponseModel.getParserStatus().append("general exception" + ex.getMessage());
        }

        return deliverSmResponseModel;
    }
}
