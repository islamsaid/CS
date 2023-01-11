/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.managedBeans;

import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.CustomerConfigurationModel;
import com.asset.contactstrategy.common.service.MainService;
import com.asset.contactstrategy.utils.Constants;
import com.asset.contactstrategy.utils.Utility;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean(name = "customerConfiguration")
public final class CustomerConfigurationBean implements Serializable {

    private CustomerConfigurationModel configurationModel;
    private String checkPanneleditable;
    private String msisdn;
    private String pannelsVisibilty;
    private String addAndCancelButtonVisibilty;
    private String saveButtonVisibilty;
    private String deleteButtonVisibilty;

    @PostConstruct
    public void init() {
        setCheckPanneleditable(GeneralConstants.ENABLE);
    }

//    public boolean validateMSISDNFormat() throws CommonException {
//        String methodName = "validateMSISDNFormat";
//        Matcher matcher = null;
//        try {
//            Pattern pattern = null;
//            Pattern.compile(GeneralConstants.MSISDN_VALIDATE_PATTERN);
//            matcher = pattern.matcher(msisdn);
//        } catch (Exception e) {
//            throw new CommonException("Error in validating MSISDN in CLASS:" + "validateMSISDNFormat" + " method: " + methodName + e, ErrorCodes.UNKOWN_ERROR);
//        }
//        return matcher.matches();
//    }
    public void checkMSISDNAvailability() {

        try {
            if((msisdn==null)||((msisdn!=null)&&(msisdn.equals("")))){
                Utility.showErrorMessage(null, Constants.MISSING_DATA);
                return ;
            }
            String value = null;
            try {//A) VALIDATE MSISDN
                //CSPhase1.5 | Esmail.Anbar | Updating Code to use new Validation Function
                value = com.asset.contactstrategy.common.utils.Utility.validateMSISDNFormatSMPP(msisdn);
            } catch (CommonException invalidmsisdn) {
                CommonLogger.errorLogger.error(UploadAndParseFileBean.class.getName() + " || "
                        + "Caught invalid MSISDN with value" + msisdn);
            }
            if(value==null){
                Utility.showErrorMessage(null, Constants.INVALID_MSISDN);
                return ;
            }else{
                msisdn = value;
            }
            setPannelsVisibilty(GeneralConstants.DISABLE);
            setCheckPanneleditable(GeneralConstants.DISABLE);
            MainService mainService = new MainService();
            configurationModel = mainService.getMSISDNAvailibility(msisdn);

            if (configurationModel == null) {
                setAddAndCancelButtonVisibilty(GeneralConstants.DISABLE);
                setDeleteButtonVisibilty(GeneralConstants.ENABLE);
                setSaveButtonVisibilty(GeneralConstants.ENABLE);

            } else {
                setAddAndCancelButtonVisibilty(GeneralConstants.ENABLE);
                setDeleteButtonVisibilty(GeneralConstants.DISABLE);
                setSaveButtonVisibilty(GeneralConstants.DISABLE);
            }
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".checkMSISDNAvailability]", ex);
            if (ex.getErrorCode().equals(ErrorCodes.UNKOWN_ERROR)) {
                Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
            } else {
                Utility.showErrorMessage(null, GeneralConstants.DATABASE_ERROR);
            }

        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".checkMSISDNAvailability]", e);
            Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
        }

    }

    public String insertNewCustomerConfig() {

        try {
            MainService mainService = new MainService();
            
            if (this.configurationModel.getDailyThreshold() > this.configurationModel.getWeeklyThreshold()
                        || this.configurationModel.getDailyThreshold() > this.configurationModel.getMounthlyThreshold()) {
                Utility.showErrorMessage(null, Constants.ERROR_DAILY_THRESHOLD);
                return null;
            }
            if (this.configurationModel.getWeeklyThreshold() > this.configurationModel.getMounthlyThreshold()) {
                Utility.showErrorMessage(null, Constants.ERROR_MONTHLY_THRESHOLD);
                return null;
            }
            if (this.configurationModel.getDailyCampain()> this.configurationModel.getWeeklyCampain()
                        || this.configurationModel.getDailyCampain() > this.configurationModel.getMounthlyCampain()) {
                Utility.showErrorMessage(null, Constants.ERROR_DAILY_CAMPAIGN);
                return null;
            }
            if (this.configurationModel.getWeeklyCampain() > this.configurationModel.getMounthlyCampain()) {
                Utility.showErrorMessage(null, Constants.ERROR_MONTHLY_CAMPAIGN);
                return null;
            }
            
            mainService.insertCustomer(configurationModel, msisdn);
            Utility.showInfoMessage(null, Constants.ITEM_ADDED, "MSISDN", msisdn);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".insertNewCustomerConfig]", ex);

            if (ex.getErrorCode().equals(ErrorCodes.UNKOWN_ERROR)) {
                Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
            } else {
                Utility.showErrorMessage(null, Constants.ADD_ITEM_FAILURE, "MSISDN", msisdn);
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".insertNewCustomerConfig]", e);
            Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
        }
        setCheckPanneleditable(GeneralConstants.ENABLE);
        setPannelsVisibilty(GeneralConstants.ENABLE);
        setAddAndCancelButtonVisibilty(GeneralConstants.ENABLE);
        setDeleteButtonVisibilty(GeneralConstants.ENABLE);
        setSaveButtonVisibilty(GeneralConstants.ENABLE);
        return "CustomerConfiguration.xhtml?faces-redirect=true";
    }

    public void cancel() {
        setCheckPanneleditable(GeneralConstants.ENABLE);
        setPannelsVisibilty(GeneralConstants.ENABLE);
        setAddAndCancelButtonVisibilty(GeneralConstants.ENABLE);
        setDeleteButtonVisibilty(GeneralConstants.ENABLE);
        setSaveButtonVisibilty(GeneralConstants.ENABLE);
    }

    public String updateCustomerConfig() {

        try {
            MainService mainService = new MainService();
            
            if (this.configurationModel.getDailyThreshold() > this.configurationModel.getWeeklyThreshold()
                        || this.configurationModel.getDailyThreshold() > this.configurationModel.getMounthlyThreshold()) {
                Utility.showErrorMessage(null, Constants.ERROR_DAILY_THRESHOLD);
                return null;
            }
            if (this.configurationModel.getWeeklyThreshold() > this.configurationModel.getMounthlyThreshold()) {
                Utility.showErrorMessage(null, Constants.ERROR_MONTHLY_THRESHOLD);
                return null;
            }
            if (this.configurationModel.getDailyCampain()> this.configurationModel.getWeeklyCampain()
                        || this.configurationModel.getDailyCampain() > this.configurationModel.getMounthlyCampain()) {
                Utility.showErrorMessage(null, Constants.ERROR_DAILY_CAMPAIGN);
                return null;
            }
            if (this.configurationModel.getWeeklyCampain() > this.configurationModel.getMounthlyCampain()) {
                Utility.showErrorMessage(null, Constants.ERROR_MONTHLY_CAMPAIGN);
                return null;
            }
            
            mainService.updateCustomer(configurationModel, msisdn);
            Utility.showInfoMessage(null, Constants.ITEM_EDITED, "MSISDN", msisdn);

        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".updateCustomerConfig]", ex);
            Utility.showErrorMessage(null, Constants.EDIT_ITME_FAILURE, "MSISDN", msisdn);
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".updateCustomerConfig]", e);
            Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
        }
        setCheckPanneleditable(GeneralConstants.ENABLE);
        setPannelsVisibilty(GeneralConstants.ENABLE);
        setAddAndCancelButtonVisibilty(GeneralConstants.ENABLE);
        setDeleteButtonVisibilty(GeneralConstants.ENABLE);
        setSaveButtonVisibilty(GeneralConstants.ENABLE);
        return "CustomerConfiguration.xhtml?faces-redirect=true";
    }

    public void deleteCustomerConfig() {

        try {
            MainService mainService = new MainService();
            mainService.deleteCustomer(msisdn);
            Utility.showInfoMessage(null, Constants.ITEM_DELETED, "MSISDN", msisdn);
        } catch (CommonException ex) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".deleteCustomerConfig]", ex);
            if (ex.getErrorCode().equals(ErrorCodes.UNKOWN_ERROR)) {
                Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
            } else {
                Utility.showErrorMessage(null, Constants.DELETE_ITEM_FAILURE, "MSISDN", msisdn);
            }
        } catch (Exception e) {
            CommonLogger.errorLogger.error("Getting Caught Exception---->  for [" + CustomerConfigurationBean.class.getName() + ".deleteCustomerConfig]", e);
            Utility.showErrorMessage(null, GeneralConstants.GENERAL_ERROR, " ");
        }
        setCheckPanneleditable(GeneralConstants.ENABLE);
        setPannelsVisibilty(GeneralConstants.ENABLE);
        setAddAndCancelButtonVisibilty(GeneralConstants.ENABLE);
        setDeleteButtonVisibilty(GeneralConstants.ENABLE);
        setSaveButtonVisibilty(GeneralConstants.ENABLE);

    }

    public CustomerConfigurationModel getConfigurationModel() {
        if (configurationModel == null) {
            configurationModel = new CustomerConfigurationModel();
        }
        return configurationModel;
    }

    public void setConfigurationModel(CustomerConfigurationModel configurationModel) {
        this.configurationModel = configurationModel;
    }

    /**
     * @return the msisdn
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * @param msisdn the msisdn to set
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * @return the saveButtonVisibilty
     */
    public String getSaveButtonVisibilty() {
        return saveButtonVisibilty;
    }

    /**
     * @param saveButtonVisibilty the saveButtonVisibilty to set
     */
    public void setSaveButtonVisibilty(String saveButtonVisibilty) {
        this.saveButtonVisibilty = saveButtonVisibilty;
    }

    /**
     * @return the deleteButtonVisibilty
     */
    public String getDeleteButtonVisibilty() {
        return deleteButtonVisibilty;
    }

    /**
     * @param deleteButtonVisibilty the deleteButtonVisibilty to set
     */
    public void setDeleteButtonVisibilty(String deleteButtonVisibilty) {
        this.deleteButtonVisibilty = deleteButtonVisibilty;
    }

    /**
     * @return the pannelsVisibilty
     */
    public String getPannelsVisibilty() {
        return pannelsVisibilty;
    }

    /**
     * @param pannelsVisibilty the pannelsVisibilty to set
     */
    public void setPannelsVisibilty(String pannelsVisibilty) {
        this.pannelsVisibilty = pannelsVisibilty;
    }

    /**
     * @return the checkPanneleditable
     */
    public String getCheckPanneleditable() {
        return checkPanneleditable;
    }

    /**
     * @param checkPanneleditable the checkPanneleditable to set
     */
    public void setCheckPanneleditable(String checkPanneleditable) {
        this.checkPanneleditable = checkPanneleditable;
    }

    /**
     * @return the addAndCancelButtonVisibilty
     */
    public String getAddAndCancelButtonVisibilty() {
        return addAndCancelButtonVisibilty;
    }

    /**
     * @param addAndCancelButtonVisibilty the addAndCancelButtonVisibilty to set
     */
    public void setAddAndCancelButtonVisibilty(String addAndCancelButtonVisibilty) {
        this.addAndCancelButtonVisibilty = addAndCancelButtonVisibilty;
    }

}
