/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.CS.BridgingDequeuerService.Controllers;

import com.asset.CS.BridgingDequeuerService.beans.ManagerBean;
import com.asset.contactstrategy.common.defines.Defines;
import com.asset.contactstrategy.common.defines.ErrorCodes;
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.models.RESTResponseModel;
import com.asset.contactstrategy.common.utils.Utility;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author mostafa.kashif
 */
@RestController
@RequestMapping(value = "/DequeuerService/")
public class ReadinessController {

    // private String transId;
    @RequestMapping(value = "/InterfaceReadiness", method = RequestMethod.GET)
    public RESTResponseModel checkInterface(HttpServletRequest request) throws Exception {
        // transId = Utility.generateTransId("DEQBRDGSMS");
        request.setAttribute("transId", Utility.generateTransId("DEQBRDGSMS"));
//        CommonLogger.businessLogger.info("Starting handling request for InterfaceReadiness with trans_id=" + request.getAttribute("transId") + ",total count:" + ManagerBean.readinessControllerTotalCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Starting handling request for InterfaceReadiness")
                .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                .put(GeneralConstants.StructuredLogKeys.TOTAL_COUNT, ManagerBean.readinessControllerTotalCount.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.incrementAndGet()).build());
        RESTResponseModel responseModel = new RESTResponseModel();
        ConcurrentHashMap<String, ArrayBlockingQueue> queues = new ConcurrentHashMap<>();
        ArrayList<Float> queuePercentages = new ArrayList<>();
        if (!Utility.readyCheck(ManagerBean.concurrentCount, Long.valueOf(ManagerBean.systemPropertiesMap.get(Defines.DequeuerRestWebService.MAX_CONCURRENT_REQUESTS_DB_PROEPRTY_NAME).getItemValue()), queues, queuePercentages, CommonLogger.businessLogger)) {
            throw new Exception("Exception in CheckInterface");
        }
        responseModel.setTransId(request.getAttribute("transId").toString());
        responseModel.setCode(String.valueOf(ErrorCodes.DEQUEUER_REST_WEB_SERVICE.SUCCESS));
        responseModel.setDescription("Success");
//        CommonLogger.businessLogger.info("Ending request for InterfaceReadiness with trans_id=" + request.getAttribute("transId") + ",responseCode:" + responseModel.getCode() + "responseDescription:" + responseModel.getDescription() + "success count:" + ManagerBean.readinessControllerSuccessCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.decrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending request for InterfaceReadiness")
                .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ManagerBean.readinessControllerSuccessCount.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.decrementAndGet()).build());
        return responseModel;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RESTResponseModel> exceptionHandler(HttpServletRequest request, Exception ex) {
        RESTResponseModel responseModel = new RESTResponseModel();
        responseModel.setCode(String.valueOf(ErrorCodes.DEQUEUER_REST_WEB_SERVICE.GENERAL_ERROR));
        responseModel.setDescription(ex.getMessage());
        responseModel.setTransId(request.getAttribute("transId").toString());
//        CommonLogger.businessLogger.info("Ending request for InterfaceReadiness with trans_id=" + request.getAttribute("transId") + ",responseCode:" + responseModel.getCode() + "responseDescription:" + responseModel.getDescription() + "failure count:" + ManagerBean.readinessControllerFailureCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.decrementAndGet());
        CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending request for InterfaceReadiness")
                .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.readinessControllerFailureCount.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.decrementAndGet()).build());
        return new ResponseEntity<RESTResponseModel>(responseModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommonException.class)
    public ResponseEntity<RESTResponseModel> commonExceptionHandler(HttpServletRequest request, Exception ex) {
        RESTResponseModel responseModel = new RESTResponseModel();
        responseModel.setCode(((CommonException) ex).getErrorCode());
        responseModel.setDescription(ex.getMessage());
        responseModel.setTransId(request.getAttribute("transId").toString());
//        CommonLogger.businessLogger.info("Ending request for InterfaceLiveness with trans_id=" + request.getAttribute("transId") + ",responseCode:" + responseModel.getCode() + "responseDescription:" + responseModel.getDescription() + "failure count:" + ManagerBean.livenessControllerFailureCount.incrementAndGet() + ",application concurrent count:" + ManagerBean.concurrentCount.decrementAndGet());
        CommonLogger.businessLogger.error(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ending request for InterfaceLiveness")
                .put(GeneralConstants.StructuredLogKeys.TRANS_ID, request.getAttribute("transId").toString())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_CODE, responseModel.getCode())
                .put(GeneralConstants.StructuredLogKeys.RESPONSE_DESCRIPTION, responseModel.getDescription())
                .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ManagerBean.readinessControllerFailureCount.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ManagerBean.concurrentCount.decrementAndGet()).build());
        return new ResponseEntity<RESTResponseModel>(responseModel, HttpStatus.BAD_REQUEST);
    }

}
