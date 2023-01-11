/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.common.models;

/**
 *
 * @author esmail.anbar
 */
public class ThreadInfo {

    private String threadName;
    private long threadId;
    private long blockedTime;
    private long blockedCount;
    private long waitedTime;
    private long waitedCount;
    private String lockName;
    private long lockOwnerId;
    private String lockOwnerName;
    private boolean inNative;
    private boolean suspended;
    private String threadState;
    private StackTraceElement[] stackTrace;


    public ThreadInfo(java.lang.management.ThreadInfo threadInfo) {
        this.threadName = threadInfo.getThreadName();
        this.threadId = threadInfo.getThreadId();
        this.blockedTime = threadInfo.getBlockedTime();
        this.blockedCount = threadInfo.getBlockedCount();
        this.waitedTime = threadInfo.getWaitedTime();
        this.waitedCount = threadInfo.getWaitedCount();
        this.lockName = threadInfo.getLockName();
        this.lockOwnerId = threadInfo.getLockOwnerId();
        this.lockOwnerName = threadInfo.getLockOwnerName();
        this.inNative = threadInfo.isInNative();
        this.suspended = threadInfo.isSuspended();
        this.threadState = threadInfo.getThreadState().name();
        this.stackTrace = threadInfo.getStackTrace();
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getBlockedTime() {
        return blockedTime;
    }

    public void setBlockedTime(long blockedTime) {
        this.blockedTime = blockedTime;
    }

    public long getBlockedCount() {
        return blockedCount;
    }

    public void setBlockedCount(long blockedCount) {
        this.blockedCount = blockedCount;
    }

    public long getWaitedTime() {
        return waitedTime;
    }

    public void setWaitedTime(long waitedTime) {
        this.waitedTime = waitedTime;
    }

    public long getWaitedCount() {
        return waitedCount;
    }

    public void setWaitedCount(long waitedCount) {
        this.waitedCount = waitedCount;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public long getLockOwnerId() {
        return lockOwnerId;
    }

    public void setLockOwnerId(long lockOwnerId) {
        this.lockOwnerId = lockOwnerId;
    }

    public String getLockOwnerName() {
        return lockOwnerName;
    }

    public void setLockOwnerName(String lockOwnerName) {
        this.lockOwnerName = lockOwnerName;
    }

    public boolean isInNative() {
        return inNative;
    }

    public void setInNative(boolean inNative) {
        this.inNative = inNative;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public String getThreadState() {
        return threadState;
    }

    public void setThreadState(String threadState) {
        this.threadState = threadState;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

}
