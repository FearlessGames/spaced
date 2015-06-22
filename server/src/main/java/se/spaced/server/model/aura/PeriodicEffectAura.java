package se.spaced.server.model.aura;

import com.google.common.collect.ImmutableSet;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.action.RepeatingSpellAction;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.shared.model.aura.ModStat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class PeriodicEffectAura extends ServerAura {
	private final int numberOfTicks;
	@OneToOne(cascade = CascadeType.ALL)
	private final Effect spellEffect;
	@Transient
	private RepeatingSpellAction repeatingSpellAction;

	protected PeriodicEffectAura() {
		super();
		spellEffect = null;
		numberOfTicks = 0;
	}

	public PeriodicEffectAura(String name, String iconPath, long duration, boolean visible, int extraStacks, int numberOfTicks, Effect spellEffect) {
		super(name, iconPath, duration, visible, extraStacks, true);
		this.numberOfTicks = numberOfTicks;
		this.spellEffect = spellEffect;
	}

	@Override
	public void apply(
			ServerEntity performer,
			final ServerEntity target,
			ActionScheduler actionScheduler,
			long now,
			final ServerAuraInstance newAura, final AuraInstanceRemover remover) {
		repeatingSpellAction = new RepeatingSpellAction(actionScheduler,
				now + getDuration() / numberOfTicks,
				spellEffect,
				performer,
				target,
				getName(),
				numberOfTicks,
				getDuration());
		repeatingSpellAction.addListener(new SpellActionListener(remover, target, newAura, numberOfTicks));
		actionScheduler.add(repeatingSpellAction);
	}

	@Override
	public void remove(ServerEntity target) {
		repeatingSpellAction.cancel();
	}


	@Override
	public ImmutableSet<ModStat> getMods() {
		return ImmutableSet.of();
	}


	private static class SpellActionListener implements RepeatingSpellAction.Listener {
		private int numberOfTicksPerformed;
		private final int numberOfTicks;
		private final AuraInstanceRemover remover;
		private final ServerEntity target;
		private final ServerAuraInstance newAura;

		SpellActionListener(
				AuraInstanceRemover remover,
				ServerEntity target,
				ServerAuraInstance newAura,
				int numberOfTicks) {
			this.remover = remover;
			this.target = target;
			this.newAura = newAura;
			this.numberOfTicks = numberOfTicks;
		}

		@Override
		public void onPerform() {
			numberOfTicksPerformed++;
			if (numberOfTicksPerformed == numberOfTicks) {
				remover.remove(target, newAura);
			}
		}
	}
}
