package com.asset.cs.vfesmscinterface.requestpreparator;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.EnqueueModelDeliverSM;
import com.asset.contactstrategy.common.models.TLVOptionalModel;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 *
 * @author islam.said
 */
public class DeliverSMRequestPreparator {

    public static BufferByte constructPDU(EnqueueModelDeliverSM enqueueModelDelvierSM, int sequenceNumber) {
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Constructing deliver_sm PDU").build());
        String message = null;
        BufferByte data;
        HeaderModel header;
        byte data_coding = 0;
        int length = 0;

        try {
            String Encoding;
            header = new HeaderModel();
            header.setCommandID(Data.DELIVER_SM);
            header.setCommandStatus(0x00);
            header.setSequenceNumber(sequenceNumber);

            switch (enqueueModelDelvierSM.getDataCoding()) {
                case 0: // english message
                    if (enqueueModelDelvierSM.getTlvs().get(Data.OPT_PAR_MSG_PAYLOAD) != null) {
                        message = new String(enqueueModelDelvierSM.getTlvs().get(Data.OPT_PAR_MSG_PAYLOAD).getValue().getBytes(Data.ENC_UTF8), Data.ENC_UTF8);
                    } else {
                        message = enqueueModelDelvierSM.getShortMessage();
                    }
                    if (message != null) {
                        length = message.getBytes().length;
                    }
                    data_coding = Data.DCS_English; // case default

                    break;

                case 1:
                    if (enqueueModelDelvierSM.getTlvs().get(Data.OPT_PAR_MSG_PAYLOAD) != null) {
                    message = enqueueModelDelvierSM.getTlvs().get(Data.OPT_PAR_MSG_PAYLOAD).getValue();
                    }else{
                    message = enqueueModelDelvierSM.getShortMessage();
                    }
                    if (message != null) {
                        Encoding = Data.ENC_UTF16;
                        length = message.getBytes(Encoding).length - 2;
                    }
                    data_coding = Data.DCS_Arabic; // case UCS2

                    break;
            }
            BufferByte bodyData = new BufferByte();

            // Mandatory Parameters
            bodyData.appendCString(enqueueModelDelvierSM.getServiceType());
            bodyData.appendByte(enqueueModelDelvierSM.getSoureceAddrTon());
            bodyData.appendByte(enqueueModelDelvierSM.getSourceAddrNpi());
            bodyData.appendCString(enqueueModelDelvierSM.getSourceAddr());
            bodyData.appendByte(enqueueModelDelvierSM.getDestAddrTon());
            bodyData.appendByte(enqueueModelDelvierSM.getDestAddrNpi());
            bodyData.appendCString(enqueueModelDelvierSM.getDestAddr());
            bodyData.appendByte(enqueueModelDelvierSM.getEsmClass());
            bodyData.appendByte(enqueueModelDelvierSM.getProtocolId());
            bodyData.appendByte(enqueueModelDelvierSM.getPriorityFlag());
            bodyData.appendCString(enqueueModelDelvierSM.getScheduleDeliveryTime());
            bodyData.appendCString(enqueueModelDelvierSM.getValidityPeriod());
            bodyData.appendByte(enqueueModelDelvierSM.getRegisteredDelivery());
            bodyData.appendByte(enqueueModelDelvierSM.getReplaceIfPresentFlag());
            bodyData.appendByte(data_coding);
            bodyData.appendByte(enqueueModelDelvierSM.getSmDefaultMsgId());
            bodyData.appendByte(enqueueModelDelvierSM.getSmLength());
            bodyData.appendString(enqueueModelDelvierSM.getShortMessage());

            // Optional Parameters
            final String messagePayload = message;
            final int messageLength = length;

            if (enqueueModelDelvierSM.getTlvs() != null) {
                HashMap<Short, TLVOptionalModel> tlvs = enqueueModelDelvierSM.getTlvs();
                tlvs.forEach((key, value) -> {
                    
                    if (key == Data.OPT_PAR_MSG_PAYLOAD) {
                        bodyData.appendShort(key);
                        bodyData.appendShort((short) messageLength);
                        bodyData.appendString(messagePayload);
                    } else {
                        bodyData.appendShort(key);
                        bodyData.appendShort((short) value.getLength());
                        if (value.getType() == null) {
                            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                                    "unknown type in optional params: " + value.getType()).build());
                        } else {
                            switch (value.getType()) {
                                case BYTE:
                                    bodyData.appendByte(Byte.parseByte(value.getValue()));
                                    break;
                                case CSTRING:
                                    bodyData.appendCString((String) value.getValue());
                                    break;
                                case INT:
                                    bodyData.appendInt(Integer.parseInt(value.getValue()));
                                    break;
                                case SHORT:
                                    bodyData.appendShort(Short.parseShort(value.getValue()));
                                    break;
                                case STRING:
                                    bodyData.appendString(value.getValue());
                                    break;
                                case NOVALUE:
                                    break;
                                default:
                                    CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                                            "unknown type in optional params: " + value.getType()).build());
                                    break;
                            }
                        }
                    }

                });

            }
            header.setCommandLength(bodyData.getBuffer().length + Data.PDU_HEADER_SIZE);
            data = new BufferByte();
            data.appendInt(header.getCommandLength());
            data.appendInt(header.getCommandID());
            data.appendInt(header.getCommandStatus());
            data.appendInt(header.getSequenceNumber());
            data.appendBytes(bodyData.getBuffer(), bodyData.getBuffer().length);

        } catch (UnsupportedEncodingException ex) {

            CommonLogger.errorLogger.error("Exception in constructing deliver_sm PDU || " + ex, ex);
            return null;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Exception in constructing deliver_sm PDU || " + ex, ex);
            return null;
        } catch (Throwable ex) {
            CommonLogger.errorLogger.error("Exception in constructing deliver_sm PDU || " + ex, ex);
            return null;
        }
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                "Finished Constructing deliver_sm PDU").build());
        return data;
    }

}
