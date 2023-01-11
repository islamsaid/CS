/**
 * created on: Dec 14, 2017 09:54:45 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.parsers;

import java.io.UnsupportedEncodingException;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.TLVModelValueType;
import com.asset.cs.vfesmscinterface.constants.Data;
import com.asset.cs.vfesmscinterface.exceptions.InvalidDateFormatException;
import com.asset.cs.vfesmscinterface.models.AddressModel;
import com.asset.cs.vfesmscinterface.models.HeaderModel;
import com.asset.cs.vfesmscinterface.models.SMPPDate;
import com.asset.cs.vfesmscinterface.models.SubmitSMModel;
import com.asset.cs.vfesmscinterface.models.TLVModel;
import com.asset.cs.vfesmscinterface.utils.BufferByte;
import com.asset.cs.vfesmscinterface.utils.Gsm7BitCharset;
import com.asset.cs.vfesmscinterface.utils.Util;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mohamed.morsy
 */
public class SubmitSMParser extends CommandRequestParser {

	@Override
	protected SubmitSMModel parsePDUBody(HeaderModel headerModel, BufferByte pdu) {

		SubmitSMModel submitSMModel = new SubmitSMModel(headerModel);
		try {

			submitSMModel.setServiceType(pdu.removeCString());
			submitSMModel.setSource(new AddressModel(pdu));
			submitSMModel.setDestination(new AddressModel(pdu));
			submitSMModel.setEsmClass(pdu.removeByte());
			submitSMModel.setProtocolId(pdu.removeByte());
			submitSMModel.setPriority(pdu.removeByte());

			String tmp = pdu.removeCString();
			if (tmp.length() > 0) {
				try {
					submitSMModel.setScheduleDeliveryTime(SMPPDate.parseSMPPDate(tmp));
				} catch (InvalidDateFormatException e) {
					CommonLogger.businessLogger.error(e.getMessage());
					CommonLogger.errorLogger.error(e.getMessage(), e);
				}
			}

			tmp = pdu.removeCString();
			if (tmp.length() > 0) {
				try {
					submitSMModel.setValidityPeriod(SMPPDate.parseSMPPDate(tmp));
				} catch (InvalidDateFormatException e) {
					CommonLogger.businessLogger.error(e.getMessage());
					CommonLogger.errorLogger.error(e.getMessage(), e);
				}
			}

			submitSMModel.setRegisteredDelivery(pdu.removeByte());
			submitSMModel.setReplaceIfPresent(pdu.removeByte());
			submitSMModel.setDataCoding(pdu.removeByte());
			submitSMModel.setSmDefaultMsgId(pdu.removeByte());

			// to convert from signed byte to unsigned integer
			int smLength = Util.convertSignedByteToInt(pdu.removeByte());
			submitSMModel.setSmLength(smLength);

			try {
				String encoding = null;
				if (submitSMModel.getDataCoding() == Data.DCS_Arabic) { // validation skipped
					encoding = Data.ENC_UTF16;
					submitSMModel.setMessage(pdu.removeString(submitSMModel.getSmLength(), encoding));
				} else {
					Gsm7BitCharset GSM7Bit = new Gsm7BitCharset("GSM7Bit", null);
					String Message1 = new String(GSM7Bit
							.decode(ByteBuffer.wrap(pdu.removeString(submitSMModel.getSmLength(), encoding).getBytes()))
							.array());
					submitSMModel.setMessage(Message1);
				}

			} catch (UnsupportedEncodingException e) {
				CommonLogger.businessLogger.error(e.getMessage());
				CommonLogger.errorLogger.error(e.getMessage(), e);
			}

			while (pdu.hasNext()) {
				Short tag = pdu.removeShort();
				parseTLV(submitSMModel, tag, pdu);
			}
		} catch (ParseException ex) {
			submitSMModel.setErrorCommadStatus(Data.ESME_ROPTPARNOTALLWD);
		} catch (Exception ex) {
			CommonLogger.errorLogger.error("general exception" + ex.getMessage(), ex);
			submitSMModel.getParserStatus().append("general exception" + ex.getMessage());
		}

		return submitSMModel;
	}

	private void parseTLV(SubmitSMModel submitSMModel, short tag, BufferByte pdu) throws ParseException {

		String msg = " already sent, will override this value.";
		TLVModel tlvModel = null;
		switch (tag) {
		case Data.OPT_PAR_USER_MSG_REF:
			if (submitSMModel.getUserMessageReference() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getUserMessageReference() + msg);
			}
			submitSMModel.setUserMessageReference(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getUserMessageReference().getLength(), TLVModelValueType.SHORT,
					submitSMModel.getUserMessageReference().getValue());
			break;
		case Data.OPT_PAR_SRC_PORT:
			if (submitSMModel.getSourcePort() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSourcePort() + msg);
			}
			submitSMModel.setSourcePort(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getSourcePort().getLength(), TLVModelValueType.SHORT,
					submitSMModel.getSourcePort().getValue());
			break;
		case Data.OPT_PAR_SRC_ADDR_SUBUNIT:
			if (submitSMModel.getSourceAddrSubunit() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSourceAddrSubunit() + msg);
			}
			submitSMModel.setSourceAddrSubunit(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getSourceAddrSubunit().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getSourceAddrSubunit().getValue());
			break;
		case Data.OPT_PAR_DST_PORT:
			if (submitSMModel.getDestinationPort() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getDestinationPort() + msg);
			}
			submitSMModel.setDestinationPort(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getDestinationPort().getLength(), TLVModelValueType.SHORT,
					submitSMModel.getDestinationPort().getValue());
			break;
		case Data.OPT_PAR_DST_ADDR_SUBUNIT:
			if (submitSMModel.getDestAddrSubunit() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getDestAddrSubunit() + msg);
			}
			submitSMModel.setDestAddrSubunit(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getDestAddrSubunit().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getDestAddrSubunit().getValue());
			break;
		case Data.OPT_PAR_SAR_MSG_REF_NUM:
			if (submitSMModel.getSarMsgRefNum() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSarMsgRefNum() + msg);
			}
			// as per requirement, we will not send this parameter as is
			submitSMModel.setSarMsgRefNum(pdu);
			return;
		case Data.OPT_PAR_SAR_TOT_SEG:
			if (submitSMModel.getSarTotalSegments() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSarTotalSegments() + msg);
			}
			// as per requirement, we will not send this parameter as is
			submitSMModel.setSarTotalSegments(pdu);
			return;
		case Data.OPT_PAR_SAR_SEG_SNUM:
			if (submitSMModel.getSarSegmentsSeqnum() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSarSegmentsSeqnum() + msg);
			}
			// as per requirement, we will not send this parameter as is
			submitSMModel.setSarSegmentsSeqnum(pdu);
			return;
		case Data.OPT_PAR_MORE_MSGS:
			if (submitSMModel.getMoreMessagesToSend() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getMoreMessagesToSend() + msg);
			}
			submitSMModel.setMoreMessagesToSend(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getMoreMessagesToSend().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getMoreMessagesToSend().getValue());
			break;
		case Data.OPT_PAR_PAYLOAD_TYPE:
			if (submitSMModel.getPayloadType() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getPayloadType() + msg);
			}
			// as per requirement, we will not send this parameter as is
			submitSMModel.setPayloadType(pdu);
			return;
		case Data.OPT_PAR_MSG_PAYLOAD:
			if (submitSMModel.getMessagePayload() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getMessagePayload() + msg);
			}
			// as per requirement, we will not send this parameter as is
			submitSMModel.setMessagePayload(pdu);

			String tmp = submitSMModel.getMessagePayload().getValue();
			if (submitSMModel.getDataCoding() == Data.DCS_Arabic) { // validation skipped
				try {
					submitSMModel.getMessagePayload().setValue(new String(tmp.getBytes(), Data.ENC_UTF16));
				} catch (UnsupportedEncodingException e) {
					CommonLogger.businessLogger.error(e.getMessage());
					CommonLogger.errorLogger.error(e.getMessage(), e);
				}
			} else {
				Gsm7BitCharset GSM7Bit = new Gsm7BitCharset("GSM7Bit", null);
				String Message1 = new String(GSM7Bit.decode(ByteBuffer.wrap(tmp.getBytes())).array());
				submitSMModel.getMessagePayload().setValue(Message1);
			}
			return;
		case Data.OPT_PAR_PRIV_IND:
			if (submitSMModel.getPrivacyIndicator() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getPrivacyIndicator() + msg);
			}
			submitSMModel.setPrivacyIndicator(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getPrivacyIndicator().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getPrivacyIndicator().getValue());
			break;
		case Data.OPT_PAR_CALLBACK_NUM:
			if (submitSMModel.getCallbackNum() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getCallbackNum() + msg);
			}
			submitSMModel.setCallbackNum(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getCallbackNum().getLength(), TLVModelValueType.STRING,
					submitSMModel.getCallbackNum().getValue());
			break;
		case Data.OPT_PAR_CALLBACK_NUM_PRES_IND:
			if (submitSMModel.getCallbackNumPresInd() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getCallbackNumPresInd() + msg);
			}
			submitSMModel.setCallbackNumPresInd(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getCallbackNumPresInd().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getCallbackNumPresInd().getValue());
			break;
		case Data.OPT_PAR_CALLBACK_NUM_ATAG:
			if (submitSMModel.getCallbackNumAtag() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getCallbackNumAtag() + msg);
			}
			submitSMModel.setCallbackNumAtag(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getCallbackNumAtag().getLength(), TLVModelValueType.STRING,
					submitSMModel.getCallbackNumAtag().getValue());
			break;
		case Data.OPT_PAR_SRC_SUBADDR:
			if (submitSMModel.getSourceSubaddress() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSourceSubaddress() + msg);
			}
			submitSMModel.setSourceSubaddress(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getSourceSubaddress().getLength(), TLVModelValueType.STRING,
					submitSMModel.getSourceSubaddress().getValue());
			break;
		case Data.OPT_PAR_DEST_SUBADDR:
			if (submitSMModel.getDestSubaddress() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getDestSubaddress() + msg);
			}
			submitSMModel.setDestSubaddress(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getDestSubaddress().getLength(), TLVModelValueType.STRING,
					submitSMModel.getDestSubaddress().getValue());
			break;
		case Data.OPT_PAR_USER_RESP_CODE:
			if (submitSMModel.getUserResponseCode() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getUserResponseCode() + msg);
			}
			submitSMModel.setUserResponseCode(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getUserResponseCode().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getUserResponseCode().getValue());
			break;
		case Data.OPT_PAR_DISPLAY_TIME:
			if (submitSMModel.getDisplayTime() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getDisplayTime() + msg);
			}
			submitSMModel.setDisplayTime(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getDisplayTime().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getDisplayTime().getValue());
			break;
		case Data.OPT_PAR_SMS_SIGNAL:
			if (submitSMModel.getSmsSignal() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getSmsSignal() + msg);
			}
			submitSMModel.setSmsSignal(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getSmsSignal().getLength(), TLVModelValueType.SHORT,
					submitSMModel.getSmsSignal().getValue());
			break;
		case Data.OPT_PAR_MS_VALIDITY:
			if (submitSMModel.getMsValidity() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getMsValidity() + msg);
			}
			submitSMModel.setMsValidity(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getMsValidity().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getMsValidity().getValue());
			break;
		case Data.OPT_PAR_MS_MSG_WAIT_FACILITIES:
			if (submitSMModel.getMsMsgWaitFacilities() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getMsMsgWaitFacilities() + msg);
			}
			submitSMModel.setMsMsgWaitFacilities(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getMsMsgWaitFacilities().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getMsMsgWaitFacilities().getValue());
			break;
		case Data.OPT_PAR_NUM_MSGS:
			if (submitSMModel.getNumberOfMessages() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getNumberOfMessages() + msg);
			}
			submitSMModel.setNumberOfMessages(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getNumberOfMessages().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getNumberOfMessages().getValue());
			break;
		case Data.OPT_PAR_ALERT_ON_MSG_DELIVERY:
			if (submitSMModel.getAlertOnMessageDelivery() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getAlertOnMessageDelivery() + msg);
			}
			submitSMModel.setAlertOnMessageDelivery(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getAlertOnMessageDelivery().getLength(),
					TLVModelValueType.NOVALUE, submitSMModel.getAlertOnMessageDelivery().getValue());
			break;
		case Data.OPT_PAR_LANG_IND:
			if (submitSMModel.getLanguageIndicator() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getLanguageIndicator() + msg);
			}
			submitSMModel.setLanguageIndicator(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getLanguageIndicator().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getLanguageIndicator().getValue());
			break;
		case Data.OPT_PAR_ITS_REPLY_TYPE:
			if (submitSMModel.getItsReplyType() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getItsReplyType() + msg);
			}
			submitSMModel.setItsReplayType(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getItsReplyType().getLength(), TLVModelValueType.BYTE,
					submitSMModel.getItsReplyType().getValue());
			break;
		case Data.OPT_PAR_ITS_SESSION_INFO:
			if (submitSMModel.getItsSessionInfo() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getItsSessionInfo() + msg);
			}
			submitSMModel.setItsSessionInfo(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getItsSessionInfo().getLength(), TLVModelValueType.STRING,
					submitSMModel.getItsSessionInfo().getValue());
			break;
		case Data.OPT_PAR_USSD_SER_OP:
			if (submitSMModel.getUssdServiceOp() != null) {
				CommonLogger.businessLogger.debug(submitSMModel.getUssdServiceOp() + msg);
			}
			submitSMModel.setUssdServiceOp(pdu);
			tlvModel = new TLVModel(tag, submitSMModel.getUssdServiceOp().getLength(), TLVModelValueType.STRING,
					submitSMModel.getUssdServiceOp().getValue());
			break;
		default:
			String defaultmsg = "error not allowed optional param: " + tag;
			CommonLogger.businessLogger.error(defaultmsg);
			CommonLogger.errorLogger.error(defaultmsg);
			submitSMModel.getParserStatus().append(defaultmsg);
		}

		submitSMModel.getTlvs().put(tlvModel.getTag(), tlvModel);
	}

}
