package se.spaced.server.stats;

import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.persistence.dao.impl.PersistableBase;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class KillEntry extends PersistableBase {
	private int killCount;
	@ManyToOne
	private final EntityTemplate killer;
	@ManyToOne
	private final EntityTemplate victim;

	protected KillEntry() {
		this(null, null);
	}

	public KillEntry(EntityTemplate killer, EntityTemplate victim) {
		this.killer = killer;
		this.victim = victim;
	}

	public void increaseKillCount() {
		killCount++;
	}

	public EntityTemplate getKiller() {
		return killer;
	}

	public EntityTemplate getVictim() {
		return victim;
	}

	public int getCount() {
		return killCount;
	}
}
