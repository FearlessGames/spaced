package se.spaced.client.model.cooldown;

import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.CooldownData;
import se.spaced.shared.util.math.LinearTimeValue;

public class ClientCooldown implements Cooldown {
	private final UUID uuid;
	private final LinearTimeValue linearTimeValue;

	public ClientCooldown(UUID uuid, LinearTimeValue linearTimeValue) {
		this.uuid = uuid;
		this.linearTimeValue = linearTimeValue;
	}

	public ClientCooldown(CooldownData cooldownData) {
		this(cooldownData.getPk(), cooldownData.getLinearTimeValue());
	}

	@Override
	@LuaMethod(name = "GetId")
	public UUID getPk() {
		return uuid;
	}

	@LuaMethod(name = "GetMax")
	public double maxValue() {
		return linearTimeValue.getMaxValue();
	}

	public LinearTimeValue getLinearTimeValue() {
		return linearTimeValue;
	}

	public boolean isReady(long now) {
		return linearTimeValue.isReady(now, linearTimeValue.getMaxValue());
	}

	public void consume(long now) {
		linearTimeValue.setValue(now, 0D);
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
