package se.spaced.messages.protocol;

import se.fearless.common.uuid.UUID;
import se.spaced.shared.util.math.LinearTimeValue;

public class CooldownData implements Cooldown {
	private final UUID uuid;
	private final LinearTimeValue linearTimeValue;

	public CooldownData(UUID uuid, LinearTimeValue linearTimeValue) {
		this.uuid = uuid;
		this.linearTimeValue = linearTimeValue;
	}

	@Override
	public UUID getPk() {
		return uuid;
	}

	public LinearTimeValue getLinearTimeValue() {
		return linearTimeValue;
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
