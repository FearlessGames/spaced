package se.spaced.client.model.spelleffects;

import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.shared.model.EffectType;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;

public class ClientSpellEffect implements SpellEffect {
	private final EffectType type;
	private final UUID pk;

	public ClientSpellEffect(EffectType type, UUID pk) {
		this.type = type;
		this.pk = pk;
	}

	@LuaMethod(name = "GetType")
	public EffectType getType() {
		return type;
	}

	@Override
	public UUID getPk() {
		return pk;
	}
}
