package com.asset.contactstrategy.common.executor;

import com.asset.contactstrategy.common.logger.CommonLogger;
import com.asset.contactstrategy.common.utils.BackPressurePolicy;
import com.asset.contactstrategy.common.utils.CustomThreadFactory;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author esmail.anbar
 * @param <T> is the type for the single entity in the batch list
 * @param <S>
 */
public abstract class RollingBatchExecutor<T, S> {

    private ArrayList<S> batchList;
    private int maxBatchSize;
    protected final ThreadPoolExecutor executor;
    private final BatchCommand baseCommand = new BatchCommand();
    private final boolean autoFlush;
    private final SafeInteger safeInteger = new SafeInteger();

    public RollingBatchExecutor(int maxBatchSize, ConsumePolicy consumePolicy,
            int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            int maxQueueSize, CustomThreadFactory customThreadFactory) {
        this.executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new ArrayBlockingQueue<>(maxQueueSize), customThreadFactory, new BackPressurePolicy());
        this.maxBatchSize = maxBatchSize;
        this.batchList = new ArrayList<>(maxBatchSize);
        this.autoFlush = consumePolicy.equals(ConsumePolicy.Eager);
    }

    public RollingBatchExecutor(int maxBatchSize, ConsumePolicy consumePolicy,
            ThreadPoolExecutor executor) {
        this.executor = executor;
        this.maxBatchSize = maxBatchSize;
        this.batchList = new ArrayList<>(maxBatchSize);
        this.autoFlush = consumePolicy.equals(ConsumePolicy.Eager);
    }

    protected abstract void consumeBatchList(ArrayList<S> batchList);

    protected abstract S processBeforeAdding(T payload);

    public enum ConsumePolicy {
        Eager,
        Lazy;
    }

    public final void execute(T payload) throws InterruptedException {
        try {
            safeInteger.increment();
            executor.execute(((BatchCommand) baseCommand.clone()).setPayload(payload));
        } catch (CloneNotSupportedException ex) {
            executor.execute(new BatchCommand().setPayload(payload));
        }
    }

    private synchronized ArrayList<S> addAndRollList(S payload) {
        batchList.add(payload);
        safeInteger.decrement();
        if (batchList.size() == maxBatchSize || (autoFlush && safeInteger.get() == 0)) {
            final ArrayList<S> tempbatchList = batchList;
            batchList = new ArrayList<>(maxBatchSize);
            return tempbatchList;
        }
        return null;
    }

    public void awaitBatchProcessing() {
        //Flushing before because the executed counts maybe lower than the batch size
        if (!autoFlush) {
            flush();
        }
        while (true) {
            if (safeInteger.get() == 0) {
                break;
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    CommonLogger.errorLogger.error(ex);
                    throw new RuntimeException("Exception caught when awaitingBatchProcessing()", ex);
                }
            }
        }
        //Flushing after because the executed counts maybe lower than the last batch size
        if (!autoFlush) {
            flush();
        }
    }

    public synchronized Integer flush() {
        if (!batchList.isEmpty()) {
            final ArrayList<S> tempbatchList = batchList;
            batchList = new ArrayList<>(maxBatchSize);
            consumeBatchList(tempbatchList);
            return tempbatchList.size();
        }
        return 0;
    }

    public final void shutdown() {
        executor.shutdown();
    }

    public final void awaitTermination() throws InterruptedException {
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        flush();
        postShutdown();
    }

    protected abstract void postShutdown();

    public void setMaxBatchSize(int maxBatchSize) {
        this.maxBatchSize = maxBatchSize;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    private class BatchCommand implements Runnable, Cloneable {

        private T payload;

        public BatchCommand setPayload(T payload) {
            this.payload = payload;
            return this;
        }

        @Override
        public void run() {
            S output = processBeforeAdding(payload);
            if (output != null) {
                final ArrayList<S> batchList = addAndRollList(output);
                if (batchList != null) {
                    consumeBatchList(batchList);
                }
            } else {
                //Fix to decrement if not used
                safeInteger.decrement();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    protected class SafeInteger {

        private int value;
        private final ReentrantLock lock;

        public SafeInteger() {
            this.lock = new ReentrantLock();
        }

        public void increment() throws InterruptedException {
            try {
                lock.lockInterruptibly();
                value++;
            } catch (InterruptedException ex) {
                CommonLogger.errorLogger.error(ex);
                throw ex;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        public void decrement() {
            try {
                lock.lockInterruptibly();
                value--;
            } catch (InterruptedException ex) {
                CommonLogger.errorLogger.error(ex);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        public void set(int value) {
            try {
                lock.lockInterruptibly();
                this.value = value;
            } catch (InterruptedException ex) {
                CommonLogger.errorLogger.error(ex);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }

        public int get() {
            try {
                lock.lockInterruptibly();
                final int x = value;
                return x;
            } catch (InterruptedException ex) {
                CommonLogger.errorLogger.error(ex);
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            return -1;
        }
    }

    public ArrayList<S> getBatchList() {
        return batchList;
    }
}
