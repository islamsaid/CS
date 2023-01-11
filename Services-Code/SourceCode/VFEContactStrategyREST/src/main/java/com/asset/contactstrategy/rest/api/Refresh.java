package com.asset.contactstrategy.rest.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.exception.CommonException;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.rest.common.ConfigurationManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Esmail.Anbar
 */
@Path("/Refresh")
public class Refresh {

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String processRequest() {
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() + " | Refresh Accumlator Count: " + ConfigurationManager.refreshAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Refreshing Stats ")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.refreshAccumulator.incrementAndGet()).build());
        try {
            ConfigurationManager.reloadData();
//            CommonLogger.businessLogger.info("Refresh Success Count: " + ConfigurationManager.refreshSuccessCount.incrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Refresh Susccess")
                    .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.refreshSuccessCount.incrementAndGet()).build());
        } catch (CommonException ex) {
            CommonLogger.businessLogger.error(ex.getErrorMsg());
            CommonLogger.errorLogger.error(ex.getErrorMsg(), ex);
//            CommonLogger.businessLogger.info("Refresh Failed Count: " + ConfigurationManager.refreshFailedCount.incrementAndGet() + " | Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Refresh failed")
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.refreshFailedCount.incrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
            return "Refresh Failed";
        }
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Refresh Successful")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet()).build());
        return "Refresh Successful";
    }
}
