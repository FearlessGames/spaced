package se.spaced.server.model.spell;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.aura.ServerAura;
import se.spaced.server.model.cooldown.CooldownSetTemplate;
import se.spaced.server.model.cooldown.CooldownTemplate;
import se.spaced.server.model.spell.effect.Effect;
import se.spaced.server.model.spell.effect.ProjectileEffect;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.ExternalPersistableBase;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.TargetingType;
import se.spaced.shared.util.math.interval.IntervalInt;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
public class ServerSpell extends ExternalPersistableBase implements Spell, Comparable<ServerSpell>, NamedPersistable {

	private String name;

	private int castTime;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Effect> effects;

	@Enumerated(EnumType.STRING)
	@Column(length = 11, nullable = false)
	private MagicSchool school;

	@Enumerated(EnumType.STRING)
	@Column(length = 11, nullable = false)
	private TargetingType targetingType;

	private boolean requiresHostileTarget;
	private boolean cancelOnMove;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "start", column = @Column(name = "closeLimit")),
			@AttributeOverride(name = "end", column = @Column(name = "farLimit"))
	})
	private IntervalInt ranges;

	private String effectResource;
	private int heatContribution;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<ServerAura> requiredAuras = Sets.newHashSet();

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private final CooldownSetTemplate cooldowns;

	protected ServerSpell() {
		cooldowns = new CooldownSetTemplate();
	}

	private ServerSpell(Builder builder) {
		super(builder.uuid);
		name = builder.name;
		castTime = builder.castTime;
		school = builder.school;
		effects = builder.effects;
		requiresHostileTarget = builder.requiresHostileTarget;
		cancelOnMove = builder.cancelOnMove;
		ranges = builder.ranges;
		effectResource = builder.effectResource;
		heatContribution = builder.heatContribution;
		targetingType = builder.targetingType;
		cooldowns = new CooldownSetTemplate();
		requiredAuras.addAll(builder.requiredAuras);
	}

	@Override
	@LuaMethod(name = "GetName")
	public String getName() {
		return name;
	}

	@LuaMethod(name = "GetCastTime")
	public int getCastTime() {
		return castTime;
	}

	public void perform(long now, ServerEntity performer, ServerEntity target) {
		for (Effect effect : effects) {
			effect.apply(now, performer, target, this.getName());
		}
	}

	public boolean requiresHostileTarget() {
		return requiresHostileTarget;
	}

	public CooldownSetTemplate getCoolDown() {
		return cooldowns;
	}

	public MagicSchool getSchool() {
		return school;
	}

	public boolean cancelOnMove() {
		return cancelOnMove;
	}

	@LuaMethod(name = "GetRanges")
	public IntervalInt getRanges() {
		return ranges;
	}

	@Override
	public String toString() {
		return "ServerSpell{" +
				"name='" + name + '\'' +
				", castTime=" + castTime +
				", effects=" + effects +
				", school=" + school +
				", requiresHostileTarget=" + requiresHostileTarget +
				", cancelOnMove=" + cancelOnMove +
				", ranges=" + ranges +
				", effectResource='" + effectResource + '\'' +
				'}';
	}

	public static ServerSpell createProjectileSpell(
			UUID uuid, String name, int castTime, MagicSchool school, Set<Effect> effects,
			boolean requiresHostileTarget,
			boolean cancelOnMove,
			IntervalInt ranges,
			double speed,
			ActionScheduler actionScheduler,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			AtomicInteger projectileIdCounter) {

		ProjectileEffect projectileEffect = new ProjectileEffect(actionScheduler,
				smrtBroadcaster,
				projectileIdCounter
		);
		projectileEffect.setSpeed(speed);
		for (Effect effect : effects) {
			projectileEffect.addImpactEffect(effect);
		}
		return new Builder(name).uuid(uuid).castTime(castTime).school(school).spellEffects(projectileEffect).requiresHostileTarget(
				requiresHostileTarget).
				cancelOnMove(cancelOnMove).ranges(ranges).build();
	}

	public static ServerSpell createDirectEffectSpell(
			UUID uuid, String name, int castTime, MagicSchool school, Effect effect,
			boolean requiresHostileTarget, boolean cancelOnMove, IntervalInt ranges) {
		return new Builder(name).uuid(uuid).castTime(castTime).school(school).spellEffects(effect).requiresHostileTarget(
				requiresHostileTarget).
				cancelOnMove(cancelOnMove).ranges(ranges).build();
	}

	public static ServerSpell createDirectEffectSpell(
			UUID uuid, String name, int castTime, MagicSchool school, Effect effect,
			boolean requiresHostileTarget, boolean cancelOnMove, IntervalInt ranges, CooldownTemplate cooldownTemplate) {
		return new Builder(name).uuid(uuid).castTime(castTime).school(school).spellEffects(effect).requiresHostileTarget(
				requiresHostileTarget).
				cancelOnMove(cancelOnMove).ranges(ranges).cooldown(cooldownTemplate).build();
	}

	public String getEffectResource() {
		return effectResource;
	}

	public Set<Effect> getEffects() {
		return effects;
	}

	@Override
	public int compareTo(ServerSpell spell) {
		return name.compareTo(spell.name);
	}

	public int getHeatContribution() {
		return heatContribution;
	}

	public Set<ServerAura> getRequiredAuras() {
		return requiredAuras;
	}

	public TargetingType getTargetingType() {

		return targetingType;
	}

	public static class Builder implements se.spaced.shared.util.Builder<ServerSpell> {
		private final String name;
		private int castTime;
		private MagicSchool school = MagicSchool.PHYSICAL;
		private final Set<Effect> effects = Sets.newHashSet();
		private boolean requiresHostileTarget;
		private boolean cancelOnMove;
		private IntervalInt ranges = new IntervalInt(0, Integer.MAX_VALUE);
		private UUID uuid = UUID.ZERO;
		private String effectResource = "NotSpecified";
		private int heatContribution = 1;
		private TargetingType targetingType = TargetingType.TARGET;
		private final List<CooldownTemplate> cooldownTemplates = Lists.newArrayList();
		private final Set<ServerAura> requiredAuras = Sets.newHashSet();

		public Builder(String name) {
			this.name = name;
		}

		public Builder castTime(int castTime) {
			this.castTime = castTime;
			return this;
		}

		public Builder school(MagicSchool school) {
			this.school = school;
			return this;
		}

		public Builder addSpellEffect(Effect effect) {
			this.effects.add(effect);
			return this;
		}

		public Builder spellEffects(Effect... effects) {
			this.effects.addAll(Arrays.asList(effects));
			return this;
		}

		public Builder requiredAuras(ServerAura... auras) {
			requiredAuras.addAll(Arrays.asList(auras));
			return this;
		}

		public Builder requiresHostileTarget(boolean requiresHostileTarget) {
			this.requiresHostileTarget = requiresHostileTarget;
			return this;
		}

		public Builder cancelOnMove(boolean cancelOnMove) {
			this.cancelOnMove = cancelOnMove;
			return this;
		}

		public Builder ranges(IntervalInt ranges) {
			this.ranges = ranges;
			return this;
		}

		public Builder ranges(int start, int end) {
			this.ranges = new IntervalInt(start, end);
			return this;
		}

		public Builder uuid(UUID uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder effectResource(String effectResource) {
			this.effectResource = effectResource;
			return this;
		}

		public Builder heatContribution(int heatContribution) {
			this.heatContribution = heatContribution;
			return this;
		}

		public Builder targetingType(TargetingType targetingType) {
			this.targetingType = targetingType;
			return this;
		}

		@Override
		public ServerSpell build() {
			ServerSpell serverSpell = new ServerSpell(this);
			for (CooldownTemplate cooldownTemplate : cooldownTemplates) {
				serverSpell.getCoolDown().add(cooldownTemplate);
			}
			return serverSpell;
		}

		public Builder cooldown(CooldownTemplate cooldownTemplate) {
			cooldownTemplates.add(cooldownTemplate);
			return this;
		}
	}

}
