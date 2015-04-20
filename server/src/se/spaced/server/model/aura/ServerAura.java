package se.spaced.server.model.aura;

import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Entity;

@Entity
public abstract class ServerAura extends ExternalPersistableBase implements Comparable<ServerAura>, AuraTemplate, NamedPersistable {

	private final String name;

	private final String iconPath;

	private final long duration;
	private final boolean visible;

	private final int extraStacks;

	private final boolean removeOnDeath;

	protected ServerAura() {
		this(null, null, 0, false, 0, false);
	}

	protected ServerAura(String name, String iconPath, long duration, boolean visible, int extraStacks, boolean removeOnDeath) {
		this.name = name;
		this.iconPath = iconPath;
		this.duration = duration;
		this.visible = visible;
		this.extraStacks = extraStacks;
		this.removeOnDeath = removeOnDeath;
	}

	public abstract void apply(
			ServerEntity performer,
			ServerEntity target,
			ActionScheduler actionScheduler,
			long now,
			ServerAuraInstance newAura, AuraInstanceRemover remover);

	public abstract void remove(ServerEntity target);

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	public int getMaxStacks() {
		return 1 + extraStacks;
	}

	public boolean isRemoveOnDeath() {
		return removeOnDeath;
	}

	@Override
	public int compareTo(ServerAura other) {
		return getPk().compareTo(other.getPk());
	}

	@Override
	public String toString() {
		return "ServerAura{" +
				"name='" + name + '\'' +
				", duration=" + duration +
				'}';
	}

	@Override
	public boolean isKey() {
		return false;
	}
}
