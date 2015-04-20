package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.AbstractRepeatedAction;
import se.spaced.server.model.action.ActionScheduler;

public class EndOfCombatTesterAction extends AbstractRepeatedAction {

	private final Combat combat;
	private final long combatTimeout;
	private final EntityCombatService entityCombatService;

	protected EndOfCombatTesterAction(
			Combat combat,
			long combatTimeout,
			long now,
			ActionScheduler scheduler,
			EntityCombatService entityCombatService) {
		super(scheduler, now + combatTimeout);
		this.combat = combat;
		this.combatTimeout = combatTimeout;
		this.entityCombatService = entityCombatService;
	}

	@Override
	protected long getTimeToNextUpdate() {
		long lastPerformedActionTime = combat.getLastPerformedActionTime(getExecutionTime());
		return getExecutionTime() - lastPerformedActionTime + combatTimeout;
	}

	@Override
	protected void performRepeat() {
		if (combat.isActive()) {
			if (combat.shouldStop(getExecutionTime())) {
				combat.stop();
				cancel();
			}
			for (ServerEntity entity : combat.getParticipants()) {
				if (getExecutionTime() - entity.getLastCombatTimestamp() >= combatTimeout) {
					entityCombatService.removeFromCombat(entity);
				}
			}
		} else {
			cancel();
		}

	}

}
