package se.spaced.server.model.aura;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import se.fearless.common.stats.AuraStats;
import se.fearless.common.stats.ModStat;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.Action;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.shared.model.stats.SpacedStatType;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import java.util.Arrays;
import java.util.Set;

@Entity
public class ModStatAura extends ServerAura {
	@ElementCollection(fetch = FetchType.EAGER)
	@Type(type = "se.spaced.server.persistence.dao.impl.hibernate.types.ModStatUserType")
	@Columns(columns = {
			@Column(name = "value"),
			@Column(name = "statType"),
			@Column(name = "operator")}
	)
	@Fetch(FetchMode.SUBSELECT)
	private final Set<ModStat> mods = Sets.newHashSet();

	protected ModStatAura() {
		super();
	}

	public ModStatAura(
			String name,
			String iconPath,
			long duration,
			boolean visible,
			int extraStacks,
			boolean removeOnDeath,
			ModStat... modStats) {
		super(name, iconPath, duration, visible, extraStacks, removeOnDeath);
		mods.addAll(Arrays.asList(modStats));
	}

	@Override
	public ImmutableSet<ModStat> getMods() {
		return ImmutableSet.copyOf(mods);
	}

	@Override
	public void remove(ServerEntity target) {
		for (ModStat mod : mods) {
			AuraStats auraStats = target.getBaseStats().getAuraStatByType(
					(SpacedStatType) mod.getStatType());
			auraStats.removeModStat(mod);
		}
	}


	@Override
	public void apply(
			ServerEntity performer,
			final ServerEntity target,
			ActionScheduler actionScheduler,
			long now,
			final ServerAuraInstance newAura, final AuraInstanceRemover remover) {
		for (ModStat mod : mods) {
			AuraStats auraStats = target.getBaseStats().getAuraStatByType((SpacedStatType) mod.getStatType());
			auraStats.addModStat(mod);
		}
		actionScheduler.add(new Action(now + getDuration()) {
			@Override
			public void perform() {
				if (newAura.hasExpired(getExecutionTime())) {
					remover.remove(target, newAura);
				}
			}
		});
	}
}
