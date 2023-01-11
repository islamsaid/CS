/**
 * created on: Dec 14, 2017 12:34:02 PM
 * created by: mohamed.morsy
 */
package com.asset.cs.vfesmscinterface.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mohamed.morsy
 *
 */
public class Executor {

	private int numOfThreads;

	private int queueCapacity;

	private ExecutorService executorService;

	private BlockingQueue<Runnable> blockingQueue;

	public Executor(int queueCapacity, int numOfThreads) {

		this.queueCapacity = queueCapacity;

		this.numOfThreads = numOfThreads;

		blockingQueue = new ArrayBlockingQueue<>(queueCapacity);

		executorService = new ThreadPoolExecutor(numOfThreads, numOfThreads, 0L, TimeUnit.MILLISECONDS, blockingQueue);
	}

	public void execute(Runnable task) {
		executorService.submit(task);
	}

	public Future<?> submit(Runnable task) {
		return executorService.submit(task);
	}

	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return executorService.awaitTermination(timeout, unit);
	}

	public boolean isTerminated() {
		return blockingQueue.isEmpty() && executorService.isTerminated();
	}

	public int getQueueSize() {
		return blockingQueue.size();
	}

	public void shutdown() {
		executorService.shutdown();
	}

	public void shutdownNow() {
		executorService.shutdownNow();
	}

	public int getNumOfThreads() {
		return numOfThreads;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	@Override
	public String toString() {
		return "{numOfThreads=" + numOfThreads + ", queueCapacity=" + queueCapacity + ", blockingQueueSize="
				+ blockingQueue.size() + "}";
	}

}
