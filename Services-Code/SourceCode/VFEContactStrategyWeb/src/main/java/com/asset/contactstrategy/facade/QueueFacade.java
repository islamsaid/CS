/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.facade;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.webmodels.QueueWebModel;
import com.asset.contactstrategy.webmodels.SMSCWebModel;
import com.asset.contactstrategy.webmodels.UserWebModel;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Yomna Naser
 */
public class QueueFacade {

    public ArrayList<QueueWebModel> getApplicationQueues() throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "Starting [getApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting getApplicationQueues")
                    .build());
            MainService mainService = new MainService();
            ArrayList<QueueModel> queueModels = mainService.getApplicationQueues();

            ArrayList<QueueWebModel> queueWebModels = new ArrayList<>();

            for (QueueModel queueModel : queueModels) {

                QueueWebModel queueWebModel = convertModelToWebModel(queueModel);

                //get creatorName using creator
                if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean queueHasChild = false;
                    for (QueueModel child : queueModels) {
                        if (queueModel.getAppId() == child.getAppId() && queueModel.getVersionId() != child.getVersionId()) {
                            //operation has edited version
                            queueHasChild = true;
                            queueWebModel.setEnableDelete(true);
                            queueWebModel.setEnableEdit(false);
                            break;
                        }
                    }
                    if (!queueHasChild) {
                        queueWebModel.setEnableDelete(true);
                        queueWebModel.setEnableEdit(true);
                    }
                } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                    queueWebModel.setEnableDelete(true);
                    queueWebModel.setEnableEdit(true);

                } else {

                    queueWebModel.setEnableDelete(false);
                    queueWebModel.setEnableEdit(false);

                }
                queueWebModels.add(queueWebModel);
            }
//            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "End [getApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End getApplicationQueues")
                    .build());
            return queueWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_QUEUES);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }

    }

    /*  public ArrayList<QueueWebModel> getApplicationQueues(int queueType) throws CommonException {

        try {
            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "Starting [getApplicationQueues]...");
            MainService mainService = new MainService();
            ArrayList<QueueModel> queueModels = mainService.getApplicationQueues(queueType);

            ArrayList<QueueWebModel> queueWebModels = new ArrayList<>();

            for (QueueModel queueModel : queueModels) {

                QueueWebModel queueWebModel = convertModelToWebModel(queueModel);

                //get creatorName using creator
                if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //check if operation has child
                    boolean queueHasChild = false;
                    for (QueueModel child : queueModels) {
                        if (queueModel.getAppId() == child.getAppId() && queueModel.getVersionId()!= child.getVersionId()) {
                            //operation has edited version
                            queueHasChild = true;
                            queueWebModel.setEnableDelete(true);
                            queueWebModel.setEnableEdit(false);
                            break;
                        }
                    }
                    if (!queueHasChild) {
                        queueWebModel.setEnableDelete(true);
                        queueWebModel.setEnableEdit(true);
                    }
                } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                    queueWebModel.setEnableDelete(true);
                    queueWebModel.setEnableEdit(true);

                } else {

                    queueWebModel.setEnableDelete(false);
                    queueWebModel.setEnableEdit(false);

                }
                queueWebModels.add(queueWebModel);
            }
            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "End [getApplicationQueues]...");
            return queueWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_QUEUES);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }

    }*/
    public QueueModel convertWebModelToModel(QueueWebModel queueWebModel) {

        QueueModel queueModel = new QueueModel();
        queueModel.setAppId(queueWebModel.getAppId());
        queueModel.setAppName(queueWebModel.getAppName());
        queueModel.setCreator(queueWebModel.getCreator());
        queueModel.setCreatorName(queueWebModel.getCreatorName());
        queueModel.setLastModifiedBy(queueWebModel.getLastModifiedBy());
        queueModel.setLastModifiedByName(queueWebModel.getLastModifiedByName());
        queueModel.setDatabaseUrl(queueWebModel.getDatabaseUrl());
        queueModel.setDequeuePoolSize(queueWebModel.getDequeuePoolSize());
        queueModel.setVersionId(queueWebModel.getVersionId());
        queueModel.setQueueName(queueWebModel.getQueueName());
        queueModel.setSchemaName(queueWebModel.getSchemaName());
        queueModel.setSchemaPassword(queueWebModel.getSchemaPassword());
        queueModel.setSenderPoolSize(queueWebModel.getSenderPoolSize());
        queueModel.setStatus(queueWebModel.getStatus());
        queueModel.setThreshold(queueWebModel.getThreshold());
        queueModel.setQueueType(queueWebModel.getQueueType());
        queueModel.setTimeWindowFlag(queueWebModel.isTimeWindowFlag());
        queueModel.setTimeWindowFromHour(queueWebModel.getTimeWindowFromHour());
        queueModel.setTimeWindowFromMin(queueWebModel.getTimeWindowFromMin());
        queueModel.setTimeWindowToHour(queueWebModel.getTimeWindowToHour());
        queueModel.setTimeWindowToMin(queueWebModel.getTimeWindowToMin());
        queueModel.setEditedVersionDescription(queueWebModel.getEditedVersionDescription());
        queueModel.setTableSpaceName(queueWebModel.getTableSpaceName());
        return queueModel;

    }

    public QueueWebModel convertModelToWebModel(QueueModel queueModel) {
        QueueWebModel queueWebModel = new QueueWebModel();
        queueWebModel.setAppId(queueModel.getAppId());
        queueWebModel.setAppName(queueModel.getAppName());
        queueWebModel.setCreator(queueModel.getCreator());
        queueWebModel.setCreatorName(queueModel.getCreatorName());
        queueWebModel.setLastModifiedBy(queueModel.getLastModifiedBy());
        queueWebModel.setLastModifiedByName(queueModel.getLastModifiedByName());
        queueWebModel.setDatabaseUrl(queueModel.getDatabaseUrl());
        queueWebModel.setDequeuePoolSize(queueModel.getDequeuePoolSize());
        queueWebModel.setVersionId(queueModel.getVersionId());
        queueWebModel.setQueueName(queueModel.getQueueName());
        queueWebModel.setSchemaName(queueModel.getSchemaName());
        queueWebModel.setSchemaPassword(queueModel.getSchemaPassword());
        queueWebModel.setSenderPoolSize(queueModel.getSenderPoolSize());
        queueWebModel.setStatus(queueModel.getStatus());
        queueWebModel.setThreshold(queueModel.getThreshold());
        queueWebModel.setQueueType(queueModel.getQueueType());
        queueWebModel.setTimeWindowFlag(queueModel.isTimeWindowFlag());
        queueWebModel.setTimeWindowFromHour(queueModel.getTimeWindowFromHour());
        queueWebModel.setTimeWindowFromMin(queueModel.getTimeWindowFromMin());
        queueWebModel.setTimeWindowToHour(queueModel.getTimeWindowToHour());
        queueWebModel.setTimeWindowToMin(queueModel.getTimeWindowToMin());

        if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
            queueWebModel.setStatusLabel(GeneralConstants.STATUS_APPROVED);
        } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
            queueWebModel.setStatusLabel(GeneralConstants.STATUS_PENDING);
        } else {
            queueWebModel.setStatusLabel(GeneralConstants.STATUS_PENDING_FOR_DELETION);
        }
        queueWebModel.setEditedVersionDescription(queueModel.getEditedVersionDescription());
        //get creatorName
        return queueWebModel;
    }

    public ArrayList<SMSCWebModel> getApplicationQueuesSMSCs(long queueID) throws CommonException {
        try {
            MainService mainService = new MainService();
            ArrayList<SMSCModel> smscModels = mainService.getApplicationQueuesSMSCs(queueID);
            ArrayList<SMSCWebModel> smscWebModels = new ArrayList<>();
            for (SMSCModel smscModel : smscModels) {

                SMSCFacade smscFacade = new SMSCFacade(new UserModel());
                SMSCWebModel smscWebModel = smscFacade.convertModelToWebModel(smscModel);
                smscWebModels.add(smscWebModel);
            }

            return smscWebModels;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.GET_QUEUE_SMSC);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void createApplicationQueue(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, UserWebModel userWebModel) throws CommonException {
        try {

//            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "Starting [createApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting createApplicationQueue")
                    .build());

            QueueModel queueModel = convertWebModelToModel(queueWebModel);
            ArrayList<SMSCModel> smscModels = new ArrayList<>();
            SMSCFacade smscFacade = new SMSCFacade(new UserModel());
            for (SMSCWebModel smscWebModel : smscWebModels) {
                SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                smscModels.add(smscModel);
            }
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                queueModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
            } else if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                queueModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
            }
            MainService mainService = new MainService();
            mainService.createApplicationQueue(queueModel, smscModels);
            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
//            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "End [createApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End createApplicationQueue")
                    .build());
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueue]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.CREATE_QUEUE);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void editApplicationQueue(QueueWebModel queueWebModel, ArrayList<SMSCWebModel> smscWebModels, UserWebModel userWebModel) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "Starting [editApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting editApplicationQueue")
                    .build());
            QueueModel queueModel = convertWebModelToModel(queueWebModel);
            ArrayList<SMSCModel> smscModels = new ArrayList<>();
            SMSCFacade smscFacade = new SMSCFacade(new UserModel());
            for (SMSCWebModel smscWebModel : smscWebModels) {
                SMSCModel smscModel = smscFacade.convertWebModelToModel(smscWebModel);
                smscModels.add(smscModel);
            }

            MainService mainService = new MainService();

            if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {

                if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    queueModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                    mainService.insertChildApplicationQueue(queueModel, smscModels);
                } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    mainService.updateApplicationQueue(queueModel, smscModels);
                }
            } else if (userWebModel.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {

                if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    mainService.updateApplicationQueue(queueModel, smscModels);
                } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    ArrayList<QueueModel> queueModels = mainService.getApplicationQueues();
                    if (hasParentOrChild(queueModel, queueModels)) {
                        mainService.updateParentAndDeleteChildApplicationQueue(queueModel, smscModels);
                    } else {
                        CommonLogger.businessLogger.info("Edit Queue: First Creation by Business user, "
                                + "edit by operational user and approve Queue ");
                        queueModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                        mainService.updateApplicationQueue(queueModel, smscModels);
                    }
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
//            CommonLogger.businessLogger.debug(QueueFacade.class.getName() + " || " + "End [editApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "End editApplicationQueue")
                    .build());

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editApplicationQueue]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.EDIT_QUEUE);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }

    }

    public boolean hasParentOrChild(QueueModel queueModel, ArrayList<QueueModel> queueModels) {

        for (QueueModel parent : queueModels) {
            if (queueModel.getAppId() == parent.getAppId() && queueModel.getVersionId() != parent.getVersionId()) {
                //queue has parent
                return true;
            }
        }
        return false;
    }

    public void deleteApplicationQueue(QueueWebModel queueWebModel, UserWebModel user) throws CommonException {

        try {
            QueueModel queueModel = convertWebModelToModel(queueWebModel);
            MainService mainService = new MainService();
            if (user.getUserType() == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    //delete
                    mainService.deleteApplicationQueue(queueModel);
                } else if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //lock app queue
                    mainService.lockApplicationQueuesForDeletion(queueModel);
                }
            } else if (user.getUserType() == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    //delete app queue
                    mainService.deleteApplicationQueue(queueModel);
                } else if (queueModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    //delete app queue and childs
                    mainService.deleteParentAndChildApplicationQueue(queueModel);
                }
                com.asset.contactstrategy.common.utils.Utility.CountersReloader();
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApplicationQueue]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.DELETE_QUEUE);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }

    }

    public boolean checkApplicationQueueNameExists(String name) throws CommonException {
        boolean queueExists = false;
        try {
            MainService mainService = new MainService();
            queueExists = mainService.getApplicationQueueByName(name);
            return queueExists;
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueByName]" + ex);
            throw ex;
        }
    }

    public void approveApplicationQueue(QueueWebModel queueModel) throws CommonException {

        try {
            MainService mainService = new MainService();
            ArrayList<QueueModel> queueModels = mainService.getApplicationQueues();

            if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {

                if (hasParentOrChild(queueModel, queueModels)) {
                    mainService.updateParentAndDeleteChildApplicationQueue(queueModel, (ArrayList<SMSCModel>) queueModel.getSmscModels());
                } else {
                    queueModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                    mainService.updateApplicationQueue(queueModel, (ArrayList<SMSCModel>) queueModel.getSmscModels());
                }
            } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                if (hasParentOrChild(queueModel, queueModels)) {
                    mainService.deleteParentAndChildApplicationQueue(queueModel);
                } else {
                    mainService.deleteApplicationQueue(queueModel);
                }
            }
            com.asset.contactstrategy.common.utils.Utility.CountersReloader();
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApplicationQueue]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.APPROVE_QUEUE);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public void rejectApplicationQueue(QueueWebModel queueModel) throws CommonException {

        try {
            MainService mainService = new MainService();
            if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                mainService.deleteApplicationQueue(queueModel);
            } else if (queueModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                queueModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                mainService.updateApplicationQueue(queueModel, (ArrayList<SMSCModel>) queueModel.getSmscModels());
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectApplicationQueue]" + ex);
            MOMErrorsModel mErrorsModel = getMOMErrorModel(ex.getErrorCode(), Constants.REJECT_QUEUE);
            Utility.sendMOMAlarem(mErrorsModel);
            throw ex;
        }
    }

    public MOMErrorsModel getMOMErrorModel(String errorCode, String methodName) {
        MOMErrorsModel mErrorsModel = new MOMErrorsModel();
        mErrorsModel.setEngineSrcID(GeneralConstants.SRC_ID_WEB_INTERFACE);
        mErrorsModel.setEntryDate(new Date());
        mErrorsModel.setErrorCode(errorCode);
        mErrorsModel.setModuleName(Constants.QUEUE_MODULE);
        mErrorsModel.setFunctionName(methodName);
        mErrorsModel.setPreceivedSeverity(GeneralConstants.MOM_ERROR_MESSAGE_SEVERITY_HIGH);
        return mErrorsModel;
    }
}
