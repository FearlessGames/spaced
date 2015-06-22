package se.spaced.server.model.combat;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.SpellListener;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.model.spell.effect.RangeableEffect;

public interface SpellCombatService {
	void doDamage(ServerEntity attacker, ServerEntity target, long now, RangeableEffect origin, String causeName);

	void doMiss(ServerEntity attacker, ServerEntity target, long now, RangeableEffect origin, String causeName);

	void doHeal(ServerEntity healer, ServerEntity target, long now, RangeableEffect origin, String causeName);

	void doCool(ServerEntity performer, ServerEntity target, long now, RangeableEffect origin, String causeName);

	void doRecover(ServerEntity performer, ServerEntity target, long now, RangeableEffect recoverEffect, String causeName);

	void startSpellCast(ServerEntity performer, ServerEntity target, ServerSpell spell, long now, SpellListener listener);

	void stopSpellCast(ServerEntity entity);

	void interruptSpellCast(ServerEntity target);
}
