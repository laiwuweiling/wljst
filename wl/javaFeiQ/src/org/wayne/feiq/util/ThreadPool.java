package org.wayne.feiq.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ThreadPool {

	private static final Log log = LogFactory.getLog(ThreadPool.class);

	private final ExecutorService pool;

	public ThreadPool() {
		pool = Executors.newCachedThreadPool();
	}

	public void execute(Runnable command) {
		pool.execute(command);
	}

	public void dispose() {
		log.debug("dispose thread pool");
		// Disable new tasks from being submitted
		pool.shutdown();
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(1, TimeUnit.MILLISECONDS)) {
				pool.shutdownNow();
				// Cancel currently executing tasks
				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(1, TimeUnit.MILLISECONDS))
					log.error("Pool did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}

}
