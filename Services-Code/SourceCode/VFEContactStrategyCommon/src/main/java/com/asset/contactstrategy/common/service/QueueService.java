/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import com.asset.contactstrategy.common.dao.CommonDAO;
import com.asset.contactstrategy.common.dao.QueueDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.models.ServicesModel;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Yomna Naser
 */
public class QueueService {

    public ArrayList<QueueModel> getApplicationQueues(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueues Started").build());
            QueueDAO queueDAO = new QueueDAO();
            ArrayList<QueueModel> queueModels = queueDAO.getApplicationQueues(connection);
            for (QueueModel model : queueModels) {
                decryptSchemaPswd(model);
            }
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueues Ended").build());

            return queueModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    /* public ArrayList<QueueModel> getApplicationQueues(Connection connection, int queueType) throws CommonException {
        try {
            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueues]...");
            QueueDAO queueDAO = new QueueDAO();
            ArrayList<QueueModel> queueModels = queueDAO.getApplicationQueues(connection, queueType);
            for(QueueModel model:queueModels){
                decryptSchemaPswd(model);
            }
            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueues]...");
            return queueModels;
        } catch (CommonException e) {
            throw e;
        }
    }*/
    public HashMap<String, QueueModel> getHashedApplicationQueues(Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getHashedApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Started").build());

            QueueDAO queueDAO = new QueueDAO();
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getHashedApplicationQueues]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getHashedApplicationQueues Ended").build());

            HashMap<String, QueueModel> queueModels = queueDAO.getHashedApplicationQueues(connection);
            Iterator<QueueModel> i = queueModels.values().iterator();
            while (i.hasNext()) {
                decryptSchemaPswd(i.next());
            }
            return queueModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    public HashMap<String, QueueModel> getApplicationQueuesServiceAndStatusApproved(Connection connection, int queueType) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueuesServiceAndStatusApproved]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesServiceAndStatusApproved Started").build());

            QueueDAO queueDAO = new QueueDAO();
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueuesServiceAndStatusApproved]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesServiceAndStatusApproved Ended").build());

            HashMap<String, QueueModel> queueModels = queueDAO.getApplicationQueuesServiceAndStatusApproved(connection, queueType);
            Iterator<QueueModel> i = queueModels.values().iterator();
            while (i.hasNext()) {
                decryptSchemaPswd(i.next());
            }
            return queueModels;
        } catch (CommonException e) {
            throw e;
        }
    }

    public ArrayList<SMSCModel> getApplicationQueuesSMSCs(long queueID, Connection connection) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueuesSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesSMSCs Started").build());

            QueueDAO queueDAO = new QueueDAO();
            ArrayList<SMSCModel> smscModels = queueDAO.getApplicationQueuesSMSCs(queueID, connection);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueuesSMSCs]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesSMSCs Ended").build());

            return smscModels;
        } catch (CommonException e) {
            throw e;
        }
    }
//2595 update phase 1.6

    public void decryptSchemaPswd(QueueModel queueModel) throws CommonException {
        if (queueModel.getSchemaPassword() != null && !queueModel.getSchemaPassword().trim().isEmpty()) {
            String decryptedPswd = com.asset.contactstrategy.common.utils.Utility.decrypt(
                    queueModel.getSchemaPassword(),
                    com.asset.contactstrategy.common.defines.Defines.ENCRYPTION_KEY);
            queueModel.setSchemaPassword(decryptedPswd);
        }
    }

    public void encryptSchemaPswd(QueueModel queueModel) throws CommonException {
        if (queueModel.getSchemaPassword() != null && !queueModel.getSchemaPassword().trim().isEmpty()) {
            String encryptedPswd = com.asset.contactstrategy.common.utils.Utility.encrypt(
                    queueModel.getSchemaPassword(),
                    com.asset.contactstrategy.common.defines.Defines.ENCRYPTION_KEY);
            queueModel.setSchemaPassword(encryptedPswd);
        }
    }

    //end update 2595
    //create new application queue
    public void insertApplicationQueue(Connection connection, QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [insertApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Started").build());

            QueueDAO queueDAO = new QueueDAO();
            queueModel.setVersionId(CommonDAO.getNextId(connection, DBStruct.APPQUEUE.SEQUENCE));
            queueModel.setAppId(CommonDAO.getNextId(connection, DBStruct.APPQUEUE.SEQUENCE_SEC));
            encryptSchemaPswd(queueModel);
            queueDAO.insertApplicationQueue(connection, queueModel);
            queueDAO.insertApplicationQueueSMSC(connection, queueModel, smscModels);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [insertApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Ended").build());

        } catch (CommonException e) {
            decryptSchemaPswd(queueModel);
            throw e;
        }
    }

    public void updateApplicationQueue(Connection connection, QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [updateApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateApplicationQueue Started").build());

            QueueDAO queueDAO = new QueueDAO();
            encryptSchemaPswd(queueModel);
            queueDAO.updateApplicationQueue(connection, queueModel);
            queueDAO.insertApplicationQueueSMSC(connection, queueModel, smscModels);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [updateApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateApplicationQueue Ended").build());

        } catch (CommonException e) {
            decryptSchemaPswd(queueModel);
            throw e;
        }
    }

    public void insertChildApplicationQueue(Connection connection, QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [insertApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Started").build());

            QueueDAO queueDAO = new QueueDAO();
            queueModel.setVersionId(CommonDAO.getNextId(connection, DBStruct.APPQUEUE.SEQUENCE));
            encryptSchemaPswd(queueModel);
            queueDAO.insertApplicationQueue(connection, queueModel);
            queueDAO.insertApplicationQueueSMSC(connection, queueModel, smscModels);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [insertApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "insertApplicationQueue Ended").build());

        } catch (CommonException e) {
            decryptSchemaPswd(queueModel);
            throw e;
        }

    }

    public void updateParentAndDeleteChildApplicationQueue(Connection connection, QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [updateAndApprovePendingApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateAndApprovePendingApplicationQueue Started").build());

            QueueDAO queueDAO = new QueueDAO();
            queueDAO.deleteApplicationQueueSMSCs(connection, queueModel);
            encryptSchemaPswd(queueModel);
            Long id = queueDAO.updateParentApplicationQueue(connection, queueModel);
            queueModel.setVersionId(id);
            queueDAO.insertApplicationQueueSMSC(connection, queueModel, smscModels);
            queueDAO.deleteChildApplicationQueue(connection, queueModel);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [updateAndApprovePendingApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "updateAndApprovePendingApplicationQueue Ended").build());

        } catch (CommonException e) {
            decryptSchemaPswd(queueModel);
            throw e;
        }

    }

    public void lockApplicationQueueForDeletion(Connection connection, QueueModel queueModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [lockApplicationQueueForDeletion]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "lockApplicationQueueForDeletion Started").build());

            QueueDAO queueDAO = new QueueDAO();
            encryptSchemaPswd(queueModel);
            queueDAO.updateApplicationQueue(connection, queueModel);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [lockApplicationQueueForDeletion]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "lockApplicationQueueForDeletion Ended").build());

        } catch (CommonException e) {
            decryptSchemaPswd(queueModel);
            throw e;
        }
    }

    public void deleteApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {

        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [deleteApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueue Started").build());

            QueueDAO queueDAO = new QueueDAO();
            queueDAO.deleteApplicationQueueSMSCs(connection, queueModel);//delete smsc for queue
            queueDAO.deleteApplicationQueue(connection, queueModel);//Good? ok delete the queue
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [deleteApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteApplicationQueue Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public void deleteParentAndChildApplicationQueue(Connection connection, QueueModel queueModel) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [deleteParentAndChildApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteParentAndChildApplicationQueue Started").build());

            QueueDAO queueDAO = new QueueDAO();
            QueueModel childQueue = queueDAO.getChildApplicationQueue(connection, queueModel);
            queueDAO.deleteApplicationQueueSMSCs(connection, childQueue);//Delete smsc for the child
            queueDAO.deleteApplicationQueueSMSCs(connection, queueModel);//delete smsc for queue
            queueDAO.deleteParentAndChildApplicationQueue(connection, queueModel);//Good? ok delete the queue parent & child
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [deleteParentAndChildApplicationQueue]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "deleteParentAndChildApplicationQueue Ended").build());

        } catch (CommonException e) {
            throw e;
        }
    }

    public boolean getApplicationQueueByName(Connection connection, String name) throws CommonException {
        try {
            boolean queueExists = false;
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueueByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueueByName Started").build());

            QueueDAO queueDAO = new QueueDAO();
            queueExists = queueDAO.getApplicationQueueByName(connection, name);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueueByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueueByName Ended").build());

            return queueExists;
        } catch (CommonException e) {
            throw e;
        }
    }

    public int enqueeMsg(Connection connection, InputModel input, String ipaddr, ServicesModel service) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueueByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "enqueeMsg Started").build());

            QueueDAO queueDAO = new QueueDAO();
            QueueModel applicationQueues = service.getQueueModel();
            int csMsgID = queueDAO.enqueeMsg(connection, input, ipaddr, applicationQueues);
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueueByName]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "enqueeMsg Ended").build());

            return csMsgID;
        } catch (CommonException e) {
            throw e;
        }
    }

    public QueueModel executeQueriesUsingQueueInformation(Connection connection, QueueModel queueModel) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [executeQueriesUsingQueueInformation]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "executeQueriesUsingQueueInformation Started").build());

        QueueDAO queueDAO = new QueueDAO();
//Defines check
        if (((Defines.QUEUE_QUERY.TABLE_NAME_POSTFIX != null) && (!Defines.QUEUE_QUERY.TABLE_NAME_POSTFIX.isEmpty()))
                && (Defines.QUEUE_QUERY.QUEUE_NAME_POSTFIX != null) && (!Defines.QUEUE_QUERY.QUEUE_NAME_POSTFIX.isEmpty())
                && (Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY) != null)
                && (!Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY).isEmpty())
                && (Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY) != null)
                && (!Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY).isEmpty())
                && (Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_START_QUERY) != null)
                && (!Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_START_QUERY).isEmpty())
                && (Defines.QUEUE_QUERY.TABLE_NAME != null) && (!Defines.QUEUE_QUERY.TABLE_NAME.isEmpty())
                && (Defines.QUEUE_QUERY.QUEUE_NAME != null) && (!Defines.QUEUE_QUERY.QUEUE_NAME.isEmpty())
                && (Defines.QUEUE_QUERY.TABLE_SPACE_NAME != null) && (!Defines.QUEUE_QUERY.TABLE_SPACE_NAME.isEmpty())
                && (Defines.QUEUE_QUERY.OBJECT_NAME != null) && (!Defines.QUEUE_QUERY.OBJECT_NAME.isEmpty())
                && (Defines.QUEUE_QUERY.OBJECT_NAME_VALUE != null) && (!Defines.QUEUE_QUERY.OBJECT_NAME_VALUE.isEmpty())
                && (Defines.QUEUE_QUERY.OBJECT_NAME_SP_VALUE != null) && (!Defines.QUEUE_QUERY.OBJECT_NAME_SP_VALUE.isEmpty())
                ) {
            String tblName = queueModel.getAppName() + Defines.QUEUE_QUERY.TABLE_NAME_POSTFIX;
            String queueName = queueModel.getAppName() + Defines.QUEUE_QUERY.QUEUE_NAME_POSTFIX;

            if ((tblName != null) && (!tblName.isEmpty()) && (queueName != null) && (!queueName.isEmpty())
                    && (queueModel.getTableSpaceName() != null) && (!queueModel.getTableSpaceName().isEmpty())) {
                //A)  Create table with (QueueApp_Name_TBL) 
                String createTblQuery = Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_TBL_QUERY);
                createTblQuery = createTblQuery.replace(Defines.QUEUE_QUERY.TABLE_NAME, tblName);
                if (queueModel.getQueueType() == 1) {
                createTblQuery = createTblQuery.replace(Defines.QUEUE_QUERY.OBJECT_NAME, Defines.QUEUE_QUERY.OBJECT_NAME_VALUE);
                } else if (queueModel.getQueueType() == 2){
                createTblQuery = createTblQuery.replace(Defines.QUEUE_QUERY.OBJECT_NAME, Defines.QUEUE_QUERY.OBJECT_NAME_SP_VALUE);
                }
                createTblQuery = createTblQuery.replace(Defines.QUEUE_QUERY.TABLE_SPACE_NAME, queueModel.getTableSpaceName());

                queueDAO.executeQueueQuery(connection, new StringBuilder(createTblQuery));

                String createQueueQuery = Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_CREATE_QUERY);
                createQueueQuery = createQueueQuery.replace(Defines.QUEUE_QUERY.QUEUE_NAME, queueName);
                createQueueQuery = createQueueQuery.replace(Defines.QUEUE_QUERY.TABLE_NAME, tblName);

                queueDAO.executeQueueQuery(connection, new StringBuilder(createQueueQuery));

                String startQueueQuery = Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_START_QUERY);
                startQueueQuery = startQueueQuery.replace(Defines.QUEUE_QUERY.QUEUE_NAME, queueName);

                queueDAO.executeQueueQuery(connection, new StringBuilder(startQueueQuery));

                queueModel.setQueueName(queueName);
//                CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [executeQueriesUsingQueueInformation]...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "executeQueriesUsingQueueInformation Ended").build());

                return queueModel;
            } else {
                throw new CommonException("Names Required from the queue are missing", GeneralConstants.QUEUE_QUERY.QUEUE_VARIABLE_EMPTY);
            }
        } else {
            throw new CommonException("Defines For Queue Query are not set", ErrorCodes.NULL_POINTER_ERROR);
        }
    }

    public void executeDeleteQueryUsingQueueInformation(Connection connection, QueueModel queueModel) throws CommonException {
//        CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [executeDeleteQueryUsingQueueInformation]...");
        CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "executeDeleteQueryUsingQueueInformation Started").build());

        QueueDAO queueDAO = new QueueDAO();
//Defines check
        if (((Defines.QUEUE_QUERY.TABLE_NAME_POSTFIX != null) && (!Defines.QUEUE_QUERY.TABLE_NAME_POSTFIX.isEmpty()))
                && (Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY) != null)
                && (!Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY).isEmpty())
                && ((Defines.QUEUE_QUERY.TABLE_NAME != null) && (!Defines.QUEUE_QUERY.TABLE_NAME.isEmpty()))) {
            String tblName = queueModel.getAppName().toUpperCase() + Defines.QUEUE_QUERY.TABLE_NAME_POSTFIX;

            if ((tblName != null) && (!tblName.isEmpty())) {
                //A)  DELETE table (QueueApp_Name_TBL) 
                String deleteTblQuery = Defines.QUEUE_QUERY.QUERIES.get(Defines.QUEUE_QUERY.QUEUE_DELETE_QUERY);
                deleteTblQuery = deleteTblQuery.replace(Defines.QUEUE_QUERY.TABLE_NAME, tblName);

                queueDAO.executeQueueQuery(connection, new StringBuilder(deleteTblQuery));

//                CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [executeDeleteQueryUsingQueueInformation]...");
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "executeDeleteQueryUsingQueueInformation Ended").build());

            } else {
                throw new CommonException("Names Required from the queue are missing", GeneralConstants.QUEUE_QUERY.QUEUE_VARIABLE_EMPTY);
            }
        } else {
            throw new CommonException("Defines For Queue Query are not set", ErrorCodes.NULL_POINTER_ERROR);
        }
    }

    public ArrayList<QueueModel> getApplicationQueuesBySetOfNames(Connection connection, List<String> queueNames, int queueType) throws CommonException {
        try {
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Starting Getting [getApplicationQueuesBySetOfNames]...");
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "Queue Names found " + queueNames);
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesBySetOfNames Started")
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueNames).build());

            QueueDAO queueDAO = new QueueDAO();
            ArrayList<QueueModel> queueModels = queueDAO.getApplicationQueuesBySetOfNames(connection, queueNames, queueType);
            boolean found = false;
            //LOGGING NON EXISTING QUEUES WITH GIVEN PARAMETER NAMES 
            if (queueModels.size() > 0) {
                for (String queueName : queueNames) {
                    found = false;
                    for (QueueModel queue : queueModels) {
                        if (queueName.equalsIgnoreCase(queue.getAppName())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
//                        CommonLogger.businessLogger.info(QueueService.class.getName() + " || " + "NOT FOUND =>[" + queueName + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Not Found")
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());

                    } else {
//                        CommonLogger.businessLogger.info(QueueService.class.getName() + " || " + "FOUND =>[" + queueName + "]");
                        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Found")
                                .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName).build());

                    }
                }
            } else {
//                CommonLogger.businessLogger.info(QueueService.class.getName() + " || " + "Given queue names were not found ..");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Given queue Names not Found").build());

            }
//            CommonLogger.businessLogger.debug(QueueService.class.getName() + " || " + "End Getting [getApplicationQueuesBySetOfNames]...");
            CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "getApplicationQueuesBySetOfNames Ended").build());

            return queueModels;
        } catch (CommonException e) {
            throw e;
        }
    }
}
