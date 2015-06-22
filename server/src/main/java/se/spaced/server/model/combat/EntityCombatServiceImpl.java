package se.spaced.server.model.combat;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

import java.util.Collection;
import java.util.Set;

@Singleton
public class EntityCombatServiceImpl implements EntityCombatService {
	private final Logger logger = LoggerFactory.getLogger(EntityCombatServiceImpl.class);

	public static final long COMBAT_TIMEOUT = 5 * 1000;

	private final ActionScheduler scheduler;
	private final CurrentActionService currentActionService;

	private final CombatRepository combatRepository;
	private final DeathService deathService;

	private final SmrtBroadcaster<S2CProtocol> broadcaster;

	@Inject
	public EntityCombatServiceImpl(
			ActionScheduler scheduler,
			CurrentActionService currentActionService,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			CombatRepository combatRepository, DeathService deathService) {
		this.scheduler = scheduler;
		this.currentActionService = currentActionService;
		this.broadcaster = broadcaster;
		this.combatRepository = combatRepository;
		this.deathService = deathService;
	}


	@Override
	public boolean isInCombat(ServerEntity entity) {
		Combat combat = combatRepository.getCombat(entity);
		if (combat == null) {
			return false;
		}
		return combat.isActive();
	}


	@Override
	public int numberOfCombat() {
		Set<Combat> allCombat = Sets.newHashSet(combatRepository.getAllCombat());
		for (Combat combat : allCombat) {
			if (!combat.isActive()) {
				cleanupCombat(combat);
			}
		}
		return combatRepository.numberOfCombat();
	}

	@Override
	public void removeFromCombat(ServerEntity entity) {
		combatRepository.remove(entity);
		// TODO is this really needed?
		if (entity.isAlive()) {
			entity.getBaseStats().getOutOfCombatHealthRegen().enable();
		}
	}


	@Override
	public Combat enterCombat(ServerEntity performer, ServerEntity target, long now, boolean entitiesAreHostile) {
		boolean performerIsInCombat = isInCombat(performer);
		boolean targetIsInCombat = isInCombat(target);

		Combat combat = getCombat(performer, target, now);

		if (!performerIsInCombat) {
			enterCombat(performer, combat);
		}
		if (!targetIsInCombat) {
			enterCombat(target, combat);
		}
		combat.updateLastAction(now, performer);
		combat.updateLastAction(now, target);
		if (entitiesAreHostile) {
			combat.addHostility(performer, target);
		}
		return combat;
	}

	private void enterCombat(ServerEntity performer, Combat combat) {
		combat.addParticipant(performer);
		combatRepository.add(performer, combat);
	}

	protected Combat getCombat(ServerEntity performer, ServerEntity target, long now) {
		Combat combatA = cleanupAndGetCombat(performer);
		Combat combatB = cleanupAndGetCombat(target);
		if (combatA == null && combatB == null) {
			Combat combat = new Combat(currentActionService, scheduler, COMBAT_TIMEOUT, broadcaster, this);
			combat.updateLastAction(now, performer);
			combat.updateLastAction(now, target);
			return combat;
		} else if (combatA == combatB) {
			return combatA;
		} else if (combatA == null) {
			return combatB;
		} else if (combatB == null) {
			return combatA;
		} else {
			Combat mergedCombat = Combat.merge(combatA, combatB, scheduler, currentActionService, broadcaster);
			mergedCombat.updateLastAction(now, performer);
			mergedCombat.updateLastAction(now, target);
			Collection<ServerEntity> participants = mergedCombat.getParticipants();
			for (ServerEntity participant : participants) {
				combatRepository.add(participant, mergedCombat);
			}
			return mergedCombat;
		}
	}


	protected Combat cleanupAndGetCombat(ServerEntity performer) {
		Combat combat = combatRepository.getCombat(performer);
		if (combat == null) {
			return null;
		}
		if (combat.isActive()) {
			return combat;
		}
		cleanupCombat(combat);
		return null;
	}

	private void cleanupCombat(Combat combat) {
		for (ServerEntity entity : combat.getParticipants()) {
			removeFromCombat(entity);
		}
	}

	@Override
	public void respawnWithHealth(ServerEntity entity, int health) {
		if (!entity.isAlive()) {
			entity.revive(health);

			deathService.respawn(entity);
		} else {
			logger.warn("The entity was alive when trying to resurrect");
		}
	}


	@Override
	public void entityAdded(ServerEntity entity) {
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		combatRepository.remove(entity);
	}
}
