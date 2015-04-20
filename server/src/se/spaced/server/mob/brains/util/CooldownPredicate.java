package se.spaced.server.mob.brains.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.server.model.ServerEntity;

import java.util.concurrent.ConcurrentMap;

public class CooldownPredicate implements Predicate<ServerEntity> {

	private final TimeProvider timeProvider;
	private final long cooldown;
	private final ConcurrentMap<ServerEntity, Long> lastActionTimestamp = Maps.newConcurrentMap();

	public CooldownPredicate(TimeProvider timeProvider, long cooldown) {
		this.timeProvider = timeProvider;
		this.cooldown = cooldown;
	}

	@Override
	public boolean apply(ServerEntity serverEntity) {
		Long lastTime = lastActionTimestamp.get(serverEntity);
		return lastTime == null || timeProvider.now() - lastTime >= cooldown;
	}

	public void updateLastTime(ServerEntity entity) {
		lastActionTimestamp.put(entity, timeProvider.now());
	}
}
