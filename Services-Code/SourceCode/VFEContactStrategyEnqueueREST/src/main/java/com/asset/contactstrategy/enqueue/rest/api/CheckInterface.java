/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.enqueue.rest.api;

import com.asset.contactstrategy.common.defines.GeneralConstants;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.logger.StructuredLogFactory;
import com.asset.contactstrategy.common.utils.Utility;
import com.asset.contactstrategy.enqueue.rest.common.ConfigurationManager;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Esmail.Anbar
 */
@Path("/CheckInterface")
public class CheckInterface {

    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getRequest() {
//        CommonLogger.businessLogger.info("Started CheckInterface Servlet");
//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.incrementAndGet() + " | Check Interface Accumlator Count: " + ConfigurationManager.checkInterfaceAccumulator.incrementAndGet());
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Started CheckInterface Servlet")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.incrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.ACCUMULATED_COUNT, ConfigurationManager.checkInterfaceAccumulator.incrementAndGet()).build());
        ArrayList<Boolean> flags = new ArrayList<>();
        flags.add(ConfigurationManager.SHUTDOWN_FLAG);
        flags.add(ConfigurationManager.MONITOR_THREAD_SHUTDOWN_FLAG);

        ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<>();
        executors.put("logThreadPool", ConfigurationManager.logThreadPool);

        ArrayList<Integer> poolSizes = new ArrayList<>();
        poolSizes.add(ConfigurationManager.MAX_LOGGING_THREAD_POOL_SIZE);

        ConcurrentHashMap<String, Thread> threads = new ConcurrentHashMap<>();
        threads.put("monitoringThread", ConfigurationManager.monitoringThread);
        threads.put("reloadingThread", ConfigurationManager.reloadingThread);

        if (!Utility.checkInterface(flags, executors, poolSizes, threads, CommonLogger.businessLogger)) {
//            CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet() + " | Check Interface Failed Count: " + ConfigurationManager.checkInterfaceFailedCount.incrementAndGet());
//            CommonLogger.businessLogger.info("Ended CheckInterface Servlet");
            CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended CheckInterface and Failed")
                    .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                    .put(GeneralConstants.StructuredLogKeys.FAILURE_COUNT, ConfigurationManager.checkInterfaceFailedCount.incrementAndGet()).build());
            return Response.serverError().build();
        }

//        CommonLogger.businessLogger.info("Application Concurrent Requests: " + ConfigurationManager.concurrentRequests.decrementAndGet() + " | Check Interface Success Count: " + ConfigurationManager.checkInterfaceSuccessCount.incrementAndGet());
//        CommonLogger.businessLogger.info("Ended CheckInterface Servlet");
        CommonLogger.businessLogger.info(StructuredLogFactory.put(GeneralConstants.StructuredLogKeys.MESSAGE, "Ended CheckInterface Servlet and Successful")
                .put(GeneralConstants.StructuredLogKeys.CONCURRENT_COUNT, ConfigurationManager.concurrentRequests.decrementAndGet())
                .put(GeneralConstants.StructuredLogKeys.SUCCESS_COUNT, ConfigurationManager.checkInterfaceSuccessCount.incrementAndGet()).build());
        return Response.ok().build();
    }
}
