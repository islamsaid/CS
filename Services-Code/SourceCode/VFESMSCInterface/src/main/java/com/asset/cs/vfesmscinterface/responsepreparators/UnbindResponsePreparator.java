/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.cs.vfesmscinterface.responsepreparators;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.models.UnbindRequestModel;
import com.asset.cs.vfesmscinterface.models.UnbindResponseModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author mohamed.osman
 */
public class UnbindResponsePreparator implements CommandResponsePreparator {

    public CommandResponseModel validateAndPrepareResponse(ConnectionStatusEnum status, CommandRequestModel command) {
//        CommonLogger.businessLogger.debug(UnbindResponsePreparator.class.getName() + " || " + "[validateAndPrepareResponse] started...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "validateAndPrepareResponse Started").build());
        long startTime = System.currentTimeMillis();
        UnbindRequestModel unbindRequestModel = (UnbindRequestModel) command;
        BufferByte bufferByte = new BufferByte();
        HeaderModel responseHeaderMode = new HeaderModel();

//        CommonLogger.businessLogger.debug(UnbindResponsePreparator.class.getName() + " || " + "Unable To Unbind, session status is:" + status);
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "unable to bind")
                .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
        bufferByte.appendInt(Data.PDU_HEADER_SIZE);
        responseHeaderMode.setCommandLength(Data.PDU_HEADER_SIZE);
        bufferByte.appendInt(getResponseCommandId(unbindRequestModel.getHeaderModel()));
        responseHeaderMode.setCommandID(getResponseCommandId(unbindRequestModel.getHeaderModel()));

        switch (status) {
            case BOUND:
            case OPEN:
                bufferByte.appendInt(Data.ESME_ROK_INT);
                responseHeaderMode.setCommandStatus(Data.ESME_ROK_INT);
                break;
            default:
                bufferByte.appendInt(Data.ESME_RSYSERR_INT);
                responseHeaderMode.setCommandStatus(Data.ESME_RSYSERR_INT);
                break;
        }
        bufferByte.appendInt(unbindRequestModel.getHeaderModel().getSequenceNumber());
        responseHeaderMode.setSequenceNumber(unbindRequestModel.getHeaderModel().getSequenceNumber());

//        CommonLogger.businessLogger.debug(UnbindResponsePreparator.class.getName() + " || " + "Validating request time :" + (System.currentTimeMillis() - startTime));
//        CommonLogger.businessLogger.debug(UnbindResponsePreparator.class.getName() + " || " + " [validateAndPrepareResponse] ended...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "validateAndResponse Ended")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());

        return new UnbindResponseModel(bufferByte, responseHeaderMode);

    }

}
