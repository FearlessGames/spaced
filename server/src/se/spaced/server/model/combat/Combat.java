package se.spaced.server.model.combat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

import java.util.Collection;
import java.util.concurrent.ConcurrentSkipListSet;

public class Combat {
	private final Logger logger = LoggerFactory.getLogger(Combat.class);

	private final CurrentActionService currentActionService;
	private final ActionScheduler scheduler;

	private final Collection<ServerEntity> participants;
	private long lastPerformedActionTime;
	private boolean active;
	private final long combatTimeout;

	private boolean firstUpdate;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;
	private final EntityCombatService entityCombatService;
	private final Multimap<ServerEntity, ServerEntity> hostilities = Multimaps.<ServerEntity, ServerEntity>synchronizedMultimap(HashMultimap.<ServerEntity, ServerEntity>create());

	public Combat(
			CurrentActionService currentActionService, ActionScheduler scheduler,
			long combatTimeout, SmrtBroadcaster<S2CProtocol> broadcaster, EntityCombatService entityCombatService) {
		this.currentActionService = currentActionService;
		this.scheduler = scheduler;
		this.broadcaster = broadcaster;
		this.entityCombatService = entityCombatService;
		this.participants = new ConcurrentSkipListSet<ServerEntity>();
		active = true;
		this.combatTimeout = combatTimeout;
		firstUpdate = true;
	}

	public void addParticipant(ServerEntity participant) {
		logger.debug("Combat.addParticipant");
		participants.add(participant);
		broadcaster.create().to(participant).toArea(participant).send().combat().combatStatusChanged(participant, true);
	}

	public boolean isActive() {
		return active;
	}

	/**
	 * @param broadcaster
	 * @param combatA	  not null
	 * @param combatB	  not null
	 * @return
	 */
	public static Combat merge(Combat combatA, Combat combatB, ActionScheduler scheduler,
										CurrentActionService currentActionService, SmrtBroadcaster<S2CProtocol> broadcaster) {
		Combat merged = new Combat(currentActionService, scheduler, Math.max(combatA.combatTimeout, combatB.combatTimeout), broadcaster,
				combatA.entityCombatService);
		merged.participants.addAll(combatA.participants);
		merged.participants.addAll(combatB.participants);
		merged.lastPerformedActionTime = Math.max(combatA.lastPerformedActionTime, combatB.lastPerformedActionTime);
		merged.active = combatA.active || combatB.active;

		// Make sure participants aren't in multiple combats
		combatA.participants.clear();
		combatB.participants.clear();

		return merged;
	}

	public Collection<ServerEntity> getParticipants() {
		return participants;
	}

	public void updateLastAction(long now, ServerEntity participant) {
		participant.updateLastCombatTimestamp(now);
		lastPerformedActionTime = Math.max(participant.getLastCombatTimestamp(), lastPerformedActionTime);

		if (firstUpdate) {
			firstUpdate = false;
			scheduler.add(new EndOfCombatTesterAction(this, combatTimeout, lastPerformedActionTime, scheduler,
					entityCombatService));
		}
	}


	public long getLastPerformedActionTime(long now) {
		for (ServerEntity participant : participants) {
			if (currentActionService.getCurrentAction(participant) != null) {
				return now;
			}
		}
		return lastPerformedActionTime;
	}

	public boolean shouldStop(long now) {
		return now - getLastPerformedActionTime(now) >= combatTimeout;
	}

	public void stop() {
		if (isActive()) {
			active = false;
			for (ServerEntity participant : participants) {
				entityCombatService.removeFromCombat(participant);
			}
		}
	}

	public void removeParticipant(ServerEntity entity) {
		if (participants.remove(entity)) {
			hostilities.removeAll(entity);
			hostilities.values().removeAll(Lists.newArrayList(entity));
			if (hostilities.isEmpty() && !participants.isEmpty()) {
				entityCombatService.removeFromCombat(participants.iterator().next());
			}
			broadcaster.create().to(entity).toArea(entity).send().combat().combatStatusChanged(entity, false);
		}
	}

	public void addHostility(ServerEntity entity1, ServerEntity entity2) {
		hostilities.put(entity1, entity2);
		hostilities.put(entity2, entity1);
	}
}
