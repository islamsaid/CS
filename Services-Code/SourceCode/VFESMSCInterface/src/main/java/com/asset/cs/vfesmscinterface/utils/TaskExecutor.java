/**
 * created on: Jan 13, 2018 11:13:02 AM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.utils;

import java.lang.reflect.Constructor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.asset.contactstrategy.common.logger.CommonLogger;

/**
 *
 * <code>
 * 		int numOfWorkers = 3; <br />
 * int taskQueueCapacity = 100; <br />
 * TaskExecutor<{@link Runnable}, {@link Integer}> taskExecutor = new
 * TaskExecutor(Runnable.class, numOfWorkers, taskQueueCapacity);
 * </code>
 *
 * <br />
 * <br />
 * runnable should have a constructor taking {@link BlockingQueue} as argument
 * <br />
 * T any class implements {@link Runnable} <br />
 * V BlockingQueue type, BlockingQueue V <br />
 *
 * @author mohamed.morsy
 *
 */
public class TaskExecutor<T extends Runnable, V> {

	private final Class<T> workerClass;

	private boolean enablePutOp;

	private int numOfWorkers;

	private int queueTaskCapacity;

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	private BlockingQueue<V> blockingQueue;

	private ExecutorService executorService;

	public TaskExecutor(Class<T> workerClass, int numOfWorkers, int queueTaskCapacity) throws Exception {

		this.workerClass = workerClass;

		this.numOfWorkers = numOfWorkers;

		this.queueTaskCapacity = queueTaskCapacity;

		enablePutOp = true;

		blockingQueue = new ArrayBlockingQueue<V>(queueTaskCapacity);

		// initialization of Executor service itself
		executorService = Executors.newCachedThreadPool();

		try {
			Constructor<T> constructor = workerClass.getConstructor(BlockingQueue.class);
			for (int i = 0; i < numOfWorkers; i++) {
				Runnable runnable = constructor.newInstance(blockingQueue);
				executorService.execute(runnable);
			}
		} catch (Exception e) {
			CommonLogger.businessLogger.error("error while initialization task executor: " + e.getMessage());
			CommonLogger.errorLogger.error("error while initialization task executor ", e);
			throw e;
		}
	}

	public void put(V v) throws InterruptedException {
		blockingQueue.put(v);
		if (!enablePutOp) {
			CommonLogger.businessLogger.debug("shutdown called, submission may be discarded param: " + v);
		}
	}

	public void safePut(V v) {
		try {
			blockingQueue.put(v);
		} catch (InterruptedException e) {
			CommonLogger.businessLogger.debug("model is: " + v + " " + e.getMessage());
			CommonLogger.errorLogger.error(e);
		}

		if (!enablePutOp) {
			CommonLogger.businessLogger.debug("shutdown called, submission may be discarded param: " + v);
		}
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executorService.awaitTermination(timeout, unit);
	}

	public void shutdown() {
		enablePutOp = false;
		executorService.shutdown();
	}

	public void shutdownNow() {
		enablePutOp = false;
		executorService.shutdownNow();
	}

	public int getQueueSize() {
		return blockingQueue.size();
	}

	public int getNumOfWorkers() {
		return numOfWorkers;
	}

	public int getQueueTaskCapacity() {
		return queueTaskCapacity;
	}

	@Override
	public String toString() {
		return "{numOfWorkers=" + numOfWorkers + ", queueTaskCapacity=" + queueTaskCapacity + ", numOfObjectsInQueue="
				+ blockingQueue.size() + "}";
	}

}
