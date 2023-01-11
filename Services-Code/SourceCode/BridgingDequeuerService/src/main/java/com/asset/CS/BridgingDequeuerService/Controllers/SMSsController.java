/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.Controllers;

import com.asset.CS.BridgingDequeuerService.beans.ManagerBean;
import com.asset.CS.BridgingDequeuerService.configuration.SpringConfiguration;
import com.asset.CS.BridgingDequeuerService.services.MainService;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.DequeuerResponseModel;
import com.asset.contactstrategy.common.utils.Utility;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mostafa.kashif
 */
@RestController
@RequestMapping(value = "/DequeuerService/")
public class SMSsController {

    @Autowired
    public MainService databaseService;
    @Autowired
    public ManagerBean managerBean;
    // private String transId;

    @RequestMapping(value = "/SMSs", method = RequestMethod.GET)
    public DequeuerResponseModel getSMSs(HttpServletRequest request, @RequestParam("requestId") String requestId, @RequestParam("queueName") String queueName, @RequestParam("waitingTime") int waitingTime, @RequestParam("batchSize") int batchSize) throws CommonException {
        try {
            // transId = Utility.generateTransId("DEQBRDGSMS");
            // CommonLogger.businessLogger.info("Starting handling request with inputs queueName:" + queueName + " waitingTime:" + waitingTime + " batchSize:" + batchSize);
            request.setAttribute("transId", Utility.generateTransId("DEQBRDGSMS"));
            ThreadContext.put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString());
//        CommonLogger.businessLogger.info("Starting handling request for SMSs with inputs requestId: " + requestId + " queueName:" + queueName + " waitingTime:" + waitingTime + " batchSize:" + batchSize + ", trans_id=" + request.getAttribute("transId") + ",total count:" + ManagerBean.smsControllerTotalCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting handling request for SMSs")
                    .put(GeneralConstants.StructuredLogKeys.REQUEST_ID, requestId)
                    .put(GeneralConstants.StructuredLogKeys.QUEUE_NAME, queueName)
                    .put(GeneralConstants.StructuredLogKeys.WAIT_TIME, waitingTime)
                    .put(GeneralConstants.StructuredLogKeys.BATCH_SIZE, batchSize)
                    .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                    .put(GeneralConstants.StructuredLogKeys.TOTAL_COUNT, ManagerBean.smsControllerTotalCount.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.incrementAndGet()).build());

            DequeuerResponseModel responseModel = new DequeuerResponseModel();
            MainService mainService = SpringConfiguration.applicationContext.getBean(MainService.class);
            mainService.setJdbcTemplate(managerBean.workingAppQueues.get(queueName).getJdbcTemplate());
            responseModel.setSms(mainService.getSMSs(queueName, batchSize, waitingTime, Thread.currentThread().getId()));
            responseModel.setCode(String.valueOf(ErrorCodes.DEQUEUER_REST_WEB_SERVICE.SUCCESS));
            responseModel.setDescription("Success");
            responseModel.setTransId(request.getAttribute("transId").toString());
            //CommonLogger.businessLogger.info("Sending response with the below SMSs size:" + responseModel.getSms().size() + " code:" + responseModel.getCode() + " description:" + responseModel.getDescription());
//        CommonLogger.businessLogger.info("Ending request for SMSs with the below SMSs size:" + responseModel.getSms().size() + ", trans_id=" + request.getAttribute("transId") + ",responseCode:" + responseModel.getCode() + "responseDescription:" + responseModel.getDescription() + "success count:" + ManagerBean.smsControllerSuccessCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.decrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending request for SMSs")
                    .put(GeneralConstants.StructuredLogKeys.SIZE, responseModel.getSms().size())
                    .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                    .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                    .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ManagerBean.smsControllerSuccessCount.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.decrementAndGet()).build());
            return responseModel;
        } finally {
            ThreadContext.clearMap();
        }
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<DequeuerResponseModel> commonExceptionHandler(HttpServletRequest request, Exception ex) {
        DequeuerResponseModel responseModel = new DequeuerResponseModel();
        responseModel.setCode(((CommonException) ex).getErrorCode());
        responseModel.setDescription(ex.getMessage());
        responseModel.setTransId(request.getAttribute("transId").toString());
//        CommonLogger.businessLogger.info("Ending request for SMSs with trans_id=" + request.getAttribute("transId") + ",responseCode:" + responseModel.getCode() + "responseDescription:" + responseModel.getDescription() + "failure count:" + ManagerBean.smsControllerFailureCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.decrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending request for SMSs")
                .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.smsControllerFailureCount.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.decrementAndGet()).build());
        return new ResponseEntity<DequeuerResponseModel>(responseModel, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DequeuerResponseModel> exceptionHandler(HttpServletRequest request, Exception ex, Model model) {
        DequeuerResponseModel responseModel = new DequeuerResponseModel();
        responseModel.setCode(String.valueOf(ErrorCodes.DEQUEUER_REST_WEB_SERVICE.GENERAL_ERROR));
        responseModel.setDescription(ex.getMessage());
        responseModel.setTransId(request.getAttribute("transId").toString());
//        CommonLogger.errorLogger.error("ERROR: ", ex);
//        CommonLogger.businessLogger.info("ERROR: Ending request for SMSs with trans_id=" + request.getAttribute("transId") + ",responseCode:" + responseModel.getCode() + "responseDescription:" + responseModel.getDescription() + "failure count:" + ManagerBean.smsControllerFailureCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.decrementAndGet());
        CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending request for SMSs")
                .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.smsControllerFailureCount.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.decrementAndGet()).build(), ex);
        return new ResponseEntity<DequeuerResponseModel>(responseModel, HttpStatus.OK);
    }
}
