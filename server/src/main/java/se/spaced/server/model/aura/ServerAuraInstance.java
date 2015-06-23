package se.spaced.server.model.aura;

import com.google.common.collect.ImmutableSet;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.server.model.ServerEntity;
import se.spaced.shared.model.aura.ModStat;

public class ServerAuraInstance implements Comparable<ServerAuraInstance>, AuraInstance {
	private final ServerAura aura;
	private final long from;

	public ServerAuraInstance(ServerAura aura, long now) {
		this.aura = aura;
		from = now;

	}

	public boolean hasExpired(long now) {
		if (aura.getDuration() <= 0) {
			return false;
		}
		return getTimeLeft(now) <= 0;
	}

	private long getExpiryTime() {
		return from + aura.getDuration();
	}

	@Override
	public int compareTo(ServerAuraInstance other) {
		return Long.signum(this.from - other.from);
	}

	public ServerAura getAura() {
		return aura;
	}

	public void remove(ServerEntity target) {
		aura.remove(target);
	}

	@Override
	public long getTimeLeft(long now) {
		return getExpiryTime() - now;
	}

	@Override
	public ServerAura getTemplate() {
		return aura;
	}

	@Override
	public UUID getPk() {
		return aura.getPk();
	}

	@Override
	public String getName() {
		return aura.getName();
	}

	@Override
	public String getIconPath() {
		return aura.getIconPath();
	}

	@Override
	public long getDuration() {
		return aura.getDuration();
	}

	@Override
	public boolean isVisible() {
		return aura.isVisible();
	}

	@Override
	public ImmutableSet<ModStat> getMods() {
		return aura.getMods();
	}

	@Override
	public boolean isKey() {
		return aura.isKey();
	}
}
