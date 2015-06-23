package se.spaced.client.model.spelleffects;

import se.fearlessgames.common.util.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.model.EffectType;

public class ClientGrantSpellEffect extends ClientSpellEffect {
	private Spell clientSpell;

	public ClientGrantSpellEffect(Spell clientSpell, UUID pk) {
		super(EffectType.GRANT_SPELL, pk);
		this.clientSpell = clientSpell;
	}

	@LuaMethod(name = "GetSpell")
	public Spell getSpell() {
		return clientSpell;
	}

	public void setClientSpell(Spell clientSpell) {
		this.clientSpell = clientSpell;
	}
}
