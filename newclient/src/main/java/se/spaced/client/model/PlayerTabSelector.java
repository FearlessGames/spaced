package se.spaced.client.model;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearless.common.util.TimeProvider;

import java.util.*;

@Singleton
public class PlayerTabSelector {
	private static final int TARGET_TIMEOUT = 4 * 1000;
	private final Map<ClientEntity, Long> lastTargetTime;

	private final TimeProvider timeProvider;
	private final TargetsInView targetsInView;
	private final PlayerTargeting playerTargeting;

	@Inject
	public PlayerTabSelector(TimeProvider timeProvider, TargetsInView targetsInView, PlayerTargeting playerTargeting) {
		this.timeProvider = timeProvider;
		this.targetsInView = targetsInView;
		this.playerTargeting = playerTargeting;
		lastTargetTime = new HashMap<ClientEntity, Long>();
	}

	public boolean tabTarget(double maxRange) {
		ClientEntity bestTarget = getBestTarget(maxRange);

		if (bestTarget != null) {
			playerTargeting.setTarget(bestTarget);
		}

		return bestTarget != null;
	}

	public ClientEntity getBestTarget(double maxRange) {
		clearOldTargets();

		List<ClientEntity> entities = targetsInView.getDistanceSortedTargets(maxRange, true);

		ClientEntity target = null;
		long bestTime = Long.MAX_VALUE;
		for (ClientEntity entity : entities) {
			if (!lastTargetTime.containsKey(entity)) {
				target = entity;
				break;
			}

			long lastTime = lastTargetTime.get(entity);
			if (lastTime < bestTime) {
				target = entity;
				bestTime = lastTime;
			}
		}

		if (target != null) {
			lastTargetTime.put(target, timeProvider.now());
		}

		return target;

	}


	private void clearOldTargets() {
		Collection<ClientEntity> clientsToRemove = getOldTargets();
		for (ClientEntity clientEntity : clientsToRemove) {
			lastTargetTime.remove(clientEntity);
		}
	}


	private Collection<ClientEntity> getOldTargets() {
		long now = timeProvider.now();
		Collection<ClientEntity> clientsToRemove = new ArrayList<ClientEntity>();
		for (Map.Entry<ClientEntity, Long> entry : lastTargetTime.entrySet()) {
			if (now - entry.getValue() >= TARGET_TIMEOUT) {
				clientsToRemove.add(entry.getKey());
			}
		}
		return clientsToRemove;
	}
}
