package de.luh.chkater.spammerdetection.utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Utility class for handling parallel processing
 *
 * @author kater
 */
public class ThreadUtility {

	public static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();

	/**
	 * Starts the runnable as new Threads up to numberOfThreads times. There
	 * will not be more Threads than number of cores. Waits for all running
	 * threads to finish.
	 * 
	 * @param numberOfThreads
	 *            number of running threads
	 * @param toRun
	 *            runnable to execute
	 */
	public static void runTaskOnAllThreads(int numberOfThreads, Runnable toRun) {
		numberOfThreads = Math.min(numberOfThreads, NUMBER_OF_THREADS);
		List<Thread> runningThreads = new ArrayList<>(numberOfThreads);
		for (int i = 0; i < numberOfThreads; i++) {
			Thread t = new Thread(toRun);
			runningThreads.add(t);
			t.start();
		}
		waitForThreads(runningThreads);
	}

	/**
	 * Starts the runnable as new Threads on each processore core. Waits for all running
	 * threads to finish.
	 * 
	 * @param toRun
	 *            runnable to execute
	 */
	public static void runTaskOnAllThreads(Runnable toRun) {
		runTaskOnAllThreads(NUMBER_OF_THREADS, toRun);
	}

	private static void waitForThreads(List<Thread> runningThreads) {
		for (Thread thread : runningThreads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}
}
