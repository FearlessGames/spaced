package se.spaced.client.model.spelleffects;

import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.spaced.shared.model.EffectType;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

public class ClientRangeSpellEffect extends ClientSpellEffect {
	private final IntervalInt range;
	private final MagicSchool school;

	public ClientRangeSpellEffect(EffectType type, IntervalInt range, UUID pk, MagicSchool school) {
		super(type, pk);
		this.range = range;
		this.school = school;
	}

	public IntervalInt getRange() {
		return range;
	}

	@LuaMethod(name = "GetRange")
	public void getRangeLua(ReturnValues returnValues) {
		returnValues.push(range.getStart(), range.getEnd());
	}

	@LuaMethod(name = "GetSchool")
	public MagicSchool getSchool() {
		return school;
	}
}
