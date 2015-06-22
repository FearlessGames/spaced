package se.spaced.server.model.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.spell.effect.RangeableEffect;

public class EnvironmentalDamageAction extends TargetedAction {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SpellCombatService spellCombatService;
	private final String causeName;
	private final RangeableEffect effect;

	public EnvironmentalDamageAction(long executionTime, ServerEntity target, SpellCombatService spellCombatService, String causeName, RangeableEffect effect) {
		super(executionTime, target, target);
		this.spellCombatService = spellCombatService;
		this.causeName = causeName;
		this.effect = effect;
	}

	@Override
	public void perform() {
		long now = getExecutionTime();
		spellCombatService.doDamage(getPerformer(), target, now, effect, causeName);
	}
}