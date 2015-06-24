package se.spaced.client.model;

import se.fearless.common.time.SystemTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.integration.expose.ReturnValues;
import se.spaced.client.model.cooldown.ClientCooldown;
import se.spaced.client.model.spelleffects.ClientSpellEffect;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.Spell;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.network.protocol.codec.datatype.SpellData;
import se.spaced.shared.network.protocol.codec.datatype.SpellEffect;
import se.spaced.shared.util.math.LinearTimeValue;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ClientSpell implements Spell {

	private static final IntervalInt NULL_RANGE = new IntervalInt(0, 0);

	private final SpellData data;
	private final List<ClientCooldown> clientCooldowns;
	private final TimeProvider timeProvider;

	public ClientSpell(SpellData data, List<ClientCooldown> clientCooldowns, TimeProvider timeProvider) {
		this.data = data;
		this.clientCooldowns = clientCooldowns;
		this.timeProvider = timeProvider;
	}

	public ClientSpell(SpellData spellData) {
		this.data = spellData;
		this.clientCooldowns = Collections.emptyList();
		this.timeProvider = new SystemTimeProvider();
	}

	@Override
	public UUID getPk() {
		return data.getId();
	}

	@LuaMethod(name = "GetId")
	public String getIdAsString() {
		return data.getId().toString();
	}

	@LuaMethod(name = "GetName")
	public String getName() {
		return data.getName();
	}

	public String getEffectResource() {
		return data.getEffectResource();
	}

	@LuaMethod(name = "GetCastTime")
	public int getCastTime() {
		return data.getCastTime();
	}

	public MagicSchool getSchool() {
		return data.getSchool();
	}

	@LuaMethod(name = "GetHeat")
	public int getHeat() {
		return data.getHeat();
	}

	@LuaMethod(name = "GetRange")
	public void getRangesForLua(ReturnValues returnValues) {
		IntervalInt ranges = getRanges();
		returnValues.push(ranges.getStart(), ranges.getEnd());
	}

	public IntervalInt getRanges() {
		IntervalInt range = data.getRanges();
		return range == null ? NULL_RANGE : range;
	}

	@LuaMethod(name = "GetCurrentCooldown")
	public void getCooldown(ReturnValues returnValues) {
		long now = timeProvider.now();
		double globalTimeLeft = 0;
		double globalMax = 0;
		double globalValue = 0;
		double globalRate = 0;

		for (ClientCooldown cooldown : clientCooldowns) {
			if (!cooldown.isReady(now)) {
				final LinearTimeValue linearTimevalue = cooldown.getLinearTimeValue();
				final double currentMax = linearTimevalue.getMaxValue();
				final double currentValue = linearTimevalue.getValue(now);
				final double currentRate = linearTimevalue.getCurrentRate();
				final double diff = currentMax - currentValue;
				final double timeLeft = diff * currentRate;
				if (timeLeft > globalTimeLeft) {
					globalTimeLeft = timeLeft;
					globalMax = currentMax;
					globalValue = currentValue;
					globalRate = currentRate;
				}
			}
		}
		returnValues.push(globalMax * globalRate, globalValue * globalRate, globalTimeLeft);
	}

	@LuaMethod(name = "GetCooldowns")
	public void getCooldowns(ReturnValues returnValues) {
		returnValues.push(clientCooldowns);
	}

	@LuaMethod(name = "GetRequiredAuras")
	public Set<? extends AuraTemplate> getRequiredAuras() {
		return data.getRequiredAuras();
	}

	@LuaMethod(name = "GetEffects")
	public Collection<? extends SpellEffect> getSpellEffects() {
		return data.getSpellEffects();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Spell)) {
			return false;
		}

		Spell that = (Spell) o;
		return getPk().equals(that.getPk());
	}

	@Override
	public int hashCode() {
		return getPk().hashCode();
	}

	@Override
	public String toString() {
		return "ClientSpell{" +
				"data=" + data +
				'}';
	}

	public boolean isCancelOnMove() {
		return data.isCancelOnMove();
	}

	public void updateSpellEffect(ClientSpellEffect spellEffect) {
		data.updateSpellEffect(spellEffect);
	}
}
