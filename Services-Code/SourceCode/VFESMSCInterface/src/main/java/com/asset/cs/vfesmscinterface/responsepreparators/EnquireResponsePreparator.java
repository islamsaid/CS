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
import com.asset.cs.vfesmscinterface.models.EnquireResponseModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;

/**
 *
 * @author Eman.fawzy
 */
public class EnquireResponsePreparator implements CommandResponsePreparator {

    @Override
    public CommandResponseModel validateAndPrepareResponse(ConnectionStatusEnum status, CommandRequestModel command) {
        CommonLogger.businessLogger.debug("EnquireResponsePreparator started...");
        long startTime = System.currentTimeMillis();

        CommandResponseModel responsePreparator = null;

        try {
            BufferByte bufferByte = new BufferByte();
            responsePreparator = new EnquireResponseModel();

            switch (status) {
                case BOUND:
                    command.getHeaderModel().setCommandStatus(Data.ESME_ROK);
                    break;
                default:
                    command.getHeaderModel().setCommandStatus(Data.ESME_RSYSERR);
                    break;
            }

            bufferByte.appendInt(command.getHeaderModel().getCommandLength());
            bufferByte.appendInt(getResponseCommandId(command.getHeaderModel()));
            bufferByte.appendInt(command.getHeaderModel().getCommandStatus());
            bufferByte.appendInt(command.getHeaderModel().getSequenceNumber());

            responsePreparator.setPdu(bufferByte);

            command.getHeaderModel().setCommandID(getResponseCommandId(command.getHeaderModel()));

            responsePreparator.setHeaderModel(command.getHeaderModel());

            CommonLogger.businessLogger.debug("EnquireResponsePreparator finished...");
        } catch (Exception e) {
            CommonLogger.businessLogger.error(e.getMessage());
            CommonLogger.errorLogger.error(e.getMessage(), e);
        }
//        CommonLogger.businessLogger.debug("EnquireResponsePreparator time :" + (System.currentTimeMillis() - startTime));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "EnquireResponsePreparator Timing")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        return responsePreparator;
    }

}
