package se.spaced.server.model.cooldown;

import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;

import javax.persistence.Entity;

@Entity
public class CooldownTemplate extends ExternalPersistableBase implements NamedPersistable, Cooldown {
	public static final CooldownTemplate NO_COOLDOWN = noCooldown();
	private static CooldownTemplate noCooldown() {
		final CooldownTemplate template = new CooldownTemplate(0.0, "generic cooldown");
		template.setPk(UUID.fromString("f7e2adeb-15a6-4664-9f73-9e07d9ff4b9a"));
		return template;
	}

	private final String name;
	private final double cooldownTime;

	public CooldownTemplate() {
		name = null;
		cooldownTime = 0.0;
	}

	public CooldownTemplate(double cooldownTime, String name) {
		this.cooldownTime = cooldownTime;
		this.name = name;
	}

	public double getCooldownTime() {
		return cooldownTime;
	}

	public SimpleCooldown createCooldown(long now) {
		return new SimpleCooldown(now, this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Cooldown)) {
			return false;
		}

		Cooldown that = (Cooldown) o;
		return getPk().equals(that.getPk());
	}

	@Override
	public int hashCode() {
		return getPk().hashCode();
	}
	
}
