package com.asset.contactstrategy.common.utils;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author esmail.anbar
 */
public class BackPressurePolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException ex) {
            throw new RuntimeException("Failed to enqueue task[" + r.toString() + "]");
        }
    }

}
