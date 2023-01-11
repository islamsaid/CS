/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.interfaces.threads;

import com.asset.contactstrategy.common.defines.DBStruct;
import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.models.ServiceQuotaCounter;
import com.asset.contactstrategy.interfaces.service.MainService;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author eslam.ahmed
 */
public class ServiceQuotaUpdaterThread implements Runnable {

    private HashMap<Integer, ArrayList<ServiceQuotaCounter>> serviceQuota = new HashMap<>();
    private HashMap<Integer, ServiceQuotaCounter> serviceCounters = new HashMap<>();
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public void run() {

        HashMap<Integer, ServiceQuotaCounter> serviceCountersTemp = null;
        try {
            reentrantLock.lock();
            if (!serviceCounters.isEmpty()) {
                serviceCountersTemp = serviceCounters;
                serviceCounters = new HashMap<>();
            }
        } finally {
            reentrantLock.unlock();
        }

        try {
            if (serviceCountersTemp != null) {
                MainService.updateServiceQuota(serviceCountersTemp);
            }
            this.serviceQuota = MainService.getServiceQuota();

        } catch (Exception ex) {
            CommonLogger.errorLogger.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param serviceId service id
     * @param value update quota value
     * @param maxValue max quota value
     * @param counterColumn service quota column to be updated
     * @return -1 exceeds service quota, 0 service not found and 1 quota updated
     * successfully
     */
    public int updateServiceQuota(Integer serviceId,
            Integer value,
            Long maxValue,
            String counterColumn) {
        ArrayList<ServiceQuotaCounter> quota = serviceQuota.get(serviceId);
        if (quota != null && !quota.isEmpty()) {
            ServiceQuotaCounter counters = serviceCounters.get(serviceId);
            if (counters == null) {
                try {
                    reentrantLock.lock();
                    counters = serviceCounters.get(serviceId);
                    if (counters == null) {
                        counters = new ServiceQuotaCounter();
                        counters.setServiceId(serviceId);
                        serviceCounters.put(serviceId, counters);
                    }
                } finally {
                    reentrantLock.unlock();
                }
            }
            switch (counterColumn) {
                case DBStruct.VFE_CS_SERVICE_HISTORY.DAILY_QUOTA_COUNTER:
                    if (maxValue != null && quota.get(0).getDailyQuotaCounter().get() + value >= maxValue) {
                        return -1;
                    }
                    counters.getDailyQuotaCounter().set(counters.getDailyQuotaCounter().get() + value);
                    break;
                case DBStruct.VFE_CS_SERVICE_HISTORY.MONITOR_COUNTER:
                    if (maxValue != null && quota.get(0).getMonitorCounter().get() + value >= maxValue) {
                        return -1;
                    }
                    counters.getMonitorCounter().set(counters.getMonitorCounter().get() + value);
                    break;
                case DBStruct.VFE_CS_SERVICE_HISTORY.DO_NOT_APPLY_COUNTER:
                    if (maxValue != null && quota.get(0).getDoNotApplyCounter().get() + value >= maxValue) {
                        return -1;
                    }
                    counters.getDoNotApplyCounter().set(counters.getDoNotApplyCounter().get() + value);
                    break;
            }
            return quota.size();
        } else {
            return 0;
        }
    }
}
