package se.spaced.shared.network.protocol.codec.datatype;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.Cooldown;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class SpellData {
	private final UUID id;
	private final String name;
	private final int castTime;
	private final MagicSchool school;
	private final boolean requiresHostileTarget;
	private final boolean cancelOnMove;
	private final IntervalInt ranges;
	private final String effectResource;
	private final int heat;

	private final Collection<? extends Cooldown> cooldowns;
	private final SortedSet<AuraTemplate> requiredAuras;
	private final Map<UUID, SpellEffect> effects;

	public SpellData(
			UUID id, String name, int castTime, MagicSchool school, boolean requiresHostileTarget,
			IntervalInt ranges,
			String effectResource,
			int heat,
			Collection<? extends Cooldown> cooldowns,
			boolean cancelOnMove,
			Set<? extends AuraTemplate> requiredAuras,
			Iterable<? extends SpellEffect> effects) {
		this.id = id;
		this.name = name;
		this.castTime = castTime;
		this.cooldowns = cooldowns;
		this.school = school;
		this.requiresHostileTarget = requiresHostileTarget;
		this.ranges = ranges;
		this.effectResource = effectResource;
		this.heat = heat;
		this.cancelOnMove = cancelOnMove;
		this.requiredAuras = Sets.newTreeSet(new Comparator<AuraTemplate>() {
			@Override
			public int compare(AuraTemplate a1, AuraTemplate a2) {
				return a1.getName().compareTo(a2.getName());
			}
		});
		this.requiredAuras.addAll(requiredAuras);
		ImmutableMap<UUID, ? extends SpellEffect> spellEffectsByUuid = Maps.uniqueIndex(effects, new Function<SpellEffect, UUID>() {
			@Override
			public UUID apply(SpellEffect spellEffect) {
				return spellEffect.getPk();
			}
		});
		this.effects = Maps.newHashMap(spellEffectsByUuid);
	}

	public SpellData(
			UUID id, String name, int castTime, MagicSchool school, boolean requiresHostileTarget,
			IntervalInt ranges, String effectResource, int heat, boolean cancelOnMove) {
		this(id, name, castTime, school, requiresHostileTarget, ranges, effectResource, heat, Collections.emptyList(),
				cancelOnMove, Collections.emptySet(), Collections.emptyList());
	}

	public SpellData(UUID id) {
		this(id, "", 0, MagicSchool.PHYSICAL, false, new IntervalInt(0, Integer.MAX_VALUE), "NotSpecified", 1,
				Collections.emptyList(), false, Collections.emptySet(), Collections.emptyList());
	}

	public UUID getId() {
		return id;
	}

	public int getCastTime() {
		return castTime;
	}

	public MagicSchool getSchool() {
		return school;
	}

	public boolean isRequiresHostileTarget() {
		return requiresHostileTarget;
	}

	public String getName() {
		return name;
	}

	public int getHeat() {
		return heat;
	}

	@Override
	public String toString() {
		return "SpellData{" +
				"id=" + id +
				", name='" + name + '\'' +
				", castTime=" + castTime +
				", school=" + school +
				", requiresHostileTarget=" + requiresHostileTarget +
				", ranges=" + ranges +
				", effectResource='" + effectResource + '\'' +
				", effects =" + effects +
				'}';
	}

	public IntervalInt getRanges() {
		return ranges;
	}

	public String getEffectResource() {
		return effectResource;
	}

	public boolean isCancelOnMove() {
		return cancelOnMove;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SpellData spellData = (SpellData) o;

		return id.equals(spellData.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public Collection<? extends Cooldown> getCooldowns() {
		return cooldowns;
	}

	public Set<? extends AuraTemplate> getRequiredAuras() {
		return requiredAuras;
	}

	public Collection<? extends SpellEffect> getSpellEffects() {
		return effects.values();
	}

	public void updateSpellEffect(SpellEffect spellEffect) {
		effects.put(spellEffect.getPk(), spellEffect);
	}
}
