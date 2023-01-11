package com.asset.cs.vfesmscinterface.responsepreparators;

import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.cs.vfesmscinterface.constants.ConnectionStatusEnum;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.models.BindRequestModel;
import com.asset.cs.vfesmscinterface.models.BindResponseModel;
import com.asset.cs.vfesmscinterface.models.CommandRequestModel;
import com.asset.cs.vfesmscinterface.models.CommandResponseModel;
import com.asset.cs.vfesmscinterface.models.TLVModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import com.asset.cs.vfesmscinterface.validators.CommonValidator;

/**
 *
 * @author mohamed.osman
 */
public class BindResponsePreparator implements CommandResponsePreparator {

    public BindRequestModel validateBind(CommandRequestModel command) {
        CommonLogger.businessLogger.debug("[validateBind] started...");
        BindRequestModel bindRequestModel = (BindRequestModel) command;

        command.getHeaderModel().setCommandStatus(CommonValidator.validateMaxSessions());
        if (command.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {
            command.getHeaderModel().setCommandStatus(CommonValidator.validateClient(bindRequestModel));
            if (command.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {

            command.getHeaderModel().setCommandStatus(CommonValidator.validateInterfaceVersion(bindRequestModel));
            if (command.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {
                command.getHeaderModel().setCommandStatus(CommonValidator.validateAddressSource(bindRequestModel));
                return bindRequestModel;
            }

            }
        }
        CommonLogger.businessLogger.debug("[validateBind] ended...");
        return bindRequestModel;
    }

    public static BufferByte prepareBufferByte(int commandId, int commandStatus, int sequenceNum, BufferByte bodyData, BindResponseModel bindResponseModel) {
        BufferByte bufferByte = new BufferByte();
        if (bodyData.length() != 0) {
            bodyData.appendByte((byte) 0x00); // eslam.ahmed | 5-5-2020
            if (Defines.SMSC_INTERFACE_PROPERTIES.APPENDING_BIND_OPTIONAL_PARAM_FLAG_VALUE.equalsIgnoreCase("TRUE")) {
                bodyData.appendShort(Data.OPT_PAR_SC_IF_VER);
                bodyData.appendShort((short) 1);
                bodyData.appendByte(Data.SMPP_V34);
                bindResponseModel.setScInterfaceVersion(new TLVModel(Data.OPT_PAR_SC_IF_VER, (short) 1, Data.SMPP_V34));
            }
            bufferByte.appendInt(bodyData.getBuffer().length + Data.PDU_HEADER_SIZE);
            bufferByte.appendInt(commandId);
            bufferByte.appendInt(commandStatus);
            bufferByte.appendInt(sequenceNum);
            bufferByte.appendBytes(bodyData.getBuffer(), bodyData.getBuffer().length);

            return bufferByte;
        } else {
            bufferByte.appendInt(Data.PDU_HEADER_SIZE);
            bufferByte.appendInt(commandId);
            bufferByte.appendInt(commandStatus);
            bufferByte.appendInt(sequenceNum);

            return bufferByte;
        }

    }

    @Override
    public CommandResponseModel validateAndPrepareResponse(ConnectionStatusEnum status, CommandRequestModel command) {
        CommonLogger.businessLogger.debug("[validateAndPrepareResponse] started...");
        long startTime = System.currentTimeMillis();
        BindRequestModel bindRequestModel;
        BufferByte bufferByte = new BufferByte();
        BufferByte bodyData = new BufferByte();
        BindResponseModel bindResponseModel = new BindResponseModel();

        // if session status is opened
        switch (status) {
            case OPEN:
//                CommonLogger.businessLogger.debug("session status is:" + status);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Session Status")
                        .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
                bindRequestModel = validateBind(command);

                CommonLogger.businessLogger.debug(bindRequestModel.toString() + bindRequestModel.getHeaderModel().toString());
                if (bindRequestModel.getHeaderModel().getCommandStatus() == Data.ESME_ROK_INT) {
                    CommonLogger.businessLogger.debug("Successful Bind Request");

                    bodyData.appendString(bindRequestModel.getSystemID());
                    bindResponseModel.setSystemId(bindRequestModel.getSystemID());
                    bufferByte = prepareBufferByte(getResponseCommandId(bindRequestModel.getHeaderModel()), bindRequestModel.getHeaderModel().getCommandStatus(), bindRequestModel.getHeaderModel().getSequenceNumber(), bodyData, bindResponseModel);

                } else {

//                    CommonLogger.businessLogger.debug("Bind Request Failed,Command Status: " + bindRequestModel.getHeaderModel().getCommandStatus());
                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Request Failed")
                            .put(GeneralConstants.StructuredLogKeys.STATUS, bindRequestModel.getHeaderModel().getCommandStatus()).build());
                    bufferByte = prepareBufferByte(getResponseCommandId(bindRequestModel.getHeaderModel()), bindRequestModel.getHeaderModel().getCommandStatus(), bindRequestModel.getHeaderModel().getSequenceNumber(), bodyData, bindResponseModel);
                }

                // if session status is closed 
                break;
            case CLOSED:
//                CommonLogger.businessLogger.debug("Bind Request Failed, Session Status Is: " + status);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Request Failed")
                        .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
                bindRequestModel = (BindRequestModel) command;

                bufferByte = prepareBufferByte(getResponseCommandId(bindRequestModel.getHeaderModel()), Data.ESME_RBINDFAIL_INT, bindRequestModel.getHeaderModel().getSequenceNumber(), bodyData, bindResponseModel);

                break;
            // if session status is Bound
            case BOUND:
//                CommonLogger.businessLogger.debug("Bind Request Failed, Session Status Is: " + status);
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Bind Request Failed")
                        .put(GeneralConstants.StructuredLogKeys.STATUS, status).build());
                bindRequestModel = (BindRequestModel) command;
                bufferByte = prepareBufferByte(getResponseCommandId(bindRequestModel.getHeaderModel()), Data.ESME_RALYBND_INT, bindRequestModel.getHeaderModel().getSequenceNumber(), bodyData, bindResponseModel);
                break;
            default:
                CommonLogger.businessLogger.debug("Bind Request Failed, System Error");
                bindRequestModel = (BindRequestModel) command;
                bufferByte = prepareBufferByte(getResponseCommandId(bindRequestModel.getHeaderModel()), Data.ESME_RSYSERR_INT, bindRequestModel.getHeaderModel().getSequenceNumber(), bodyData, bindResponseModel);
                break;
        }
//        CommonLogger.businessLogger.debug("Validating request time :" + (System.currentTimeMillis() - startTime));
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Validating Request")
                .put(GeneralConstants.StructuredLogKeys.ELAPSED_TIME, (System.currentTimeMillis() - startTime)).build());
        CommonLogger.businessLogger.debug(" [validateAndPrepareResponse] ended...");

        bindResponseModel.setPdu(bufferByte);

        bindRequestModel.getHeaderModel().setCommandID(getResponseCommandId(bindRequestModel.getHeaderModel()));

        bindResponseModel.setHeaderModel(bindRequestModel.getHeaderModel());

        return bindResponseModel;
    }

}
