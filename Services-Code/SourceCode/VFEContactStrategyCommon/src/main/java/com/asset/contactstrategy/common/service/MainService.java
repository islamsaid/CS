/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.asset.contactstrategy.common.controller.DataSourceManger;
import com.asset.contactstrategy.common.dao.CommonDAO;
import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.AdsGroupModel;
import com.asset.contactstrategy.common.models.CampaignModel;
import com.asset.contactstrategy.common.models.CsSmscInterfaceHistoryModel;
import com.asset.contactstrategy.common.models.CustomerConfigurationModel;
import com.asset.contactstrategy.common.models.CustomersCampaignsModel;
import com.asset.contactstrategy.common.models.DWHElementModel;
import com.asset.contactstrategy.common.models.DWHElementValueModel;
import com.asset.contactstrategy.common.models.FileModel;
import com.asset.contactstrategy.common.models.FilterModel;
import com.asset.contactstrategy.common.models.GroupsParentModel;
import com.asset.contactstrategy.common.models.LineModel;
import com.asset.contactstrategy.common.models.LogModel;
import com.asset.contactstrategy.common.models.LookupModel;
import com.asset.contactstrategy.common.models.MOMErrorsModel;
import com.asset.contactstrategy.common.models.MatchingInstanceModel;
import com.asset.contactstrategy.common.models.MenuModel;
import com.asset.contactstrategy.common.models.OriginatorTypeModel;
import com.asset.contactstrategy.common.models.QueueModel;
import com.asset.contactstrategy.common.models.ReportsModel;
import com.asset.contactstrategy.common.models.ReportsViewModel;
import com.asset.contactstrategy.common.models.RequestPreparator;
import com.asset.contactstrategy.common.models.SMSBulkFileModel;
import com.asset.contactstrategy.common.models.SMSCInterfaceClientModel;
import com.asset.contactstrategy.common.models.SMSCModel;
import com.asset.contactstrategy.common.models.SMSGroupModel;
import com.asset.contactstrategy.common.models.ServiceModel;
import com.asset.contactstrategy.common.models.SystemPropertiesModel;
import com.asset.contactstrategy.common.models.UploadProcedureResult;
import com.asset.contactstrategy.common.models.UserModel;
import com.asset.contactstrategy.common.models.WebLogModel;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.interfaces.models.CustomerConfigAndGrpModel;
import com.asset.contactstrategy.interfaces.models.CustomerConfigurationsModel;
import com.asset.contactstrategy.interfaces.models.InputModel;
import com.asset.contactstrategy.interfaces.service.CustomerService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;

/**
 *
 * @author kerollos.asaad
 */
public class MainService {

    public ArrayList<QueueModel> getApplicationQueues() throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            ArrayList<QueueModel> queueModels = queueService.getApplicationQueues(connection);
            return queueModels;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return null;
    }

    /*   public ArrayList<QueueModel> getApplicationQueues(int queueType) throws CommonException {

     Connection connection = null;
     try {
     connection = DataSourceManger.getConnection();
     QueueService queueService = new QueueService();
     ArrayList<QueueModel> queueModels = queueService.getApplicationQueues(connection, queueType);
     return queueModels;

     } catch (Exception ex) {
     CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex, ex);
     if (ex instanceof CommonException) {
     throw (CommonException) ex;
     } else if (ex instanceof SQLException) {
     throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
     }
     } finally {
     if (connection != null) {
     try {
     DataSourceManger.closeConnection(connection);
     } catch (Exception ex) {
     CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueues]" + ex, ex);
     throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
     }
     }
     }
     return null;
     } */
    public HashMap<String, QueueModel> getHashedApplicationQueues() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            return queueService.getHashedApplicationQueues(connection);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getHashedApplicationQueues]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getHashedApplicationQueues]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return null;
    }

    public HashMap<String, QueueModel> getApplicationQueuesServiceAndStatusApproved(int queueType) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            return queueService.getApplicationQueuesServiceAndStatusApproved(connection, queueType);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueuesServiceAndStatusApproved]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueuesServiceAndStatusApproved]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return null;
    }

    public int getRecievedSMSCountPerCustomer(ReportsModel reportsModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ReportsService reportsService = new ReportsService();
            int count = reportsService.RecievedSMSPerCustomeService(connection, reportsModel);
            return count;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getRecievedSmsCount]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getRecievedSmsCount]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    //Start Customer Configuration
    public CustomerConfigurationModel getMSISDNAvailibility(String msisdn) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerConfigurationService configurationService = new CustomerConfigurationService();
            CustomerConfigurationModel configurationModel = configurationService.checkMSISDNAvailabilityService(msisdn, connection);
            return configurationModel;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getMSISDNAvailibility]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getMSISDNAvailibility]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void insertCustomer(CustomerConfigurationModel configurationModel, String msisdn) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerConfigurationService configurationService = new CustomerConfigurationService();
            configurationService.InsertCustomerData(msisdn, configurationModel, connection);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCustmer]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCustmer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertCustmer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateCustomer(CustomerConfigurationModel configurationModel, String msisdn) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerConfigurationService configurationService = new CustomerConfigurationService();
            configurationService.updateCustomerData(msisdn, configurationModel, connection);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateCustomer]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteCustomer(String msisdn) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerConfigurationService configurationService = new CustomerConfigurationService();
            configurationService.deleteCustomerData(msisdn, connection);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCustomer]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    //End Customer Configuration
    public int getViolationSMSPerCustomer(ReportsModel reportsModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ReportsService reportsService = new ReportsService();
            int count = reportsService.violationSMSPerCustomerService(connection, reportsModel);
            return count;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSmsCount]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSmsCount]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }

    }

    public int recievedSMSByCSPCount(ReportsModel reportsModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ReportsService reportsService = new ReportsService();
            int count = reportsService.recievedSMSByCSPService(connection, reportsModel);
            return count;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSMSPerCustomer]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSMSPerCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }

    }

    public ReportsModel recievedNoOfSMSByPlatform(ReportsModel reportsModel) throws CommonException {

        Connection connection = null;
        ReportsModel model;
        try {
            connection = DataSourceManger.getConnection();
            ReportsService reportsService = new ReportsService();
            model = reportsService.noOfSMSByPlatformService(connection, reportsModel);
            return model;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSMSPerCustomer]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSMSPerCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }

    }

    public ReportsModel getNoOfSMSToSMSC(ReportsModel reportsModel) throws CommonException {

        Connection connection = null;
        ReportsModel model;
        try {
            connection = DataSourceManger.getConnection();
            ReportsService reportsService = new ReportsService();
            model = reportsService.NoOfSMSToSMSCService(connection, reportsModel);
            return model;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSMSPerCustomer]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getViolationSMSPerCustomer]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }

    }

    /**
     * createApplicationQueue .. create the connection from the info in the
     * queue and execute the queries if success commit the insertion of the
     * queue
     *
     */
    public void createApplicationQueue(QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        Connection conForQInsert = null;
        Connection conForQQuery = null;
        QueueService queueService = new QueueService();
        String queueName = queueModel.getAppName();
        try {
            if (Defines.MESSAGING_MODE.equals(Defines.RABBITMQ)) {

                RabbitmqQueueService rabbitmqQueueService = new RabbitmqQueueService();
                rabbitmqQueueService.createQueue(queueName);

            } else if (Defines.MESSAGING_MODE.equals(Defines.ORCLAQ)) {
                //A )) 1-- CREATE CONNECTION GOTTEN FROM QUEUEMODEL
                String dbURL = queueModel.getDatabaseUrl();
                String schemaName = queueModel.getSchemaName();
                String schemaPswd = queueModel.getSchemaPassword();
                if (((dbURL != null) && (!dbURL.isEmpty()))
                        && ((schemaName != null) && (!schemaName.isEmpty()))
                        && ((schemaPswd != null) && (!schemaPswd.isEmpty()))) {
                    conForQQuery = Utility.getDBConnection(dbURL, schemaName, schemaPswd);// Open Connection
                } else {
                    throw new CommonException("Connection is missing some or all arguments", GeneralConstants.QUEUE_QUERY.QUEUE_CONNECTION_EMPTY);
                }

                //A )) 2-- EXECUTE QUERIES BASED ON QUEUEMODEL INFO
                if (queueModel.getQueueType() == 1 || queueModel.getQueueType() == 2) {
                    if ((conForQQuery != null) && (!conForQQuery.isClosed())) {
                        queueModel = queueService.executeQueriesUsingQueueInformation(conForQQuery, queueModel);
                    } else {
                        throw new CommonException("Connection is closed or not gotten without reason", ErrorCodes.GET_CONNECTION_ERROR);
                    }
                }
                conForQQuery.commit();
            }
            //B )) INSERT QUEUE IN DATABASE
            conForQInsert = DataSourceManger.getConnection();
            queueService.insertApplicationQueue(conForQInsert, queueModel, smscModels);

            //C  ))  NO ERROR OCCURED TILL NOW .. COMMIT          
            conForQInsert.commit();

        } catch (Exception ex) {
            if (conForQInsert != null) {
                DataSourceManger.rollBack(conForQInsert);
            }
            if (conForQQuery != null) {
                try {
                    conForQQuery.rollback();
                } catch (SQLException rollex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                            + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack in createApplicationQueue]", rollex);
                    throw new CommonException(rollex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
                }
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueue]", ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conForQInsert != null) {
                try {
                    DataSourceManger.closeConnection(conForQInsert);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueue]", ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
            if (conForQQuery != null) {
                try {
                    Utility.closeDBConnection(conForQQuery);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueue]", ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateApplicationQueue(QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            queueService.updateApplicationQueue(connection, queueModel, smscModels);
            connection.commit();

        } catch (Exception ex) {
            if (connection != null) {
                DataSourceManger.rollBack(connection);
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateApplicationQueue]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }

        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void insertChildApplicationQueue(QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            queueService.insertChildApplicationQueue(connection, queueModel, smscModels);
            connection.commit();

        } catch (Exception ex) {
            if (connection != null) {
                DataSourceManger.rollBack(connection);
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertChildApplicationQueue]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertChildApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateParentAndDeleteChildApplicationQueue(QueueModel queueModel, ArrayList<SMSCModel> smscModels) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            queueService.updateParentAndDeleteChildApplicationQueue(connection, queueModel, smscModels);
            connection.commit();

        } catch (Exception ex) {
            if (connection != null) {
                DataSourceManger.rollBack(connection);
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateParentAndDeleteChildApplicationQueue]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateParentAndDeleteChildApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void lockApplicationQueuesForDeletion(QueueModel queueModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            queueModel.setStatus(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE);
            queueService.lockApplicationQueueForDeletion(connection, queueModel);
            connection.commit();

        } catch (Exception ex) {
            if (connection != null) {
                DataSourceManger.rollBack(connection);
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [lockApplicationQueuesForDeletion]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [lockApplicationQueuesForDeletion]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteApplicationQueue(QueueModel queueModel) throws CommonException {
        Connection conForQDeletion = null;
        Connection conForQTblDeletion = null;
        QueueService queueService = new QueueService();
        ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
        String referencedService = null;
        try {
            //PRE CONDITION:  CHECK FOR SERVICE REFERENCING THE QUEUE 
            conForQDeletion = DataSourceManger.getConnection();
            referencedService = serviceManagmentService.hasReferencetoQueue(conForQDeletion, queueModel);
            if ((referencedService == null) || ((referencedService != null) && (referencedService.isEmpty()))) {
                //A )) DELETE QUEUE FROM DATABASE
                queueService.deleteApplicationQueue(conForQDeletion, queueModel);

                if (Defines.MESSAGING_MODE.equals(Defines.RABBITMQ)) {
                    RabbitmqQueueService rabbitmqQueueService = new RabbitmqQueueService();
                    rabbitmqQueueService.deleteQueue(queueModel.getAppName());
                } else if (Defines.MESSAGING_MODE.equals(Defines.ORCLAQ)) {
                    //B )) 1-- CREATE CONNECTION GOTTEN FROM QUEUEMODEL
                    String dbURL = queueModel.getDatabaseUrl();
                    String schemaName = queueModel.getSchemaName();
                    String schemaPswd = queueModel.getSchemaPassword();
                    if (((dbURL != null) && (!dbURL.isEmpty()))
                            && ((schemaName != null) && (!schemaName.isEmpty()))
                            && ((schemaPswd != null) && (!schemaPswd.isEmpty()))) {
                        conForQTblDeletion = Utility.getDBConnection(dbURL, schemaName, schemaPswd);// Open Connection
                    } else {
                        throw new CommonException("Connection is missing some or all arguments", GeneralConstants.QUEUE_QUERY.QUEUE_CONNECTION_EMPTY);
                    }

                    //B )) 2-- EXECUTE DELETE QUERY BASED ON QUEUEMODEL INFO
                   if (queueModel.getQueueType() == 1 || queueModel.getQueueType() == 2) {
                        if ((conForQTblDeletion != null) && (!conForQTblDeletion.isClosed())) {
                            queueService.executeDeleteQueryUsingQueueInformation(conForQTblDeletion, queueModel);
                        } else {
                            throw new CommonException("Connection is closed or not gotten without reason", ErrorCodes.GET_CONNECTION_ERROR);
                        }
                    }

                }
                //C  ))  NO ERROR OCCURED TILL NOW .. COMMIT
                conForQDeletion.commit();
                if (conForQTblDeletion != null) {
                    conForQTblDeletion.commit();
                }
            } else {
                throw new CommonException("A reference from Service " + referencedService + " was found.", ErrorCodes.INTEGRITY_ERROR);
            }
        } catch (Exception ex) {
            if (conForQDeletion != null) {
                DataSourceManger.rollBack(conForQDeletion);
            }
            if (conForQTblDeletion != null) {
                try {
                    conForQTblDeletion.rollback();
                } catch (SQLException rollex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                            + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack in deleteApplicationQueue]" + rollex, rollex);
                    throw new CommonException(rollex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
                }
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApplicationQueue]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conForQDeletion != null) {
                try {
                    DataSourceManger.closeConnection(conForQDeletion);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
            if (conForQTblDeletion != null) {
                try {
                    Utility.closeDBConnection(conForQTblDeletion);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteParentAndChildApplicationQueue(QueueModel queueModel) throws CommonException {
        Connection conForQDeletion = null;
        Connection conForQTblDeletion = null;
        QueueService queueService = new QueueService();
        ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
        String referencedService = null;
        try {
            //PRE CONDITION:  CHECK FOR SERVICE REFERENCING THE QUEUE 
            conForQDeletion = DataSourceManger.getConnection();
            referencedService = serviceManagmentService.hasReferencetoQueue(conForQDeletion, queueModel);
            if ((referencedService == null) || ((referencedService != null) && (referencedService.isEmpty()))) {
                //A )) DELETE QUEUE FROM DATABASE
                queueService.deleteParentAndChildApplicationQueue(conForQDeletion, queueModel);

                if (Defines.MESSAGING_MODE.equals(Defines.RABBITMQ)) {
                    RabbitmqQueueService rabbitmqQueueService = new RabbitmqQueueService();
                    rabbitmqQueueService.deleteQueue(queueModel.getAppName());
                } else if (Defines.MESSAGING_MODE.equals(Defines.ORCLAQ)) {
                    //B )) 1-- CREATE CONNECTION GOTTEN FROM QUEUEMODEL
                    String dbURL = queueModel.getDatabaseUrl();
                    String schemaName = queueModel.getSchemaName();
                    String schemaPswd = queueModel.getSchemaPassword();
                    if (((dbURL != null) && (!dbURL.isEmpty()))
                            && ((schemaName != null) && (!schemaName.isEmpty()))
                            && ((schemaPswd != null) && (!schemaPswd.isEmpty()))) {
                        conForQTblDeletion = Utility.getDBConnection(dbURL, schemaName, schemaPswd);// Open Connection
                    } else {
                        throw new CommonException("Connection is missing some or all arguments", GeneralConstants.QUEUE_QUERY.QUEUE_CONNECTION_EMPTY);
                    }
                    //B )) 2-- EXECUTE DELETE QUERY BASED ON QUEUEMODEL INFO
                    if (queueModel.getQueueType() == 1 || queueModel.getQueueType() == 2) {
                        if ((conForQTblDeletion != null) && (!conForQTblDeletion.isClosed())) {
                            queueService.executeDeleteQueryUsingQueueInformation(conForQTblDeletion, queueModel);
                        } else {
                            throw new CommonException("Connection is closed or not gotten without reason", ErrorCodes.GET_CONNECTION_ERROR);
                        }
                    }
                }
                //C  ))  NO ERROR OCCURED TILL NOW .. COMMIT
                conForQDeletion.commit();
                if (conForQTblDeletion != null) {
                    conForQTblDeletion.commit();
                }
            } else {
                throw new CommonException("SERVICE " + referencedService, ErrorCodes.INTEGRITY_CONSTRAINT_ERROR);
            }
        } catch (Exception ex) {
            DataSourceManger.rollBack(conForQDeletion);

            if (conForQTblDeletion != null) {
                try {
                    conForQTblDeletion.rollback();
                } catch (SQLException rollex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                            + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack in deleteParentAndChildApplicationQueue]" + rollex, rollex);
                    throw new CommonException(rollex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
                }
            }
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteParentAndChildApplicationQueue]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conForQDeletion != null) {
                try {
                    DataSourceManger.closeConnection(conForQDeletion);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteParentAndChildApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
            if (conForQTblDeletion != null) {
                try {
                    Utility.closeDBConnection(conForQTblDeletion);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteParentAndChildApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public boolean getApplicationQueueByName(String name) throws CommonException {
        Connection connection = null;
        boolean queueExists = false;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            queueExists = queueService.getApplicationQueueByName(connection, name);
            return queueExists;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueByName]" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueueByName]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }

        }

    }

    public boolean checkCampaignFileStatus(int fileId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService elementsService = new CampaignService();

            return elementsService.checkFileStatus(connection, fileId);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->checkCampaignFileStatus " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void updateCampaignFileStatus(int fileId, int fileStatus) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService service = new CampaignService();
            service.updateFileStatus(connection, fileId, fileStatus);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->updateCampaignFileStatus " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public void updateCampaignFilesStatus() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService service = new CampaignService();
            service.updateFilesStatus(connection);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateCampaignFilesStatus => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public boolean checkSMSGroupFileStatus(int fileId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSGroupService elementsService = new SMSGroupService();

            return elementsService.checkFileStatus(connection, fileId);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->checkSMSGroupFileStatus" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void updateSMSGroupFileStatus(int fileId, int fileStatus) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSGroupService service = new SMSGroupService();
            service.updateFileStatus(connection, fileId, fileStatus);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateSMSGroupFileStatus => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public void updateGroupFileStatus(int fileId, int fileStatus, int type) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            if (type == GeneralConstants.SMS_GROUP) {
                SMSGroupService service = new SMSGroupService();
                service.updateFileStatus(connection, fileId, fileStatus);
            } else if (type == GeneralConstants.ADS_GROUP) {
                AdsGroupService service = new AdsGroupService();
                service.updateFileStatus(connection, fileId, fileStatus);
            } else {
                CampaignService service = new CampaignService();
                service.updateFileStatus(connection, fileId, fileStatus);
            }
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateGroupFileStatus => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public void updateSMSGroupFilesStatus() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSGroupService service = new SMSGroupService();
            service.updateFilesStatus(connection);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateSMSGroupFilesStatus => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public boolean checkADSGroupFileStatus(int fileId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            AdsGroupService elementsService = new AdsGroupService();

            return elementsService.checkFileStatus(connection, fileId);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->checkADSGroupFileStatus" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void updateADSGroupFileStatus(int fileId, int fileStatus) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            AdsGroupService service = new AdsGroupService();
            service.updateFileStatus(connection, fileId, fileStatus);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->Failure updateADSGroupFileStatus => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public void updateADSGroupFilesStatus() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            AdsGroupService service = new AdsGroupService();
            service.updateFilesStatus(connection);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateADSGroupFilesStatus => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public boolean CompareTimeStamps(String firstItemKey, int firstSrcId, String secondItemKey, int secondSrcId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService serv = new CustomerMatchingService();
            return serv.CompareTimeStamps(connection, firstItemKey, firstSrcId, secondItemKey, secondSrcId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "CompareTimeStamps" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void updateALLMatchingInstancesRunId(int runId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService serv = new CustomerMatchingService();
            serv.updateInstancesRunId(connection, runId);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateALLMatchingInstancesRunId => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public MatchingInstanceModel getMatchingInstance(int srcId, String instanceId) throws CommonException {
        Connection connection = null;

        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService serv = new CustomerMatchingService();
            return serv.getInstance(connection, srcId, instanceId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> getMatchingInstance" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void updateMatchingInstanceModel(MatchingInstanceModel instance) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService serv = new CustomerMatchingService();
            serv.updateInstanceModel(connection, instance);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateMatchingInstanceModel => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public boolean allInstancesSuccessed(int dwhRunId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService serv = new CustomerMatchingService();
            return serv.allInstancesSuccessed(connection, dwhRunId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->allInstancesSuccessed" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public boolean allInstancesFinished(int dwhRunId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService serv = new CustomerMatchingService();
            return serv.allInstancesFinished(connection, dwhRunId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->allInstancesFinished" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public ArrayList<DWHElementValueModel> loadCustomersAttributes() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHElementService elementsService = new DWHElementService();
            ArrayList<DWHElementValueModel> elementsList = elementsService.loadCustomersAttributes(connection);
            return elementsList;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->loadCustomersAttributes" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public ArrayList<DWHElementValueModel> loadGovernmentAttributes() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHElementService elementsService = new DWHElementService();
            ArrayList<DWHElementValueModel> elementsList = elementsService.loadGovernmentAttributes(connection);
            return elementsList;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->loadGovernmentAttributes" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public ArrayList<DWHElementModel> loadAllDWHElements() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHElementService elementsService = new DWHElementService();
            ArrayList<DWHElementModel> elementsList = elementsService.loadAllElements(connection);
            return elementsList;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->loadAllDWHElements" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public Vector<DWHElementModel> getDWHElementsList() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHElementService elementsService = new DWHElementService();
            Vector<DWHElementModel> elementsList = elementsService.getDWHElementsList(connection);
            return elementsList;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->getDWHElementsList" + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void resetStatisticsCounterColumn(String tableName, int columnId, int lastMSISDNTwoDigits) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerStatisticsService service = new CustomerStatisticsService();
            service.resetStatisticsCounterColumn(connection, tableName, columnId, lastMSISDNTwoDigits);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> resetStatisticsCounterColumn => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    //not throw exception to keep calling
    public boolean resetStatisticsCounterColumnTable(String tableName, int columnId) {
        Connection connection = null;
        boolean success = false;
        try {
            connection = DataSourceManger.getConnection();
            CustomerStatisticsService serv = new CustomerStatisticsService();
            success = serv.resetStatisticsCounterColumnTable(connection, tableName, columnId);
            connection.commit();
        } catch (Exception e) {
            try {
                DataSourceManger.rollBack(connection);
                CommonLogger.errorLogger.error("Getting Caught Exception----> resetStatisticsCounterColumnTable => ", e);
                CommonLogger.businessLogger.error("Getting Caught Exception----> resetStatisticsCounterColumnTable => " + e);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception----> RollBack => ", e);
                CommonLogger.businessLogger.error("Getting Caught Exception----> RollBack => " + e);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (CommonException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception----> Close Connection => ", ex);
                    CommonLogger.businessLogger.error("Getting Caught Exception----> Close Connection => " + ex);
                }
            }
        }
        return success;
    }

    public void copyCounterToTemp(String tableName, int columnId, int lastMSISDNTwoDigits) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerStatisticsService service = new CustomerStatisticsService();
            service.copyCounterToTemp(connection, tableName, columnId, lastMSISDNTwoDigits);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> copyCounterToTemp => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    //not throw exception to keep calling
    public boolean copyCounterToTempFullTable(String tableName, int columnId) {
        Connection connection = null;
        boolean success = false;
        try {
            connection = DataSourceManger.getConnection();
            CustomerStatisticsService serv = new CustomerStatisticsService();
            success = serv.copyCounterToTempFullTable(connection, tableName, columnId);
            connection.commit();
        } catch (Exception e) {
            try {
                DataSourceManger.rollBack(connection);
                CommonLogger.errorLogger.error("Getting Caught Exception----> copyCounterToTempFullTable => ", e);
                CommonLogger.businessLogger.error("Getting Caught Exception----> copyCounterToTempFullTable => " + e);
            } catch (CommonException ex) {
                CommonLogger.errorLogger.error("Getting Caught Exception----> RollBack => ", e);
                CommonLogger.businessLogger.error("Getting Caught Exception----> RollBack => " + e);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (CommonException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception----> Close Connection => ", ex);
                    CommonLogger.businessLogger.error("Getting Caught Exception----> Close Connection => " + ex);
                }
            }
        }
        return success;
    }

//    public void updateSMSCustomerStatistics(int columnId, int lastMSISDNTwoDigits) throws CommonException {
//        Connection connection = null;
//        try {
//            connection = DataSourceManger.getConnection();
//            CustomerStatisticsService service = new CustomerStatisticsService();
//            service.updateSMSCustomerStatistics(connection, columnId, lastMSISDNTwoDigits);
//            connection.commit();
//        } catch (Exception e) {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.errorLogger.error("Getting Caught Exception----> updateSMSCustomerStatistics => " + e, e);
//            if (e instanceof CommonException) {
//                throw (CommonException) e;
//            } else {
//                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//            }
//        } finally {
//            if (connection != null) {
//                DataSourceManger.closeConnection(connection);
//            }
//        }
//    }
//
//    public void updateADSCustomerStatistics(int columnId, int lastMSISDNTwoDigits) throws CommonException {
//        Connection connection = null;
//        try {
//            connection = DataSourceManger.getConnection();
//            CustomerStatisticsService service = new CustomerStatisticsService();
//            service.updateADSCustomerStatistics(connection, columnId, lastMSISDNTwoDigits);
//            connection.commit();
//        } catch (Exception e) {
//            DataSourceManger.rollBack(connection);
//            CommonLogger.errorLogger.error("Getting Caught Exception----> updateADSCustomerStatistics => " + e, e);
//            if (e instanceof CommonException) {
//                throw (CommonException) e;
//            } else {
//                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
//            }
//        } finally {
//            if (connection != null) {
//                DataSourceManger.closeConnection(connection);
//            }
//        }
//    }
//
//    //not throw exception to keep calling
//    public boolean updateSMSStatisticsTable(int columnId) {
//        Connection connection = null;
//        boolean success = false;
//        try {
//            connection = DataSourceManger.getConnection();
//            CustomerStatisticsService serv = new CustomerStatisticsService();
//            success = serv.updateSMSStatisticsTable(connection, columnId);
//            connection.commit();
//        } catch (Exception e) {
//            try {
//                DataSourceManger.rollBack(connection);
//                CommonLogger.errorLogger.error("Getting Caught Exception----> updateSMSStatisticsTable => ", e);
//                CommonLogger.businessLogger.error("Getting Caught Exception----> updateSMSStatisticsTable => " + e);
//            } catch (CommonException ex) {
//                CommonLogger.errorLogger.error("Getting Caught Exception----> RollBack => ", e);
//                CommonLogger.businessLogger.error("Getting Caught Exception----> RollBack => " + e);
//            }
//        } finally {
//            if (connection != null) {
//                try {
//                    DataSourceManger.closeConnection(connection);
//                } catch (CommonException ex) {
//                    CommonLogger.errorLogger.error("Getting Caught Exception----> Close Connection => ", ex);
//                    CommonLogger.businessLogger.error("Getting Caught Exception----> Close Connection => " + ex);
//                }
//            }
//
//        }
//        return success;
//    }
////not throw exception to keep calling
//
//    public boolean updateADSStatisticsTable(int columnId) {
//        Connection connection = null;
//        boolean success = false;
//        try {
//            connection = DataSourceManger.getConnection();
//            CustomerStatisticsService serv = new CustomerStatisticsService();
//            success = serv.updateADSStatisticsTable(connection, columnId);
//            connection.commit();
//        } catch (Exception e) {
//            try {
//                DataSourceManger.rollBack(connection);
//                CommonLogger.errorLogger.error("Getting Caught Exception----> updateADSStatisticsTable => ", e);
//                CommonLogger.businessLogger.error("Getting Caught Exception----> updateADSStatisticsTable => " + e);
//            } catch (CommonException ex) {
//                CommonLogger.errorLogger.error("Getting Caught Exception----> RollBack => ", ex);
//                CommonLogger.businessLogger.error("Getting Caught Exception----> RollBack => " + ex);
//            }
//
//        } finally {
//            if (connection != null) {
//                try {
//                    DataSourceManger.closeConnection(connection);
//                } catch (CommonException ex) {
//                    CommonLogger.errorLogger.error("Getting Caught Exception----> Close Connection => ", ex);
//                    CommonLogger.businessLogger.error("Getting Caught Exception----> Close Connection => " + ex);
//                }
//            }
//
//        }
//        return success;
//    }
    public long matchSMSGroupsCustomersByCriteria(int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery) throws CommonException {
        Connection connection = null;
        long affectedRows = 0;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            affectedRows = service.matchSMSGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> matchSMSGroupsCustomersByCriteria " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return affectedRows;
    }

    public long matchADSGroupsCustomersByCriteria(int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery) throws CommonException {
        Connection connection = null;
        long affectedRows = 0;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            affectedRows = service.matchADSGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> matchADSGroupsCustomersByCriteria " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return affectedRows;
    }

    public long matchCampaignGroupsCustomersByCriteria(long groupId, int runId, int lastMSISDNTwoDigits, String filterQuery, long max_targeted_customers) throws CommonException {
        Connection connection = null;
        long affectedRows = 0;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            affectedRows = service.matchCampaignGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery, max_targeted_customers);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> matchCampaignGroupsCustomersByCriteria " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return affectedRows;
    }

    public long matchGroupsCustomersByCriteria(int groupType, int groupId, int runId, int lastMSISDNTwoDigits, String filterQuery, long max_targeted_customers, int connectionRetries) throws CommonException {
        Connection connection = null;
        long affectedRows = 0;
        try {
            for (int i = 0; i < connectionRetries; i++) {
                try {
                    connection = DataSourceManger.getConnection();
                    break;
                } catch (Exception e) {
                    CommonLogger.businessLogger.error("MainService || " + "Caught Connection Exception----> for [matchGroupsCustomersByCriteria] Retry: " + i + " | " + e);
                    CommonLogger.errorLogger.error("MainService || " + "Caught Connection Exception----> for [matchGroupsCustomersByCriteria] Retry: " + i + " | " + e, e);
                }
            }
            if (groupType == GeneralConstants.CAMPAIGN_GROUP) {
                CustomerMatchingService service = new CustomerMatchingService();
                affectedRows = service.matchCampaignGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery, max_targeted_customers);
            } else if (groupType == GeneralConstants.SMS_GROUP) {
                CustomerMatchingService service = new CustomerMatchingService();
                affectedRows = service.matchGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery,
                        DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME,
                        DBStruct.VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID, /* variable name should be changed*/
                        DBStruct.DWH_CUSTOMERS.TBL_NAME);
            } else {
                CustomerMatchingService service = new CustomerMatchingService();
                affectedRows = service.matchGroupsCustomersByCriteria(connection, groupId, runId, lastMSISDNTwoDigits, filterQuery,
                        DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME,
                        DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.ADS_GROUP_ID,
                        DBStruct.DWH_CUSTOMERS.TBL_NAME);
            }
            connection.commit();
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("MainService || " + "Getting Caught Exception---->  for [matchGroupsCustomersByCriteria]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return affectedRows;
    }

    public UploadProcedureResult matchSMSGroupsCustomersByUpload(int groupId, int runId, int FileId) throws CommonException {
        Connection connection = null;
        UploadProcedureResult result = new UploadProcedureResult();
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            result = service.matchSMSGroupsCustomersByUpload(connection, groupId, runId, FileId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> matchSMSGroupsCustomersByUpload " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return result;
    }

    public UploadProcedureResult matchADSGroupsCustomersByUpload(int groupId, int runId, int FileId) throws CommonException {
        Connection connection = null;
        UploadProcedureResult result = new UploadProcedureResult();
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            result = service.matchADSGroupsCustomersByUpload(connection, groupId, runId, FileId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> matchADSGroupsCustomersByUpload " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return result;
    }

    public UploadProcedureResult matchCampaignGroupsCustomersByUpload(long groupId, int runId, int FileId) throws CommonException {
        Connection connection = null;
        UploadProcedureResult result = new UploadProcedureResult();
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            result = service.matchCampaignGroupsCustomersByUpload(connection, groupId, runId, FileId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> matchCampaignGroupsCustomersByUpload " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return result;
    }

    public UploadProcedureResult matchGroupsCustomersByUpload(int groupId, int runId, int FileId, int type, int connectionRetries) throws CommonException {
        Connection connection = null;
        UploadProcedureResult result = new UploadProcedureResult();
        try {
            for (int i = 0; i < connectionRetries; i++) {
                try {
                    connection = DataSourceManger.getConnection();
                    break;
                } catch (Exception e) {
                    CommonLogger.businessLogger.error("MainService || " + "Caught Connection Exception----> for [matchGroupsCustomersByUpload] Retry: " + i + " | " + e);
                    CommonLogger.errorLogger.error("MainService || " + "Caught Connection Exception----> for [matchGroupsCustomersByUpload] Retry: " + i + " | " + e, e);
                }
            }
            if (type == GeneralConstants.CAMPAIGN_GROUP) {
                CustomerMatchingService service = new CustomerMatchingService();
                result = service.matchCampaignGroupsCustomersByUpload(connection, groupId, runId, FileId);
            } else if (type == GeneralConstants.ADS_GROUP) {
                CustomerMatchingService service = new CustomerMatchingService();
                result = service.matchGroupsCustomersByUpload(connection, groupId, runId, FileId,
                        DBStruct.VFE_CS_ADS_GROUP_FILES.TABLE_NAME,
                        DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.TABLE_NAME,
                        DBStruct.VFE_CS_CUSTOMERS_ADS_GROUPS.ADS_GROUP_ID);
            } else {
                CustomerMatchingService service = new CustomerMatchingService();
                result = service.matchGroupsCustomersByUpload(connection, groupId, runId, FileId,
                        DBStruct.VFE_CS_SMS_GROUP_FILES.TABLE_NAME,
                        DBStruct.VFE_CS_CUSTOMERS_GROUPS.TABLE_NAME,
                        DBStruct.VFE_CS_CUSTOMERS_GROUPS.ADS_GROUP_ID /* variable name should be changed*/);
            }
            connection.commit();
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("MainService || Getting Caught Exception---->  for [matchGroupsCustomersByUpload]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("MainService || Getting Caught Exception---->  for [matchGroupsCustomersByUpload]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return result;
    }

    public UploadProcedureResult deleteSuspendCampaign(Connection connection, long groupId, int runId, long maxTargetedCustomers, long customersToSuspend) throws CommonException {
        //  Connection connection = null;
        UploadProcedureResult result = new UploadProcedureResult();
        try {
            //  connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            result = service.deleteSuspendCampaign(connection, groupId, runId, maxTargetedCustomers, customersToSuspend);
            // connection.commit();
        } catch (Exception e) {
            //  DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> updateSMSCustomerStatistics => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        }
        //finally {
//            if (connection != null) {
//                DataSourceManger.closeConnection(connection);
//            }
//        }
        return result;
    }

    public void addGroupsCustomersNewPartation(int partitionId, String tableName) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            service.addGroupsCustomersNewPartation(connection, partitionId, tableName);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> addGroupsCustomersNewPartation " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void dropGroupsCustomersPartation(int partitionId, String tableName) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CustomerMatchingService service = new CustomerMatchingService();
            service.dropGroupsCustomersPartation(connection, partitionId, tableName);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> dropGroupsCustomersPartation " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void addDWHProfileNewPartation(int partitionId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHProfileService service = new DWHProfileService();
            service.addDWHProfileNewPartation(connection, partitionId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> addDWHProfileNewPartation " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void dropDWHProfilePartation(int partitionId) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHProfileService service = new DWHProfileService();
            service.dropDWHProfilePartation(connection, partitionId);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> dropDWHProfilePartation " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void insertDWHProfileBatch(String SQLStatment, Vector<LineModel> dwListbatch, int numRetry) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection(numRetry);
            DWHProfileService service = new DWHProfileService();
            service.insertDWHProfileBatch(connection, SQLStatment, dwListbatch);
            connection.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception----> insertDWHProfileBatch => " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }

        }
    }

    public HashMap<String, String> getSystemPropertiesByGroupID(int groupID) throws CommonException {
        Connection connection = null;
        HashMap<String, String> systemProperities = new HashMap<>();
        try {
            connection = DataSourceManger.getConnection();
            SystemPropertiesService service = new SystemPropertiesService();
            systemProperities = service.getSystemPropertiesByGroupID(connection, groupID);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> getSystemPropertiesByGroupID " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return systemProperities;
    }

    public String getSystemPropertyByKey(String key, int groupID) throws CommonException {
        Connection connection = null;
        String ItemValue = null;
        try {
            connection = DataSourceManger.getConnection();
            SystemPropertiesService service = new SystemPropertiesService();
            ItemValue = service.getSystemPropertyByKey(connection, key, groupID);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->getSystemPropertyByKey " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return ItemValue;
    }

    public HashMap<String, String> getAllSystemProperties() throws CommonException {
        Connection connection = null;
        HashMap<String, String> systemProperities = new HashMap<>();
        try {
            connection = DataSourceManger.getConnection();
            SystemPropertiesService service = new SystemPropertiesService();
            systemProperities = service.getAllSystemProperties(connection);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception----> getAllSystemProperties " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return systemProperities;
    }

    public void incrementFinishedInstances(SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            SystemPropertiesService service = new SystemPropertiesService();
            service.incrementFinishedInstances(con, SystemPropertiesModel);
            con.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error("Getting Caught Exception----> incrementFinishedInstances => " + SystemPropertiesModel.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    public void updateTimeSystemProperty(Connection connection, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        try {
            SystemPropertiesService service = new SystemPropertiesService();
            service.updateTimeSystemProperty(connection, SystemPropertiesModel);
        } catch (Exception e) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Failure updateTimeSystemProperty => " + SystemPropertiesModel.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        }
    }

    public void updateTimeSystemProperty(SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            SystemPropertiesService service = new SystemPropertiesService();
            service.updateTimeSystemProperty(con, SystemPropertiesModel);
            con.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Failure updateTimeSystemProperty => " + SystemPropertiesModel.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    //Zain Used to update in a transaction
    public void updateSystemProperty(Connection connection, SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        try {
            SystemPropertiesService service = new SystemPropertiesService();
            service.updateSystemProperty(connection, SystemPropertiesModel);
        } catch (Exception e) {
            //DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Failure updateSystemProperty => " + SystemPropertiesModel.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        }
    }

    public void updateSystemProperty(SystemPropertiesModel SystemPropertiesModel) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            SystemPropertiesService service = new SystemPropertiesService();
            service.updateSystemProperty(con, SystemPropertiesModel);
            con.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Failure updateSystemProperty => " + SystemPropertiesModel.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    public ArrayList<String> loadDwhUsedColumns() throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            DWHElementService elementsService = new DWHElementService();
            ArrayList<String> usedColumns = elementsService.loadDwhUsedColumns(connection);
            return usedColumns;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->loadDwhUsedColumns " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public ArrayList<LookupModel> loadIdLableLookups(String tableName, String idcolName, String lableColName) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            LookupService lookUpsService = new LookupService();
            ArrayList<LookupModel> lookupsList = lookUpsService.loadIdLableLookups(tableName, idcolName, lableColName, connection);
            return lookupsList;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->loadIdLableLookups " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public ArrayList<OriginatorTypeModel> loadOriginatorLookup(String tableName, String idColName, String lableColName, String allowedColName) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            LookupService lookUpsService = new LookupService();
            ArrayList<OriginatorTypeModel> lookupsList = lookUpsService.loadOriginatorLookup(tableName, idColName, lableColName, allowedColName, connection);
            return lookupsList;

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->loadIdLableLookups " + ex, ex);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public HashMap<Integer, LookupModel> lookupListToMap(ArrayList<LookupModel> list) {
        HashMap<Integer, LookupModel> map = new HashMap<Integer, LookupModel>();
        if (list != null) {
            for (LookupModel lk : list) {
                map.put(lk.getId(), lk);
            }
        }
        return map;
    }

    public void insertNewElement(DWHElementModel newElement) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            DWHElementService service = new DWHElementService();
            service.insertNewDWHElement(newElement, con);
            if (newElement.getDisplayTypeId() == Defines.DWHELEMENT_DISPLAY_TYPES.MULTI_SELECTION) {
                service.insertElementLOV(newElement, con);
            }
            con.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error("Getting Caught Exception----> inserting new DWH Element => " + newElement.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    public void insertWebLog(WebLogModel logModel) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            WebLoggerService service = new WebLoggerService();
            service.insertWebLog(con, logModel);
            con.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error("Getting Caught Exception---->Failure inserting Web Log " + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    public void deleteMultiSelectionVale(DWHElementValueModel elementLOValue) throws CommonException {
        Connection con = null;

        try {

            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            DWHElementService service = new DWHElementService();
            service.deleteMultiSelectionValue(elementLOValue, con);
            con.commit();
        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error("Getting Caught Exception----> To Delete DWH Element Multi-Selection Value => " + elementLOValue.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }

        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    public void updateDwhElement(DWHElementModel element) throws CommonException {

        Connection con = null;

        try {

            con = DataSourceManger.getConnection();
            con.setAutoCommit(false);
            DWHElementService service = new DWHElementService();
            service.updateDwhElement(element, con);
            con.commit();

        } catch (Exception e) {
            DataSourceManger.rollBack(con);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Failure Update new DWH Element => " + element.toString() + e, e);
            if (e instanceof CommonException) {
                throw (CommonException) e;
            } else {
                throw new CommonException(e.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }

        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }

        }
    }

    public void deleteElement(DWHElementModel element) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            connection.setAutoCommit(false);
            DWHElementService elementsService = new DWHElementService();
            elementsService.deleteElement(connection, element);
            connection.commit();
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteElement] " + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public ArrayList<SMSCModel> getSMSCs() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            ArrayList<SMSCModel> smscModels = smscService.getSMSCs(connection);
            return smscModels;
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getSMSCs] " + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void editSMSC(SMSCModel sMSCModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            smscService.editSMSC(connection, sMSCModel);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editSMSC]" + e, e);
            DataSourceManger.rollBack(connection);
            throw e;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void editEditedSMSC(SMSCModel sMSCModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            smscService.editEditedSMSC(connection, sMSCModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editEditedSMSC]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public SMSCModel createSMSC(SMSCModel sMSCModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            sMSCModel = smscService.createSMSC(connection, sMSCModel);
            DataSourceManger.commitConnection(connection);
            return sMSCModel;
        } catch (CommonException ex) {

            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createSMSC]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public SMSCModel retrieveConnectedSMSC(SMSCModel sMSCModel) throws CommonException {
        Connection connection = null;
        SMSCModel returnedModel = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            returnedModel = smscService.retrieveConnectedSMSC(connection, sMSCModel);
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [retrieveConnectedSMSC] " + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return returnedModel;
    }

    public void deleteSMSC(SMSCModel sMSCModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            smscService.deleteSMSC(connection, sMSCModel);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteSMSC]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void deleteParentAndChildSMSC(SMSCModel parent, SMSCModel child) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            smscService.deleteSMSC(connection, parent);
            smscService.deleteSMSC(connection, child);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteParentAndChildSMSC]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void ChangeSMSCStatusToDelete(SMSCModel sMSCModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            smscService.ChangeSMSCStatusToDelete(connection, sMSCModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [ChangeSMSCStatusToDelete]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);

            }
        }
    }

    public ArrayList<SMSCModel> getApprovedSMSCs() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            ArrayList<SMSCModel> smscModels = smscService.getApprovedSMSCs(connection);
            return smscModels;
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApprovedSMSCs] " + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public SMSCModel getSMSCByName(String name) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSCService smscService = new SMSCService();
            SMSCModel smscModel = smscService.getSMSCByName(connection, name);
            return smscModel;
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getSMSCByName] " + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getSMSCByName]" + ex, ex);
                    throw ex;
                }
            }
        }
    }

    public ArrayList<SMSCModel> getApplicationQueuesSMSCs(long queueID) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            ArrayList<SMSCModel> smscModels = queueService.getApplicationQueuesSMSCs(queueID, connection);
            return smscModels;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueuesSMSCs]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueuesSMSCs]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return null;
    }

    public ArrayList<ServiceModel> getServices() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            ArrayList<ServiceModel> serviceModels = serviceManagmentService.getServices(connection);
            return serviceModels;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getServices" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for getServices" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
                }
            }
        }
    }

    public ArrayList<ServiceModel> getApprovedServices(boolean serviceSupportAds) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            ArrayList<ServiceModel> serviceModels = serviceManagmentService.getApprovedServices(connection, serviceSupportAds);
            return serviceModels;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getApprovedServices" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for getApprovedServices" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
                }
            }
        }
    }

    public Vector<SMSGroupModel> getApprovedGroupsList() throws CommonException {
        String methodName = "getApprovedGroupsList";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.getApprovedGroupsList(conn);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public Vector<GroupsParentModel> getApprovedGroupsList(int type) throws CommonException {
        String methodName = "getApprovedGroupsList";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            Vector<? extends GroupsParentModel> retParent = new Vector<GroupsParentModel>();
            if (type == GeneralConstants.SMS_GROUP) {
                SMSGroupService groupService = new SMSGroupService();
                retParent = groupService.getApprovedGroupsList(conn);
            } else if (type == GeneralConstants.ADS_GROUP) {
                AdsGroupService groupService = new AdsGroupService();
                retParent = groupService.getApprovedGroupsList(conn);
            } else if (type == GeneralConstants.CAMPAIGN_GROUP) {
                CampaignService groupService = new CampaignService();
                retParent = groupService.getApprovedActiveCampaignsList(conn);
            }
            return (Vector<GroupsParentModel>) retParent;
            /*else{
             CampaignService campService = new CampaignService();
             return campService.getApprovedActiveCampaignsList(conn);
             }*/
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public Vector<CampaignModel> getApprovedActiveCampaignsList() throws CommonException {
        String methodName = "getApprovedActiveCampaignsList";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            CampaignService groupService = new CampaignService();
            return groupService.getApprovedActiveCampaignsList(conn);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public Vector<AdsGroupModel> getApprovedADSGroupsList() throws CommonException {
        String methodName = "getApprovedADSGroupsList";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.getApprovedGroupsList(conn);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    // START KerollosAsaad.
    public ArrayList<SMSGroupModel> getSmsGroupsList() throws CommonException {
        String methodName = "getSmsGroupsList";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.getSmsGroupsList(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<AdsGroupModel> getAdsGroupsList() throws CommonException {
        String methodName = "getAdsGroupsList";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.getAdsGroupsList(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void addNewSmsGroup(SMSGroupModel group, int actorType) throws CommonException {
        String methodName = "addNewSmsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            group.setStatus((actorType == GeneralConstants.USER_TYPE_BUSINESS_VALUE)
                    ? GeneralConstants.STATUS_PENDING_VALUE : GeneralConstants.STATUS_APPROVED_VALUE);
            groupService.addNewSmsGroup(group, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void addNewAdsGroup(AdsGroupModel group, int actorType) throws CommonException {
        String methodName = "addNewAdsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            group.setStatus((actorType == GeneralConstants.USER_TYPE_BUSINESS_VALUE)
                    ? GeneralConstants.STATUS_PENDING_VALUE : GeneralConstants.STATUS_APPROVED_VALUE);
            groupService.addNewAdsGroup(group, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void approveSmsGroup(SMSGroupModel group) throws CommonException {
        String methodName = "approveSmsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                SMSGroupModel temp = groupService.getSmsGroupParent(group.getGroupId(), group.getVersionId(), conn);
                if (temp.getVersionId() != -1) {
                    // delete child group information/filters/files from database
                    // delete parent group filters/files
                    // insert for parent group retrieved filters/files of child group
                    // update parent group information with the child one.
                    groupService.deleteSmsGroupFiles(group.getVersionId(), conn);
                    groupService.deleteSmsGroupFiltersAndLOV(group, conn);
                    groupService.deleteSmsGroup(group.getVersionId(), conn);
                    group.setVersionId(temp.getVersionId());
                }
                group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                groupService.editSmsGroup(group, conn);

            } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                SMSGroupModel child = groupService.getSmsGroupChild(group, conn);
                //Deleting child group
                groupService.deleteSmsGroupFiles(child.getVersionId(), conn);
                groupService.deleteSmsGroupFiltersAndLOV(child, conn);
                groupService.deleteSmsGroup(child.getVersionId(), conn);
                //Deleting parent group
                groupService.deleteSmsGroupFiles(group.getVersionId(), conn);
                groupService.deleteSmsGroupFiltersAndLOV(group, conn);
                groupService.deleteSmsGroup(group.getVersionId(), conn);
            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void approveAdsGroup(AdsGroupModel group) throws CommonException {
        String methodName = "approveAdsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
//            // delete child group information/filters/files from database
//            // delete parent group filters/files
//            // insert for parent group retrieved filters/files of child group
//            // update parent group information with the child one.
//            groupService.deleteAdsGroup(group.getID(), conn);
//            groupService.deleteAdsGroupFiles(group.getID(), conn);
//            groupService.deleteAdsGroupFiltersAndLOV(group, conn);
//            
//            groupService.deleteAdsGroupFiles(group.getID(), conn);
//            groupService.deleteAdsGroupFiltersAndLOV(groupService.getAdsGroupParent(group.getID(), conn), conn);
//            
//            groupService.editAdsGroup(group, conn);
            if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                AdsGroupModel temp = groupService.getAdsGroupParent(group.getGroupId(), group.getVersionId(), conn);
                if (temp.getVersionId() != -1) {

                    // delete child group information/filters/files from database
                    // delete parent group filters/files
                    // insert for parent group retrieved filters/files of child group
                    // update parent group information with the child one.
                    groupService.deleteAdsGroupFiles(group.getVersionId(), conn);
                    groupService.deleteAdsGroupFiltersAndLOV(group, conn);
                    groupService.deleteAdsGroup(group.getVersionId(), conn);
                    group.setVersionId(temp.getVersionId());
                }
                group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                groupService.editAdsGroup(group, conn);

            } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                AdsGroupModel child = groupService.getAdsGroupChild(group, conn);
                //Deleting child group
                groupService.deleteAdsGroupFiles(child.getVersionId(), conn);
                groupService.deleteAdsGroupFiltersAndLOV(child, conn);
                groupService.deleteAdsGroup(child.getVersionId(), conn);
                //Deleting parent group
                groupService.deleteAdsGroupFiles(group.getVersionId(), conn);
                groupService.deleteAdsGroupFiltersAndLOV(group, conn);
                groupService.deleteAdsGroup(group.getVersionId(), conn);
            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    /**
     * This method deletes all group element filters, LOV and group uploaded
     * files.
     *
     * @param group
     * @throws CommonException
     */
    public void deleteSmsGroupCriteriasData(SMSGroupModel group) throws CommonException {
        String methodName = "deleteSmsGroupCriteriasData";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            groupService.deleteSmsGroupFiltersAndLOV(group, conn);
            groupService.deleteSmsGroupFiles(group.getVersionId(), conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteAdsGroupCriteriasData(AdsGroupModel group) throws CommonException {
        String methodName = "deleteAdsGroupCriteriasData";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            groupService.deleteAdsGroupFiltersAndLOV(group, conn);
            groupService.deleteAdsGroupFiles(group.getVersionId(), conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteSmsGroupFiltersAndLOV(SMSGroupModel group) throws CommonException {
        String methodName = "deleteSmsGroupFiltersAndLOV";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            groupService.deleteSmsGroupFiltersAndLOV(group, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteAdsGroupFiltersAndLOV(AdsGroupModel group) throws CommonException {
        String methodName = "deleteAdsGroupFiltersAndLOV";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            groupService.deleteAdsGroupFiltersAndLOV(group, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteSmsGroupFiles(int groupId) throws CommonException {
        String methodName = "deleteSmsGroupFiles";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            groupService.deleteSmsGroupFiles(groupId, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteAdsGroupFiles(int groupId) throws CommonException {
        String methodName = "deleteAdsGroupFiles";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            groupService.deleteAdsGroupFiles(groupId, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void editSmsGroup(SMSGroupModel group, int actorType) throws CommonException {
        String methodName = "editSmsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            if (actorType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                if (group.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    groupService.editSmsGroup(group, conn);
                } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    SMSGroupModel parent = groupService.getSmsGroupParent(group.getGroupId(), group.getVersionId(), conn);
                    if (parent.getVersionId() != -1) { // if group has parent
                        groupService.deleteSmsGroupFiltersAndLOV(group, conn);//delete child
                        groupService.deleteSmsGroupFiles(group.getVersionId(), conn);
                        groupService.deleteSmsGroup(group.getVersionId(), conn);
                        group.setVersionId(parent.getVersionId());
                        group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                        groupService.editSmsGroup(group, conn);//edit in parent
                    } else if (parent.getVersionId() == -1) {// if group has NO parent
                        group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                        groupService.editSmsGroup(group, conn);
                    }
                }

            } else if (actorType == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                if (group.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    group.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                    groupService.addNewEditedSmsGroup(group, conn);
                } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    groupService.editSmsGroup(group, conn);
                }

            }

            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void editAdsGroup(AdsGroupModel group, int actorType) throws CommonException {
        String methodName = "editAdsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            if (actorType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                if (group.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    groupService.editAdsGroup(group, conn);
                } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    AdsGroupModel parent = groupService.getAdsGroupParent(group.getGroupId(), group.getVersionId(), conn);
                    if (parent.getVersionId() != -1) { // if group has parent
                        groupService.deleteAdsGroupFiltersAndLOV(group, conn);//delete child
                        groupService.deleteAdsGroupFiles(group.getVersionId(), conn);
                        groupService.deleteAdsGroup(group.getVersionId(), conn);
                        group.setVersionId(parent.getVersionId());
                        group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                        groupService.editAdsGroup(group, conn);//edit in parent
                    } else if (parent.getVersionId() == -1) {// if group has NO parent
                        group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                        groupService.editAdsGroup(group, conn);
                    }
                }

            } else if (actorType == GeneralConstants.USER_TYPE_BUSINESS_VALUE) {
                if (group.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE) {
                    group.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                    groupService.addNewEditedAdsGroup(group, conn);
                } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    groupService.editAdsGroup(group, conn);
                }

            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public boolean AdsGroupHasParent(int groupEditID) throws CommonException {
        String methodName = "AdsGroupHasParent";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.adsGroupHasParent(groupEditID, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public SMSGroupModel getSmsGroupById(int groupId) throws CommonException {
        String methodName = "getSmsGroupById";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.getSmsGroupById(groupId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public AdsGroupModel getAdsGroupById(int groupId) throws CommonException {
        String methodName = "getAdsGroupById";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.getAdsGroupById(groupId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    /*Group name is used for editing purposes so the group with same name is not considered*/
    public AdsGroupModel getAdsGroupByPriority(int groupPriority, String groupName) throws CommonException {
        String methodName = "getAdsGroupByPriority";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.getAdsGroupByPriority(groupPriority, groupName, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public AdsGroupModel getAdsGroupByName(String groupName) throws CommonException {
        String methodName = "getAdsGroupByName";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.getAdsGroupByName(groupName, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteSmsGroup(SMSGroupModel group, int actorType) throws CommonException {
        String methodName = "deleteSmsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            if (actorType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE) {
                SMSGroupModel child = groupService.getSmsGroupParent(group.getGroupId(), group.getVersionId(), conn);
                if (child.getVersionId() != -1) {
                    groupService.deleteSmsGroupFiltersAndLOV(child, conn);//2
                    groupService.deleteSmsGroupFiles(child.getVersionId(), conn);//3
                    groupService.deleteSmsGroup(child.getVersionId(), conn);//1
                }
                groupService.deleteSmsGroupFiltersAndLOV(group, conn);//2
                groupService.deleteSmsGroupFiles(group.getVersionId(), conn);//3
                groupService.deleteSmsGroup(group.getVersionId(), conn);//1
            } else {
                groupService.changeSmsGroupStatus(group.getVersionId(), GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE, conn);
            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteAdsGroup(AdsGroupModel group, int actorType) throws CommonException {
        String methodName = "deleteAdsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            if ((group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE)
                    || (actorType == GeneralConstants.USER_TYPE_OPERATIONAL_VALUE)) {
                AdsGroupModel child = groupService.getAdsGroupParent(group.getGroupId(), group.getVersionId(), conn);
                if (child.getVersionId() != -1) {
                    groupService.deleteAdsGroupFiltersAndLOV(child, conn);//2
                    groupService.deleteAdsGroupFiles(child.getVersionId(), conn);//3
                    groupService.deleteAdsGroup(child.getVersionId(), conn);//1
                }
                groupService.deleteAdsGroupFiltersAndLOV(group, conn);//2
                groupService.deleteAdsGroupFiles(group.getVersionId(), conn);//3
                groupService.deleteAdsGroup(group.getVersionId(), conn);//1
            } else {
                groupService.changeAdsGroupStatus(group.getVersionId(), GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE, conn);
            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteSmsGroupVersions(int groupID) throws CommonException {
        String methodName = "deleteSmsGroupVersions";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            groupService.deleteSmsGroupVersions(groupID, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteAdsGroupVersions(int groupID) throws CommonException {
        String methodName = "deleteAdsGroupVersions";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            groupService.deleteAdsGroupVersions(groupID, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void changeSmsGroupStatus(int groupID, int status) throws CommonException {
        String methodName = "changeSmsGroupStatus";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            groupService.changeSmsGroupStatus(groupID, status, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void changeAdsGroupStatus(int groupID, int status) throws CommonException {
        String methodName = "changeAdsGroupStatus";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            groupService.changeAdsGroupStatus(groupID, status, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void changeSmsGroupEditedID(int groupID, int parentGroupID) throws CommonException {
        String methodName = "changeSmsGroupEditedID";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            groupService.changeSmsGroupEditedID(groupID, parentGroupID, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void changeAdsGroupEditedID(int groupID, int parentGroupID) throws CommonException {
        String methodName = "changeAdsGroupEditedID";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            groupService.changeAdsGroupEditedID(groupID, parentGroupID, conn);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public String getFilterQuery(ArrayList<FilterModel> filterArray) throws CommonException {
        String methodName = "getFilterQuery";
        try {
            GroupFilterService groupFilterService = new GroupFilterService();
            return groupFilterService.getFilterQuery(filterArray);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        }
    }

    public ArrayList<FilterModel> retrieveSmsGroupFilters(int groupId) throws CommonException {
        String methodName = "retrieveSmsGroupFilters";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.retrieveSmsGroupFilters(groupId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<FilterModel> retrieveAdsGroupFilters(int groupId) throws CommonException {
        String methodName = "retrieveAdsGroupFilters";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.retrieveAdsGroupFilters(groupId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<FileModel> retrieveSmsGroupFiles(int groupId) throws CommonException {
        String methodName = "retrieveSmsGroupFiles";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.retrieveSmsGroupFiles(groupId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<FileModel> retrieveAdsGroupFiles(int groupId) throws CommonException {
        String methodName = "retrieveAdsGroupFiles";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            return groupService.retrieveAdsGroupFiles(groupId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<FileModel> retrieveGroupFiles(int type, int groupId) throws CommonException {
        String methodName = "retrieveGroupFiles";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            if (type == GeneralConstants.ADS_GROUP) {
                AdsGroupService groupService = new AdsGroupService();
                return groupService.retrieveAdsGroupFiles(groupId, conn);
            } else if (type == GeneralConstants.SMS_GROUP) {
                SMSGroupService groupService = new SMSGroupService();
                return groupService.retrieveSmsGroupFiles(groupId, conn);
            } else {
                CampaignService campaignService = new CampaignService();
                return campaignService.retrieveCampaignFiles(groupId, conn);
            }
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public String getInstanceReloadCounter(int instanceID) throws CommonException {
        String methodName = "getInstanceReloadCounter";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SendingSmsInstancesService sendingSmsInstancesService = new SendingSmsInstancesService();
            return sendingSmsInstancesService.getInstanceReloadCounter(conn, instanceID);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public HashMap<String, String> getInstanceProperties(int instanceID) throws CommonException {
        String methodName = "getInstanceProperties";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SendingSmsInstancesService sendingSmsInstancesService = new SendingSmsInstancesService();
            return sendingSmsInstancesService.getInstanceProperties(conn, instanceID);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateInstanceShutdownFlag(int instanceID, String flag) throws CommonException {
        String methodName = "updateInstanceShutdownFlag";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SendingSmsInstancesService sendingSmsInstancesService = new SendingSmsInstancesService();
            sendingSmsInstancesService.updateInstanceShutdownFlag(conn, instanceID, flag);
            conn.commit();
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateInstanceReloadCounter(int instanceID, String flag) throws CommonException {
        String methodName = "updateInstanceShutdownFlag";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SendingSmsInstancesService sendingSmsInstancesService = new SendingSmsInstancesService();
            sendingSmsInstancesService.updateInstanceReloadCounter(conn, instanceID, flag);
            conn.commit();
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public String getInstanceShutdownFlag(int instanceID) throws CommonException {
        String methodName = "getInstanceShutdownFlag";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SendingSmsInstancesService sendingSmsInstancesService = new SendingSmsInstancesService();
            return sendingSmsInstancesService.getInstanceShutdownFlag(conn, instanceID);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    // END KerollosAsaad.
    public int getInterfaceInstaceReloadCounter(int instanceID) throws CommonException {
        String methodName = "getInterfaceInstaceReloadCounter";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            InterfacesInstancesService service = new InterfacesInstancesService();
            return service.getInstaceReloadCounter(conn, instanceID);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }

    public void updateInterfaceInstanceReloadCounter(int instanceID, int reloadCounter) throws CommonException {
        String methodName = "updateInterfaceInstanceReloadCounter";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            InterfacesInstancesService service = new InterfacesInstancesService();
            service.updateInstanceReloadCounter(conn, instanceID, reloadCounter);
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            try {
                DataSourceManger.closeConnection(conn);
            } catch (Exception ex) {
                CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
            }
        }
    }

    public UserModel retrieveUser(String userName) throws CommonException {

        Connection connection = null;
        UserModel userModel = null;
        try {
            connection = DataSourceManger.getConnection();
            UserService userService = new UserService();
            userModel = userService.retrieveUser(connection, userName);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [UserService]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [UserService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return userModel;
    }

    public void insertUser(UserModel user) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            UserService userService = new UserService();
            userService.insertUser(connection, user);
            DataSourceManger.commitConnection(connection);

        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertUser]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertUser]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateUser(UserModel user) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            UserService userService = new UserService();
            userService.updateUser(connection, user);
            DataSourceManger.commitConnection(connection);

        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateUser]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateUser]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<DWHElementModel> loadAllCommercialElements() throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.loadAllCommercialElements(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [loadAllCommercialElements]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [loadAllCommercialElements]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR); // TODO
                }
            }
        }
        return null;
    }

    public ArrayList<LookupModel> getDisplayTypeOperators(int displayTypeId) throws CommonException {
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            LookupService lookupService = new LookupService();
            return lookupService.getDisplayTypeOperators(displayTypeId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [getDisplayTypeOperators]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [getDisplayTypeOperators]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR); // TODO
                }
            }
        }
        return null;
    }

    public void insertMOMError(MOMErrorsModel mOMErrorsModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            MOMErrorService mErrorService = new MOMErrorService();
            mErrorService.insertMOMError(connection, mOMErrorsModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertMOMError]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    //Zain Used in transaction
    public void CountersReloader(Connection connection) throws CommonException {

        try {
            // connection = DataSourceManger.getConnection();
            ReloaderService reloaderService = new ReloaderService();
            reloaderService.changeReloadCounterSendingSMS(connection);
            reloaderService.changeReloadCounterSendinginterfaces(connection);
            // DataSourceManger.commitConnection(connection);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [CountersReloader]" + ex, ex);
            // DataSourceManger.rollBack(connection);
            throw ex;
        }
//        finally {
//            if (connection != null) {
//                DataSourceManger.closeConnection(connection);
//            }
//        }
    }

    public void CountersReloader() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ReloaderService reloaderService = new ReloaderService();
            reloaderService.changeReloadCounterSendingSMS(connection);
            reloaderService.changeReloadCounterSendinginterfaces(connection);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [CountersReloader]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }
    //Amal : service actions 
    ///////////////////////
    ///////////////////////

    public void createService(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        Connection conForQQuery = null;
        try {
            connection = DataSourceManger.getConnection();
            if (queueModel != null) {
                QueueService queueService = new QueueService();
                //A )) 1-- CREATE CONNECTION GOTTEN FROM QUEUEMODEL
                String dbURL = queueModel.getDatabaseUrl();
                String schemaName = queueModel.getSchemaName();
                String schemaPswd = queueModel.getSchemaPassword();
                if (((dbURL != null) && (!dbURL.isEmpty()))
                        && ((schemaName != null) && (!schemaName.isEmpty()))
                        && ((schemaPswd != null) && (!schemaPswd.isEmpty()))) {
                    conForQQuery = Utility.getDBConnection(dbURL, schemaName, schemaPswd);// Open Connection
                } else {
                    throw new CommonException("Connection is missing some or all arguments", GeneralConstants.QUEUE_QUERY.QUEUE_CONNECTION_EMPTY);
                }

                //A )) 2-- EXECUTE QUERIES BASED ON QUEUEMODEL INFO
                if ((conForQQuery != null) && (!conForQQuery.isClosed())) {
                    queueModel = queueService.executeQueriesUsingQueueInformation(conForQQuery, queueModel);
                } else {
                    throw new CommonException("Connection is closed or not gotten without reason", ErrorCodes.GET_CONNECTION_ERROR);
                }

                //B )) INSERT QUEUE IN DATABASE
                queueService.insertApplicationQueue(connection, queueModel, smscModels);
                //C )) SET QUEUE IN SERVICE
                serviceModel.setSelectedApplicationQueueID(queueModel.getVersionId());
            }
            serviceModel.setVersionId(CommonDAO.getNextId(connection, DBStruct.SERVICE.SEQUENCE_SEC));
            serviceModel.setServiceID(CommonDAO.getNextId(connection, DBStruct.SERVICE.SEQUENCE));
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            serviceManagmentService.insertService(connection, serviceModel, serviceWhitelistIPs);

            //D )) No Error Commit
            if (queueModel != null) {
                conForQQuery.commit();
            }
            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                if (conForQQuery != null) {
                    conForQQuery.rollback();

                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException rollex) {
                CommonLogger.errorLogger.error("Getting Caught Exception with error code"
                        + ErrorCodes.ROLL_BACK_ERROR + "---->  for [rollBack in createService]" + rollex, rollex);
                throw new CommonException(rollex.getMessage(), ErrorCodes.ROLL_BACK_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
            if (conForQQuery != null) {
                try {
                    Utility.closeDBConnection(conForQQuery);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createApplicationQueue]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void editApprovedService(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            if (queueModel != null && queueModel.getStatus() != GeneralConstants.STATUS_APPROVED_VALUE && queueModel.getStatus() != GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                QueueService queueService = new QueueService();
                //queueService.updateApplicationQueue(connection, queueModel, smscModels);
                queueService.deleteApplicationQueue(connection, queueModel);
                queueModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                queueService.insertApplicationQueue(connection, queueModel, smscModels);
                serviceModel.setSelectedApplicationQueueID(queueModel.getVersionId());
            } else {
                serviceModel.setAutoCreatdFlag(false);
            }
//            
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            serviceModel.setVersionId(CommonDAO.getNextId(connection, DBStruct.SERVICE.SEQUENCE_SEC));
            serviceModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
            serviceModel.setServiceName(serviceModel.getServiceName() + "_edited");
            serviceManagmentService.insertService(connection, serviceModel, serviceWhitelistIPs);

            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editApprovedService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editApprovedService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void editPendingService(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            if (queueModel != null && queueModel.getStatus() != GeneralConstants.STATUS_APPROVED_VALUE && queueModel.getStatus() != GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                QueueService queueService = new QueueService();
                //queueService.updateApplicationQueue(connection, queueModel, smscModels);
                queueService.deleteApplicationQueue(connection, queueModel);
                queueModel.setStatus(GeneralConstants.STATUS_PENDING_VALUE);
                queueService.insertApplicationQueue(connection, queueModel, smscModels);
                serviceModel.setSelectedApplicationQueueID(queueModel.getVersionId());
            } else {
                serviceModel.setAutoCreatdFlag(false);
            }
//            
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            serviceManagmentService.updateService(connection, serviceModel, serviceWhitelistIPs);

            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editPendingService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editPendingService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    /*Edits service information .. update .. pending cycle is not in the service .. the fn Updates service and reinserts white list*/
    public void approveService(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            if (queueModel != null && queueModel.getStatus() != GeneralConstants.STATUS_APPROVED_VALUE && queueModel.getStatus() != GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                QueueService queueService = new QueueService();
                queueService.deleteApplicationQueue(connection, queueModel);
                queueModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                queueService.insertApplicationQueue(connection, queueModel, smscModels);
                serviceModel.setSelectedApplicationQueueID(queueModel.getVersionId());
            } else {
                serviceModel.setAutoCreatdFlag(false);
            }

            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            serviceManagmentService.updateServiceReInsertWhiteList(connection, serviceModel, serviceWhitelistIPs);

//            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
//            ServiceModel approvedServiceVersion = serviceManagmentService.getParentServiceVersion(connection, serviceModel.getServiceID());
//            serviceManagmentService.deleteAllServiceVersions(connection, serviceModel, serviceWhitelistIPs);
//            serviceModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
//            if (approvedServiceVersion != null) {
//                serviceModel.setId(approvedServiceVersion.getId());
//                serviceModel.setServiceName(approvedServiceVersion.getServiceName());
//            }
//            serviceManagmentService.insertService(connection, serviceModel, serviceWhitelistIPs);
            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [approveService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteAllServiceVersions(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            //A > check for campaign reference
            Boolean campaignExists = serviceManagmentService.checkCampaignServiceByService(serviceModel, connection);
            if (!campaignExists) {
                if (queueModel != null && queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    QueueService queueService = new QueueService();
                    queueService.deleteApplicationQueue(connection, queueModel);
                }
                serviceManagmentService.deleteAllServiceVersions(connection, serviceModel, serviceWhitelistIPs);
            } else {
                throw new CommonException("Service" + serviceModel.getServiceName(), ErrorCodes.INTEGRITY_CONSTRAINT_ERROR);
            }
            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteAllServiceVersions]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteAllServiceVersions]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deleteApprovedService(ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
//            serviceModel.setId(CommonDAO.getNextId(connection, DBStruct.SERVICE.SEQUENCE_SEC));
//            serviceModel.setStatus(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE);
//            serviceManagmentService.insertService(connection, serviceModel, serviceWhitelistIPs);
            serviceModel.setStatus(GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE);
            serviceManagmentService.updateService(connection, serviceModel, serviceWhitelistIPs);
            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApprovedService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteApprovedService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void deletePendingService(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();

            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            Boolean campaignExists = serviceManagmentService.checkCampaignServiceByService(serviceModel, connection);
            if (!campaignExists) {
                if (queueModel != null && queueModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                    QueueService queueService = new QueueService();
                    queueService.deleteApplicationQueue(connection, queueModel);
                }
                serviceManagmentService.deleteService(connection, serviceModel, serviceWhitelistIPs);
            } else {
                throw new CommonException("Campaign reference", ErrorCodes.INTEGRITY_CONSTRAINT_ERROR);
            }
            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deletePendingService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deletePendingService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void rejectService(QueueModel queueModel, ArrayList<SMSCModel> smscModels, ServiceModel serviceModel, List<String> serviceWhitelistIPs) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                serviceModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                serviceManagmentService.updateService(connection, serviceModel, serviceWhitelistIPs);
            } else if (serviceModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                deletePendingService(queueModel, smscModels, serviceModel, serviceWhitelistIPs);
            }

            connection.commit();

        } catch (Exception ex) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
                CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectService]" + ex, ex);
                if (ex instanceof CommonException) {
                    throw (CommonException) ex;
                } else if (ex instanceof SQLException) {
                    throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
                }
            } catch (SQLException ex1) {
                Logger.getLogger(MainService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectService]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    //End Amal
    public ArrayList<UserModel> getUsersList() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            UserService userService = new UserService();
            ArrayList<UserModel> uersModels = userService.getUsersList(connection);
            return uersModels;
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getSMSCs]" + ex, ex);
                    throw ex;
                }
            }
        }
    }

    public void deleteUser(UserModel userModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            UserService userService = new UserService();
            userService.deleteUser(connection, userModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {

            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteUser]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    ////////////////////CAMPAIGNS////////////////////////
    public ArrayList<CampaignModel> getCampaigns() throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            ArrayList<CampaignModel> campaignModels = campaignService.getCampaigns(connection);
            return campaignModels;
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getCampaigns]" + ex, ex);
                    throw ex;
                }
            }
        }

    }

    public CampaignModel retrieveConnectedCampaign(CampaignModel campaignModel) throws CommonException {

        Connection connection = null;
        CampaignModel returnedModel = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            returnedModel = campaignService.retrieveConnectedCampaign(connection, campaignModel);

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return returnedModel;
    }

    public void deleteCampaign(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignService.deleteCampaign(connection, campaignModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {

            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteCampaign]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void deleteParentAndChildCampaign(CampaignModel parent, CampaignModel child) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignService.deleteCampaign(connection, parent);
            campaignService.deleteCampaign(connection, child);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [deleteParentAndChildCampaign]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);

            }
        }
    }

    public void ChangeCampaignStatusToDelete(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignService.ChangeCampaignStatusToDelete(connection, campaignModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [ChangeCampaignStatusToDelete]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);

            }
        }
    }

    public void editCampaign(CampaignModel campaignModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignService.editCampaign(connection, campaignModel);
            DataSourceManger.commitConnection(connection);
        } catch (CommonException e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editCampaign]" + e, e);
            DataSourceManger.rollBack(connection);
            throw e;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public void editEditedCampaign(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignService.editEditedCampaign(connection, campaignModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [editEditedCampaign]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public CampaignModel createCampaign(CampaignModel campaignModel, ArrayList<ServiceModel> serviceList) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignModel = campaignService.createCampaign(connection, campaignModel, serviceList);
            DataSourceManger.commitConnection(connection);
            return campaignModel;
        } catch (CommonException ex) {

            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [createCampaign]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }

    }

    public CampaignModel getCampaignByName(String name) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            CampaignModel campaignModel = campaignService.getCampaignByName(connection, name);
            return campaignModel;
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getCampaignByName]" + ex, ex);
                    throw ex;
                }
            }
        }

    }

    public CampaignModel getCampaignByPriority(int priority) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            CampaignModel campaignModel = campaignService.getCampaignByPriority(connection, priority);
            return campaignModel;
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getCampaignByPriority]" + ex, ex);
                    throw ex;
                }
            }
        }

    }

    public void ChangeCampaignStatus(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            campaignService.ChangeCampaignStatus(connection, campaignModel);
            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [ChangeCampaignStatus]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;

        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);

            }
        }
    }

    public ArrayList<FilterModel> retrieveCampaignFilters(long campaignId) throws CommonException {
        String methodName = "retrieveCampaignFilters";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            return campaignService.retrieveCampaignFilters(campaignId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<FileModel> retrieveCampaignFiles(long campaignId) throws CommonException {
        String methodName = "retrieveCampaignFiles";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            CampaignService campaignService = new CampaignService();
            return campaignService.retrieveCampaignFiles(campaignId, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void approveCampaign(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            CampaignModel returnedModel = retrieveConnectedCampaign(campaignModel);
            CampaignService campaignService = new CampaignService();
            if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                campaignModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                if (returnedModel == null) {
                    campaignService.editEditedCampaign(connection, campaignModel);
                } else {
                    //delete returnedModel
                    returnedModel.setLastModifiedBy(campaignModel.getLastModifiedBy());
                    returnedModel.setArabicScript(campaignModel.getArabicScript());
                    returnedModel.setCampaignDescription(campaignModel.getCampaignDescription());
                    returnedModel.setCampaignName(campaignModel.getCampaignName());
                    returnedModel.setCampaignStatus(campaignModel.getCampaignStatus());
                    returnedModel.setControlPercentage(campaignModel.getControlPercentage());
                    returnedModel.setEditedDescription(campaignModel.getEditedDescription());
                    returnedModel.setEndDate(campaignModel.getEndDate());
                    returnedModel.setEnglishScript(campaignModel.getEnglishScript());
                    returnedModel.setFilterQuery(campaignModel.getFilterQuery());
                    returnedModel.setMaxNumberOfCommunications(campaignModel.getMaxNumberOfCommunications());
                    returnedModel.setMaxTargetedCustomers(campaignModel.getMaxTargetedCustomers());
                    returnedModel.setPriority(campaignModel.getPriority());
                    returnedModel.setStartDate(campaignModel.getStartDate());
                    returnedModel.setStatus(campaignModel.getStatus());
                    returnedModel.setFilterList(campaignModel.getFilterList());
                    returnedModel.setFilesModel(campaignModel.getFilesModel());

                    campaignService.deleteCampaign(connection, campaignModel);

                    campaignService.editEditedCampaign(connection, returnedModel);

                }
            } else if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                if (returnedModel != null) {
                    if (returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                        campaignService.deleteCampaign(connection, campaignModel);
                        campaignService.deleteCampaign(connection, returnedModel);
                    } //lw state bta3 returned model approved yb2a d parent msh child
                    //delete directly
                    else if (returnedModel.getStatus() == GeneralConstants.STATUS_APPROVED_VALUE || returnedModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) { //aw pending for deletion
                        campaignService.deleteCampaign(connection, campaignModel);
                    }

                } else {
                    campaignService.deleteCampaign(connection, campaignModel);
                }
            }

            DataSourceManger.commitConnection(connection);

        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [approveCampaign]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void rejectCampaign(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            // CampaignModel returnedModel = retrieveConnectedCampaign(campaignModel);
            CampaignService campaignService = new CampaignService();
            if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                campaignService.deleteCampaign(connection, campaignModel);
            } else if (campaignModel.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
//                campaignModel.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
//                campaignService.editEditedCampaign(connection, campaignModel);
                campaignService.ChangeCampaignStatusToApproved(connection, campaignModel);
            }

            DataSourceManger.commitConnection(connection);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [rejectCampaign]" + ex, ex);
            DataSourceManger.rollBack(connection);
            throw ex;
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    //////////////////End CAMPAIGNS//////////////////////
    public ArrayList<MenuModel> retrieveMenuList(int userType) throws CommonException {
        Connection connection = null;
        ArrayList<MenuModel> menuList = null;
        try {
            connection = DataSourceManger.getConnection();
            MenuService menuService = new MenuService();
            menuList = menuService.getMenuList(connection, userType);
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [retrieveMenuList]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
        return menuList;
    }

    public ArrayList<ReportsViewModel> getReportsList() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            LookupService lookUpService = new LookupService();
            return lookUpService.getReportsList(connection);
        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [getReportsList]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                DataSourceManger.closeConnection(connection);
            }
        }
    }

    public void rejectSmsGroup(SMSGroupModel group) throws CommonException {
        String methodName = "rejectSmsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            if (group.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                //groupService.editSmsGroup(group, conn);
                groupService.changeSmsGroupStatus(group.getVersionId(), GeneralConstants.STATUS_APPROVED_VALUE, conn);
            } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                groupService.deleteSmsGroup(group.getVersionId(), conn);
                groupService.deleteSmsGroupFiltersAndLOV(group, conn);
                groupService.deleteSmsGroupFiles(group.getVersionId(), conn);
            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void rejectAdsGroup(AdsGroupModel group) throws CommonException {
        String methodName = "rejectAdsGroup";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            AdsGroupService groupService = new AdsGroupService();
            if (group.getStatus() == GeneralConstants.STATUS_PENDING_FOR_DELETION_VALUE) {
                group.setStatus(GeneralConstants.STATUS_APPROVED_VALUE);
                // groupService.editAdsGroup(group, conn);
                groupService.changeAdsGroupStatus(group.getVersionId(), GeneralConstants.STATUS_APPROVED_VALUE, conn);
            } else if (group.getStatus() == GeneralConstants.STATUS_PENDING_VALUE) {
                groupService.deleteAdsGroupFiles(group.getVersionId(), conn);
                groupService.deleteAdsGroupFiltersAndLOV(group, conn);
                groupService.deleteAdsGroup(group.getVersionId(), conn);
            }
            DataSourceManger.commitConnection(conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public Timestamp getMidNightDatabaseTime() throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();

            CommonService commonService = new CommonService();
            return commonService.getMidNightDatabaseTime(con);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->getMidNightDatabaseTime", ex);
            throw ex;
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public Timestamp getCurrentDatabaseTime() throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();

            CommonService commonService = new CommonService();
            return commonService.getCurrentDatabaseTime(con);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->getCurrentDatabaseTime", ex);
            throw ex;
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public SMSGroupModel getSmsGroupByPriority(int groupPriority, String groupName) throws CommonException {
        String methodName = "getSmsGroupByPriority";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.getSmsGroupByPriority(groupPriority, groupName, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public SMSGroupModel getSmsGroupByName(String groupName) throws CommonException {
        String methodName = "getSmsGroupByName";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSGroupService groupService = new SMSGroupService();
            return groupService.getSmsGroupByName(groupName, conn);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public CustomerConfigAndGrpModel getCustomerSpecialConfigAndGrp(CustomerConfigAndGrpModel customerModel, int runId, String logPrefix) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            CustomerService customerService = new CustomerService();
            CustomerConfigurationsModel customerConfigurations = customerService.getCustomerConfigurations(con, customerModel.getMsisdn());
            if (customerConfigurations == null) {
                customerModel.setHasCustomConfig(false);
//                CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + " doesn't have specific configurations");
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Customer Doesnot have Specific Configurations")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                        .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits()).build());
            } else {

                customerModel.setHasCustomConfig(true);
                customerModel.setDailyThresholdAds(customerConfigurations.getDailyThresholdAds());
                customerModel.setDailyThresholdSms(customerConfigurations.getDailyThresholdSms());
                customerModel.setDoNotContact(customerConfigurations.getDoNotContact());
                customerModel.setMonthlyThresholdAds(customerConfigurations.getMonthlyThresholdAds());
                customerModel.setMonthlyThresholdSms(customerConfigurations.getMonthlyThresholdSms());
                customerModel.setWeeklyThresholdAds(customerConfigurations.getWeeklyThresholdAds());
                customerModel.setWeeklyThresholdSms(customerConfigurations.getWeeklyThresholdSms());
//                CommonLogger.businessLogger.info(logPrefix + "Customer MSISDN:" + customerModel.getMsisdn() + " Last two digits:" + customerModel.getLastTwoDigits() + "  specific configuration"
//                        + " dailyThresholdSMS:" + customerModel.getDailyThresholdSms() + " weeklyThresholdSMS:" + customerModel.getWeeklyThresholdSms() + " monthlyThresholdSMS:" + customerModel.getMonthlyThresholdSms()
//                        + " doNotContact: " + customerModel.getDoNotContact());
                CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Specific Configurations")
                        .put(GeneralConstants.StructuredLogKeys.MSISDN, customerModel.getMsisdn())
                        .put(GeneralConstants.StructuredLogKeys.LAST_MSISDN_TWO_DIGITS, customerModel.getLastTwoDigits())
                        .put(GeneralConstants.StructuredLogKeys.DAILY_THRESHOLD_SMS, customerModel.getDailyThresholdSms())
                        .put(GeneralConstants.StructuredLogKeys.WEEKLY_THRESHOLD_SMS, customerModel.getWeeklyThresholdSms())
                        .put(GeneralConstants.StructuredLogKeys.MONTHLY_THRESHOLD_SMS, customerModel.getMonthlyThresholdSms())
                        .put(GeneralConstants.StructuredLogKeys.DONOT_CONTACT, customerModel.getDoNotContact()).build());
            }
            SMSCustomersGroupsService smsCustomersGroupsService = new SMSCustomersGroupsService();
            return smsCustomersGroupsService.getCustomerGrp(con, customerModel, runId, logPrefix);

        } catch (Exception ex) {
            CommonLogger.businessLogger.error(logPrefix + "Getting Caught Exception---->getCustomerSpecialConfigAndGrp-->" + ex, ex);

            CommonLogger.errorLogger.error(logPrefix + "Getting Caught Exception---->getCustomerSpecialConfigAndGrp-->" + ex, ex);
            //We need to change error code
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public boolean checkCustomerExist(String MSISDN, String transId, long csMsgId) throws CommonException {
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            DWHProfileService dwhProfileService = new DWHProfileService();
            return dwhProfileService.checkCustomerExist(con, MSISDN);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->checkCustomerExist" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public ArrayList<CustomersCampaignsModel> getCustomerCampaignsByCampaign(Integer runId, Long campaignId, Integer suspendedNumber) throws CommonException {
        String methodName = "getCustomerCampaignsByCampaign";
        Connection con = null;
        try {
            con = DataSourceManger.getConnection();
            CampaignService service = new CampaignService();
            return service.getCustomerCampaignsByCampaign(con, runId, campaignId, suspendedNumber);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (con != null) {
                DataSourceManger.closeConnection(con);
            }
        }
    }

    public Boolean checkServiceName(String serviceName) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            return serviceManagmentService.checkService(connection, serviceName);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkServiceName" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for checkServiceName" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
                }
            }
        }

    }

    ////////Fix Issue /////////////
    public ArrayList<ServiceModel> getCampaignServices(CampaignModel campaignModel) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            ArrayList<ServiceModel> serviceModels = serviceManagmentService.getCampaignServices(connection, campaignModel);
            return serviceModels;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCampaignServices" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for getCampaignServices" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
                }
            }
        }
    }
    ///////End fix issue //////////////

    //2595
    public void insertSMSBulkFile(SMSBulkFileModel fileModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            service.insertSMSFile(connection, fileModel);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertSMSBulkFile]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertSMSBulkFile]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertSMSBulkFile]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateSMSBulkFileStatus(SMSBulkFileModel fileModel) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            service.updateSMSBulkFileStatus(connection, fileModel);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkFileStatus]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkFileStatus]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkFileStatus]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public String getSMSBridgingInstance(int instanceID, String key) throws CommonException {
        String methodName = "getSMSBridgingInstance";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSBridgingInstancesService smsBridgingInstancesService = new SMSBridgingInstancesService();
            return smsBridgingInstancesService.getInstance(conn, instanceID, key);
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void updateSMSBridgingInstance(int instanceID, String key, String value) throws CommonException {
        String methodName = "updateSMSBridgingInstance";
        Connection conn = null;
        try {
            conn = DataSourceManger.getConnection();
            SMSBridgingInstancesService smsBridgingInstancesService = new SMSBridgingInstancesService();
            smsBridgingInstancesService.updateInstance(conn, instanceID, key, value);
            conn.commit();
        } catch (Exception ex) {
            DataSourceManger.rollBack(conn);
            CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (conn != null) {
                try {
                    DataSourceManger.closeConnection(conn);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error(MainService.class.getName() + " || " + "Getting Caught Exception---->  for [" + methodName + "]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public ArrayList<QueueModel> getApplicationQueuesBySetOfNames(List<String> queueNames, int queueType) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            QueueService queueService = new QueueService();
            ArrayList<QueueModel> queueModels = queueService.getApplicationQueuesBySetOfNames(connection, queueNames, queueType);
            return queueModels;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueuesBySetOfNames]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getApplicationQueuesBySetOfNames]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return null;
    }
    //2595 end

    public void insertSMSBulkRecords(ArrayList<InputModel> dataRecords, String fileName) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            service.insertSMSBulkRecords(connection, dataRecords, fileName);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertSMSBulkFile]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertSMSBulkFile]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertSMSBulkFile]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void archiveSMSBulkRecords(HashMap<String, ArrayList> interfaceResult, ArrayList<Long> resultIds) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            service.insertSMSBulkRecordsArchive(connection, interfaceResult, resultIds);
            service.deleteSMSBulkRecords(connection, resultIds);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkRecords]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkRecords]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkRecords]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void archiveFailedSMSBulkRecords(ArrayList<Long> resultIds) throws CommonException {

        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            service.insertFailedSMSBulkRecordsArchive(connection, resultIds);
            service.deleteSMSBulkRecords(connection, resultIds);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkRecords]" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
        } finally {
            if (connection != null) {
                try {
                    connection.commit();
                } catch (SQLException ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkRecords]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [updateSMSBulkRecords]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public RequestPreparator getBatchRecords(int threadNumber, int numberOfThreads, int batchSize, ArrayList<Long> resultIds) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            return service.getSMSBulkRecords(connection, threadNumber, numberOfThreads, batchSize, resultIds);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getBatchRecords]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getBatchRecords]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return null;

    }

    public int getBulkRecordsCount() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            SMSBulkFilesService service = new SMSBulkFilesService();
            return service.getBulkRecordsCount(connection);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getBulkRecordsCount]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [getBulkRecordsCount]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
        return 0;
    }

    public boolean checkDatasource() throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            UtilityService service = new UtilityService();
            return service.checkDatasource(connection);
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [checkDatasource]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else if (ex instanceof SQLException) {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
            throw new CommonException("Exception Occured: " + ex.getMessage(), 0);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [checkDatasource]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public HashMap<String, SMSCInterfaceClientModel> retrieveClients() throws CommonException {

        Connection connection = null;
        HashMap<String, SMSCInterfaceClientModel> clientsMap = null;
        try {
            connection = DataSourceManger.getConnection();
            ClientService clientService = new ClientService();
            clientsMap = new HashMap<>();
            clientsMap = clientService.retrieveClients(connection);

        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [ClientService]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [ClientService]" + ex, ex);
                }
            }
        }
        return clientsMap;
    }

    public void insertLog(List<LogModel> logModels) throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            LogService logService = new LogService();
            logService.insertLog(connection, logModels);
            DataSourceManger.commitConnection(connection);

        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public void insertCsSmsInterfaceHistoryModel(List<CsSmscInterfaceHistoryModel> csSmsInterfaceHistoryModels)
            throws CommonException {
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            LogService logService = new LogService();
            logService.insertCsSmscInterfaceHistoryModel(connection, csSmsInterfaceHistoryModels);
            DataSourceManger.commitConnection(connection);

        } catch (Exception ex) {
            DataSourceManger.rollBack(connection);
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex, ex);
            if (ex instanceof CommonException) {
                throw (CommonException) ex;
            } else {
                throw new CommonException(ex.getMessage(), ErrorCodes.UNKOWN_ERROR);
            }
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for [insertLog]" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.CLOSE_CONNECTION_ERROR);
                }
            }
        }
    }

    public static void registerRabbitmqConsumer(Channel channel, Consumer consumer, QueueModel queueModel, String queuePrefix) throws CommonException {
        RabbitmqQueueService rabbitmqQueueService = new RabbitmqQueueService();
        String queueName = queuePrefix + queueModel.getAppName();
        rabbitmqQueueService.registerDequeuer(channel, consumer, queueName, Long.toString(queueModel.getAppId()));
    }

    public HashMap<String, ServiceModel> getQueueServiceMap() throws CommonException {
        HashMap<String, ServiceModel> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            ArrayList<ServiceModel> serviceModels = serviceManagmentService.getServices(connection);
            serviceModels.forEach((serviceModel) -> {
                map.put(serviceModel.getSelectedApplicationQueueModel().getAppName(), serviceModel);
            });
            return map;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getQueueServiceMap" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for getQueueServiceMap" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
                }
            }
        }
    }

    public HashMap<String, QueueModel> getServiceQueueMap() throws CommonException {
        HashMap<String, QueueModel> map = new HashMap<>();
        Connection connection = null;
        try {
            connection = DataSourceManger.getConnection();
            ServiceManagmentService serviceManagmentService = new ServiceManagmentService();
            ArrayList<ServiceModel> serviceModels = serviceManagmentService.getServices(connection);
            serviceModels.forEach((serviceModel) -> {
                CommonLogger.businessLogger.debug(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE,
                        "Service: " + serviceModel.getServiceName()
                        + " | Queue: " + serviceModel.getSelectedApplicationQueueModel().getAppName()).build());
                map.put(serviceModel.getServiceName(), serviceModel.getSelectedApplicationQueueModel());
            });
            return map;
        } catch (Exception ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for getQueueServiceMap" + ex, ex);
            throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
        } finally {
            if (connection != null) {
                try {
                    DataSourceManger.closeConnection(connection);
                } catch (Exception ex) {
                    CommonLogger.errorLogger.error("Getting Caught Exception---->  for getQueueServiceMap" + ex, ex);
                    throw new CommonException(ex.getMessage(), ErrorCodes.SERVICE_RETREIVE_DB_ERROR);
                }
            }
        }
    }
    
}
