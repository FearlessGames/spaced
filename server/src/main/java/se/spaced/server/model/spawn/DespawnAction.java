package se.spaced.server.model.spawn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.Mob;
import se.spaced.server.model.action.Action;
import se.spaced.server.model.action.ActionScheduler;

public class DespawnAction extends Action {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MobSpawn mobSpawn;
	private final Mob decayingEntity;
	private final long firstDecayTry;
	private long totalAddedDecayTime;

	private final ActionScheduler scheduler;

	public DespawnAction(MobSpawn mobSpawn, Mob decayingEntity, long decayTime, long now, ActionScheduler scheduler) {
		super(now + decayTime);
		this.mobSpawn = mobSpawn;
		this.scheduler = scheduler;
		firstDecayTry = now + decayTime;
		this.decayingEntity = decayingEntity;
	}

	@Override
	public void perform() {
		if (shouldDespawn()) {
			mobSpawn.removeEntity(decayingEntity);
			mobSpawn.tick(getExecutionTime());
			return;
		}
		scheduler.reschedule(this, calculateAdditionalDecayTime());
	}

	// TODO Check whether we should let the corpse decay more
	private boolean shouldDespawn() {
		return true;
	}

	// TODO Decide this formula based on loot status
	private long calculateAdditionalDecayTime() {
		totalAddedDecayTime += 2 * 60 * 1000;
		return firstDecayTry + totalAddedDecayTime;
	}
}
