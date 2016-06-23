package se.spaced.messages.protocol;

import com.google.common.collect.ImmutableSet;
import se.fearless.common.stats.ModStat;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;

public class ClientAuraInstance implements AuraInstance {

	private final AuraTemplate template;
	private final TimeProvider timeProvider;
	private final long expiryTime;

	public ClientAuraInstance(AuraTemplate template, long timeLeft, TimeProvider timeProvider) {
		this.template = template;
		this.timeProvider = timeProvider;
		expiryTime = timeProvider.now() + timeLeft;
	}

	@Override
	@LuaMethod(name = "GetUUID")
	public UUID getPk() {
		return template.getPk();
	}

	@Override
	@LuaMethod(name = "GetName")
	public String getName() {
		return template.getName();
	}

	@Override
	@LuaMethod(name = "GetIconPath")
	public String getIconPath() {
		return template.getIconPath();
	}

	@Override
	@LuaMethod(name = "GetDuration")
	public long getDuration() {
		return template.getDuration();
	}

	@Override
	@LuaMethod(name = "IsVisible")
	public boolean isVisible() {
		return template.isVisible();
	}

	@Override
	@LuaMethod(name = "GetMods")
	public ImmutableSet<ModStat> getMods() {
		return template.getMods();
	}

	@Override
	public long getTimeLeft(long now) {
		return expiryTime - now;
	}

	@LuaMethod(name = "GetTimeLeft")
	public long timeLeft() {
		return getTimeLeft(timeProvider.now());
	}

	@Override
	public AuraTemplate getTemplate() {
		return template;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ClientAuraInstance aura = (ClientAuraInstance) o;

		if (!getPk().equals(aura.getPk())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return getPk().hashCode();
	}

	@Override
	public boolean isKey() {
		return template.isKey();
	}
}


