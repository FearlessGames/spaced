package se.spaced.server.model.spell;

import se.spaced.shared.network.protocol.codec.datatype.SpellData;

public class SpellDataFactory {
	private SpellDataFactory() {
	}

	public static SpellData createSpellData(ServerSpell spell) {
		return new SpellData(spell.getPk(), spell.getName(), spell.getCastTime(),
				spell.getSchool(), spell.requiresHostileTarget(), spell.getRanges(),
				spell.getEffectResource(), spell.getHeatContribution(),
				spell.getCoolDown().getCooldownTemplates(), spell.cancelOnMove(), spell.getRequiredAuras(), spell.getEffects());
	}

}
