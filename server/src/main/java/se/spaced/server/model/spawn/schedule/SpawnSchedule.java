package se.spaced.server.model.spawn.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.action.Action;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.spawn.MobSpawn;


public class SpawnSchedule {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final int minCount;
	private final int maxCount;
	private final long maxWaitTime;
	private long lastSpawnTime;
	private final ActionScheduler scheduler;
	private SpawnAction action;
	private boolean paused;

	public SpawnSchedule(
			int minCount,
			int maxCount,
			long maxWaitTime,
			ActionScheduler scheduler) {
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.maxWaitTime = maxWaitTime;
		this.scheduler = scheduler;
	}

	public void perform(MobSpawn mobSpawn, long now) {
		while (!isPaused()) {
			int count = mobSpawn.getMobCount();

			long waitTime = getWaitTime(count);
			if (waitTime < 0) {
				return;
			}
			long nextSpawnTime = lastSpawnTime + waitTime;
			if (nextSpawnTime > now) {
				reschedule(nextSpawnTime, mobSpawn);
				return;
			}
			lastSpawnTime = now;
			mobSpawn.doSpawn();
		}
	}

	private long getWaitTime(int count) {
		if (count < minCount) {
			return 0;
		}
		if (count >= maxCount) {
			return -1;
		}
		return maxWaitTime * (count - minCount + 1) / (maxCount - minCount + 1);
	}

	public void stop() {
		paused = true;
	}

	public void tick(MobSpawn mobSpawn, long now) {
		paused = false;
		perform(mobSpawn, now);
	}

	public boolean isPaused() {
		return paused;
	}

	protected void reschedule(long nextTime, MobSpawn mobSpawn) {
		if (action == null) {
			action = new SpawnAction(nextTime, mobSpawn);
			scheduler.add(action);
		} else {
			if (nextTime < action.getExecutionTime()) {
				action.cancel();
				action = null;
				reschedule(nextTime, mobSpawn);
			} else if (nextTime > action.getExecutionTime()) {
				scheduler.reschedule(action, nextTime);
			}
		}
	}

	public long getExecutionTime() {
		if (action == null) {
			return 0L;
		}
		return action.getExecutionTime();
	}

	public void resetLastSpawnTime(long now) {
		lastSpawnTime = now;
	}

	private class SpawnAction extends Action {
		private final MobSpawn mobSpawn;

		protected SpawnAction(long executionTime, MobSpawn mobSpawn) {
			super(executionTime);
			this.mobSpawn = mobSpawn;
		}

		@Override
		public void perform() {
			SpawnSchedule.this.perform(mobSpawn, getExecutionTime());
		}
	}
}
