package se.spaced.messages.protocol;

import com.google.common.collect.ImmutableSet;
import se.fearless.common.stats.ModStat;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;

import java.util.Collection;

public class ClientAuraTemplate implements AuraTemplate {
	private final UUID pk;
	private final String name;
	private final long duration;
	private final String iconPath;
	private final boolean visible;
	private final ImmutableSet<ModStat> modStats;
	private final boolean isKeyAura;

	public ClientAuraTemplate(UUID pk, String name, long duration, String iconPath, boolean visible, Collection<ModStat> stats, boolean keyAura) {
		this.pk = pk;
		this.name = name;
		this.iconPath = iconPath;
		this.duration = duration;
		this.visible = visible;
		isKeyAura = keyAura;
		modStats = ImmutableSet.copyOf(stats);
	}

	@Override
	@LuaMethod(name = "GetUUID")
	public UUID getPk() {
		return pk;
	}

	@Override
	@LuaMethod(name = "GetName")
	public String getName() {
		return name;
	}

	@Override
	@LuaMethod(name = "GetIconPath")
	public String getIconPath() {
		return iconPath;
	}

	@Override
	@LuaMethod(name = "GetDuration")
	public long getDuration() {
		return duration;
	}

	@Override
	@LuaMethod(name = "IsVisible")
	public boolean isVisible() {
		return visible;
	}

	@Override
	@LuaMethod(name = "GetMods")
	public ImmutableSet<ModStat> getMods() {
		return modStats;
	}

	@Override
	@LuaMethod(name = "IsKey")
	public boolean isKey() {
		return isKeyAura;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ClientAuraTemplate aura = (ClientAuraTemplate) o;

		return pk.equals(aura.pk);
	}

	@Override
	public int hashCode() {
		return pk.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}


