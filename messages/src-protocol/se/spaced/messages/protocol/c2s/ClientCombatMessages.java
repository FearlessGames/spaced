package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.Spell;

@SmrtProtocol
public interface ClientCombatMessages {
	void startSpellCast(Entity target, Spell spell);

	void stopSpellCast();

	void requestCooldownData(Cooldown cooldown);
}
