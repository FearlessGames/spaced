package se.spaced.server.model.aura;

import com.google.common.collect.ImmutableSet;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.shared.model.aura.ModStat;

import javax.persistence.Entity;

@Entity
public class KeyAura extends ServerAura {

	protected KeyAura() {
		super();
	}

	public KeyAura(
			String name,
			String iconPath,
			long duration,
			boolean visible,
			boolean removeOnDeath) {
		super(name, iconPath, duration, visible, -1, removeOnDeath);
	}

	@Override
	public ImmutableSet<ModStat> getMods() {
		return ImmutableSet.of();
	}

	@Override
	public void remove(ServerEntity target) {
	}


	@Override
	public void apply(
			ServerEntity performer,
			final ServerEntity target,
			ActionScheduler actionScheduler,
			long now,
			final ServerAuraInstance newAura, final AuraInstanceRemover remover) {
	}

	@Override
	public boolean isKey() {
		return true;
	}
}
