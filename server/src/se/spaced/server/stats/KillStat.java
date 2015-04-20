package se.spaced.server.stats;

import se.spaced.server.model.spawn.EntityTemplate;

public class KillStat {
	private final long killCount;
	private final EntityTemplate entity;

	public KillStat(long killCount, EntityTemplate entity) {
		this.killCount = killCount;
		this.entity = entity;
	}

	public long getKillCount() {
		return killCount;
	}

	public EntityTemplate getEntity() {
		return entity;
	}
}
