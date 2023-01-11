/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author rania.magdy
 */
public class SMSCFacade {

    private int userType;
    private UserModel userModel;

    public SMSCFacade(UserModel userModel1) {
        userModel = userModel1;
        userType = userModel1.getUserType();
    }

    public ArrayList<SMSCWebModel> getSMSCs() throws CommonException {
        MainService mainService = new MainService();

        try {
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "Starting [getSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getSMSCs")
                    .build());
            ArrayList<SMSCModel> smscModels = mainService.getSMSCs();

            ArrayList<SMSCWebModel> smscWebModels = new ArrayList<>();

            for (SMSCModel smscModel : smscModels) {

                SMSCWebModel smscWebModel = convertModelToWebModel(smscModel);

                if (smscModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean smscHasChild = false;
                    for (SMSCModel child : smscModels) {
                        if (smscModel.getSMSCid() == child.getSMSCid() && smscModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            smscHasChild = true;
                            smscWebModel.setEnableDelete(true);
                            smscWebModel.setEnableEdit(false);
                            smscWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                            break;
                        }
                    }
                    if (!smscHasChild) {
                        smscWebModel.setEnableDelete(true);
                        smscWebModel.setEnableEdit(true);
                        smscWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
                    }
                } else if (smscModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                    smscWebModel.setEnableDelete(true);
                    smscWebModel.setEnableEdit(true);
                    smscWebModel.setStatusName(GeneralConstants.STATUS_PENDING);
                } else {

                    smscWebModel.setEnableDelete(false);
                    smscWebModel.setEnableEdit(false);
                    smscWebModel.setStatusName(GeneralConstants.STATUS_PENDING_FOR_DELETION);
                }
                smscWebModels.add(smscWebModel);
            }
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "End [getSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getSMSCs")
                    .build());
            return smscWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getSMSCs]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }

    }

    public void editSMSC(SMSCWebModel sMSCWebModel) throws CommonException {
        MainService mainService = new MainService();
        sMSCWebModel.setCreator(userModel.getId());

        try {
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "Starting [editSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting editSMSC")
                    .build());

            SMSCModel sMSCModel = convertWebModelToModel(sMSCWebModel);

            SMSCModel returnedModel = mainService.retrieveConnectedSMSC(sMSCModel);
            if (userType == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {//business user
                if (returnedModel == null) {
                    if (sMSCModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.editEditedSMSC(sMSCModel);
                    } else {
                        mainService.editSMSC(sMSCModel);
                    }
                } else { //edit editedVersion
                    mainService.editEditedSMSC(sMSCModel);
                }
            } else if (userType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//Operational user
                sMSCModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                if (returnedModel == null) {
                    mainService.editEditedSMSC(sMSCModel); //and change to approved if pending
                } else {
                    //delete returnedModel
                    returnedModel.setCreator(sMSCModel.getCreator());
                    returnedModel.setDescription(sMSCModel.getDescription());
                    returnedModel.setIp(sMSCModel.getIp());
                    returnedModel.setPassword(sMSCModel.getPassword());
                    returnedModel.setPort(sMSCModel.getPort());
                    returnedModel.setSMSCname(sMSCModel.getSMSCname());
                    returnedModel.setStatus(sMSCModel.getStatus());
                    returnedModel.setSystemType(sMSCModel.getSystemType());
                    returnedModel.setUsername(sMSCModel.getUsername());
                    mainService.deleteSMSC(sMSCModel);//delete edited smsc
                    mainService.editEditedSMSC(returnedModel);//and change in original smsc to approve
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }

//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "End [editSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End editSMSC")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editSMSC]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.EDIT_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public SMSCWebModel createSMSC(SMSCWebModel sMSCWebModel) throws CommonException {
        MainService mainService = new MainService();
        sMSCWebModel.setCreator(userModel.getId());
        try {
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "Starting [createSMSC]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting createSMSC")
                    .build());

            SMSCModel sMSCModel = convertWebModelToModel(sMSCWebModel);
            //check unique name
            SMSCModel model = mainService.getSMSCByName(sMSCModel.getSMSCname().trim());
            if (model == null) {
                if (userType == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {//business user
                    sMSCModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                } else if (userType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//operational user
                    sMSCModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                }
                sMSCModel = mainService.createSMSC(sMSCModel);
                if (userType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                    com.asset.contactstrategy.common.utils.Utility.CountersReloader();
                }
                sMSCWebModel = convertModelToWebModel(sMSCModel);
//                CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "End [createSMSC]...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End createSMSC")
                        .build());
                return sMSCWebModel;
            } else {
                CommonLogger.errorLogger.error("Getting Caught Exception with Error code :"
                        + ErrorCodes.UNIQUE_NAME + "---->  for [createSMSC]" + " Name of SMSC is used before");
                throw new CommonException("Name of SMSC is used before", ErrorCodes.UNIQUE_NAME);
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createSMSC]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.CREATE_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void deleteSMSC(SMSCWebModel sMSCWebModel) throws CommonException {
        MainService mainService = new MainService();
        sMSCWebModel.setCreator(userModel.getId());
        try {
            SMSCModel sMSCModel = convertWebModelToModel(sMSCWebModel);
            SMSCModel returnedModel = mainService.retrieveConnectedSMSC(sMSCModel);
            if (userType == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {//business
                if (returnedModel != null) {
                    //lw state bta3 returned child pending
                    //convert both for pending for deletion
                    if (returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.ChangeSMSCStatusToDelete(sMSCModel);
                        //   mainService.ChangeSMSCStatusToDelete(returnedModel); //comment
                    } //lw state bta3 returned model approved yb2a d parent msh child
                    //delete directly
                    else if (returnedModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE || returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {//aw pending for deletion
                        mainService.deleteSMSC(sMSCModel);
                    }
                } else {
                    //do not has child 
                    //convert for pending for deletion

                    if (sMSCModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.deleteSMSC(sMSCModel);
                    } else {
                        mainService.ChangeSMSCStatusToDelete(sMSCModel);
                    }

                }
            } else if (userType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//operational
                //lw state bta3 returned child pending
                //convert both for pending for deletion
                if (returnedModel != null) {
                    if (returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        mainService.deleteParentAndChildSMSC(sMSCModel, returnedModel);
                    } //lw state bta3 returned model approved yb2a d parent msh child
                    //delete directly
                    else if (returnedModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE || returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) { //aw pending for deletion
                        mainService.deleteSMSC(sMSCModel);
                    }
                } else {
                    mainService.deleteSMSC(sMSCModel);
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteSMSC]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.DELETE_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void approveSMSCOperation(SMSCWebModel sMSCWebModel) throws CommonException {
        MainService mainService = new MainService();
        sMSCWebModel.setCreator(userModel.getId());
        try {
            SMSCModel sMSCModel = convertWebModelToModel(sMSCWebModel);
            SMSCModel returnedModel = mainService.retrieveConnectedSMSC(sMSCModel);

            if (sMSCModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                sMSCModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                if (returnedModel == null) {
                    mainService.editEditedSMSC(sMSCModel); //and change to approved if pending
                } else {
                    //delete returnedModel
                    returnedModel.setCreator(sMSCModel.getCreator());
                    returnedModel.setDescription(sMSCModel.getDescription());
                    returnedModel.setIp(sMSCModel.getIp());
                    returnedModel.setPassword(sMSCModel.getPassword());
                    returnedModel.setPort(sMSCModel.getPort());
                    returnedModel.setSMSCname(sMSCModel.getSMSCname());
                    returnedModel.setStatus(sMSCModel.getStatus());
                    returnedModel.setSystemType(sMSCModel.getSystemType());
                    returnedModel.setUsername(sMSCModel.getUsername());
                    mainService.deleteSMSC(sMSCModel);//delete edited smsc
                    mainService.editEditedSMSC(returnedModel);//and change in original smsc to approve
                }
            } else if (sMSCModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                if (returnedModel == null) {
                    mainService.deleteSMSC(sMSCModel);
                    return;
                }
                if (returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    mainService.deleteSMSC(sMSCModel);
                    mainService.deleteSMSC(returnedModel);
                } //lw state bta3 returned model approved yb2a d parent msh child
                //delete directly
                else if (returnedModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE || returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) { //aw pending for deletion
                    mainService.deleteSMSC(sMSCModel);
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveSMSCOperation]", ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.APPROVE_SMSC_OPERATION);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public ArrayList<SMSCWebModel> getApprovedSMSCs() throws CommonException {
        MainService mainService = new MainService();

        try {
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "Starting [getApprovedSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getApprovedSMSCs")
                    .build());
            ArrayList<SMSCModel> smscModels = mainService.getApprovedSMSCs();

            ArrayList<SMSCWebModel> smscWebModels = new ArrayList<>();

            for (SMSCModel smscModel : smscModels) {

                SMSCWebModel smscWebModel = convertModelToWebModel(smscModel);
                smscWebModels.add(smscWebModel);
            }
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "End [getApprovedSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getApprovedSMSCs")
                    .build());
            return smscWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApprovedSMSCs]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_APPROVED_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public SMSCModel convertWebModelToModel(SMSCWebModel smscWebModel) {
        SMSCModel smscModel = new SMSCModel();
        smscModel.setCreator(smscWebModel.getCreator());
        smscModel.setVersionId(smscWebModel.getVersionId());
        smscModel.setIp(smscWebModel.getIp());
        smscModel.setPassword(smscWebModel.getPassword());
        smscModel.setPort(smscWebModel.getPort());
        smscModel.setSMSCid(smscWebModel.getSMSCid());
        smscModel.setSMSCname(smscWebModel.getSMSCname());
        smscModel.setStatus(smscWebModel.getStatus());
        smscModel.setSystemType(smscWebModel.getSystemType());
        smscModel.setUsername(smscWebModel.getUsername());
        smscModel.setDescription(smscWebModel.getDescription());
        smscModel.setWindowSize(smscWebModel.getWindowSize());
        smscModel.setThroughput(smscWebModel.getThroughput());
        return smscModel;
    }

    public SMSCWebModel convertModelToWebModel(SMSCModel smscModel) {
        SMSCWebModel smscWebModel = new SMSCWebModel();
        smscWebModel.setSMSCid(smscModel.getSMSCid());
        smscWebModel.setSMSCname(smscModel.getSMSCname());
        smscWebModel.setCreator(smscModel.getCreator());
        smscWebModel.setIp(smscModel.getIp());
        smscWebModel.setPassword(smscModel.getPassword());
        smscWebModel.setVersionId(smscModel.getVersionId());
        smscWebModel.setPort(smscModel.getPort());
        smscWebModel.setSystemType(smscModel.getSystemType());
        smscWebModel.setUsername(smscModel.getUsername());
        smscWebModel.setStatus(smscModel.getStatus());
        smscWebModel.setDescription(smscModel.getDescription());
        smscWebModel.setWindowSize(smscModel.getWindowSize());
        smscWebModel.setThroughput(smscModel.getThroughput());
        if (smscModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
            smscWebModel.setStatusName(GeneralConstants.STATUS_APPROVED);
        } else if (smscModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
            smscWebModel.setStatusName(GeneralConstants.STATUS_PENDING);
        } else {
            smscWebModel.setStatusName(GeneralConstants.STATUS_PENDING_FOR_DELETION);
        }
        return smscWebModel;
    }

    public MOMErrorsModel getMOMErrorModel(String errorCode, String methodName) {
        MOMErrorsModel mErrorsModel = new MOMErrorsModel();
        mErrorsModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        mErrorsModel.setEntryDate(new Date());
        mErrorsModel.setErrorCode(errorCode);
        mErrorsModel.setModuleName(Constants.SMSC_MODULE);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }

    public void rejectSMSCOperation(SMSCWebModel sMSCWebModel) throws CommonException {
        MainService mainService = new MainService();
        sMSCWebModel.setCreator(userModel.getId());

        try {
//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "Starting [rejectSMSCOperation]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting rejectSMSCOperation")
                    .build());

            SMSCModel sMSCModel = convertWebModelToModel(sMSCWebModel);

            //SMSCModel returnedModel = mainService.retrieveConnectedSMSC(sMSCModel);
            if (userType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {//Operational user
                if (sMSCModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                    sMSCModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                    mainService.editEditedSMSC(sMSCModel);//and change in original smsc to approve

                } else if (sMSCModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                    mainService.deleteSMSC(sMSCModel);
                }
            }

//            CommonLogger.businessLogger.debug(SMSCFacade.class.getName() + " || " + "End [rejectSMSCOperation]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End rejectSMSCOperation")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectSMSCOperation]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.EDIT_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

}
