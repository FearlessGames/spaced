package se.spaced.server.model.action;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

@Singleton
public class ActionScheduler {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final Set<Action> actionsInQueue = Sets.newHashSet();
	private final PriorityQueue<Action> actions = new PriorityQueue<Action>();
	private final Queue<Action> newActions = new ConcurrentLinkedQueue<Action>();
	private final AtomicLong delay = new AtomicLong();

	@Inject
	public ActionScheduler() {
	}

	/**
	 * Thread safe, since it adds to a concurrent queue
	 */
	public void add(Action action) {
		newActions.add(action);
	}

	/**
	 * Not thread safe, should only be called from tick.
	 */
	private void doAdd(Action action) {
		actions.add(action);
		actionsInQueue.add(action);
	}

	/**
	 * Not thread safe, should only be called from the main thread
	 */
	public void reschedule(Action action, long newExecutionTime) {
		if (actionsInQueue.contains(action)) {
			if (newExecutionTime < action.executionTime) {
				throw new IllegalArgumentException("Can't reschedule before the current execution time");
			}
			action.newExecutionTime = newExecutionTime;
			action.rescheduled = true;
		} else {
			action.executionTime = newExecutionTime;
			action.rescheduled = false;
			doAdd(action);
		}
	}


	public long getDelay() {
		return delay.get();
	}

	/**
	 * Not thread safe, should only be called from the main thread
	 */
	public void tick(long now) {
		while (true) {
			mergeNewActions();

			if (actions.isEmpty()) {
				break;
			}

			Action action = actions.peek();
			if (!action.timeToExecute(now)) {
				break;
			}
			delay.set(now - action.getExecutionTime());
			actions.remove();
			actionsInQueue.remove(action);

			if (!action.isCancelled()) {
				if (!action.isRescheduled()) {
					action.perform();
				} else {
					action.executionTime = action.newExecutionTime;
					action.rescheduled = false;
					doAdd(action);
				}
			}
		}
	}

	private void mergeNewActions() {
		while (!newActions.isEmpty()) {
			Action action = newActions.remove();
			if (!actionsInQueue.contains(action)) {
				doAdd(action);
			}
		}
	}

	public boolean isEmpty() {
		return actions.isEmpty() && newActions.isEmpty();
	}

	public int size() {
		return actions.size() + newActions.size();
	}
}
