package se.spaced.server.model.spawn.schedule;

import se.fearless.common.uuid.UUID;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;

import javax.persistence.Entity;

@Entity
public class SpawnScheduleTemplate extends ExternalPersistableBase {
	private int minCount;
	private int maxCount;
	private long maxWaitTime;

	protected SpawnScheduleTemplate() {
	}

	public SpawnScheduleTemplate(UUID uuid, int minCount, int maxCount, long maxWaitTime) {
		super(uuid);
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.maxWaitTime = maxWaitTime;
	}

	public SpawnSchedule createSchedule(ActionScheduler scheduler) {
		return new SpawnSchedule(minCount, maxCount, maxWaitTime, scheduler);
	}

	public int getMinCount() {
		return minCount;
	}

	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}
}
