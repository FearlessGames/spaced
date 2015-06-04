package se.ardorgui.view.util;

import com.ardor3d.util.GameTaskQueueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class VisualiserCallback {
	private static final Logger logger = LoggerFactory.getLogger(VisualiserCallback.class);
	private static Thread openGlThread = Thread.currentThread();		// TODO: does this work!?

	/**
	 * If running in the OpenGL thread, call Callable directly, otherwise
	 * queue in GameTask queue for OpenGL thread execution.
	 *
	 * @param exe Callable to execute
	 * @param taskDescription Description of task
	 * @param queue Which queue to execute in
	 */
	public static Object executeTask(Callable<?> exe, boolean waitForTask, String taskDescription, TaskQueue queue) {
		if (Thread.currentThread().equals(openGlThread)) {
			try {
				return exe.call();
			} catch (Exception e) {
				logger.error( "Something was wrong executing " + taskDescription, e );
				throw new RuntimeException("Something was wrong executing " + taskDescription, e);
			}
		}

		Future<?> futureEntities = null;
		try {
			switch (queue) {
				case RENDER:
					futureEntities = GameTaskQueueManager.getManager(0).render(exe);
					break;
				case UPDATE:
					futureEntities = GameTaskQueueManager.getManager(0).update(exe);
					break;
			}
			if (waitForTask) {
				return futureEntities.get();
			}
		} catch (InterruptedException e) {
			logger.warn("Cannot wait any longer for " + taskDescription +
					 " to construct!... Polling instead.", e);
			pollTaskCompletion(futureEntities, taskDescription);
		} catch (ExecutionException e) {
			logger.error( "Something was wrong executing " + taskDescription, e );
			throw new RuntimeException("Something was wrong constructing " + taskDescription, e);
		}
		return null;
	}

	/**
	 * Repeatedly check within a 5 seconds time if a Future task is done.
	 * If the task isn't done within this time a VisualInitializationError is thrown.
	 *
	 * @param task
	 * @param taskDescription
	 */
	private static void pollTaskCompletion(Future<?> task, String taskDescription) {
		for (int i = 0; i < 100; i++) {
			if (task.isDone()) {
				return;
			}
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}
		}
		throw new RuntimeException(taskDescription + " was not completed within time!");
	}
}