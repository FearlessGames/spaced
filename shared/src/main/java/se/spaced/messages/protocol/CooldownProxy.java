package se.spaced.messages.protocol;

import se.fearless.common.uuid.UUID;

public class CooldownProxy implements Cooldown {
	private final UUID pk;

	public CooldownProxy(UUID pk) {
		this.pk = pk;
	}

	@Override
	public UUID getPk() {
		return pk;
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
