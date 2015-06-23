package se.spaced.server.mob.brains;

import se.ardortech.math.SpacedVector3;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.*;
import se.spaced.messages.protocol.s2c.*;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.relations.RelationsService;
import se.spaced.server.model.spawn.ProximityAggroParameters;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.MagicSchool;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.playback.RecordingPoint;

import java.util.Map;

public class AggroingBrain extends AbstractMobBrain implements ServerCombatMessages, ServerMovementMessages, ServerEntityDataMessages {
	public static final SpacedVector3 DISTANCE_WEIGHTS = new SpacedVector3(1.0, 6.0, 1.0);
	private final S2CProtocol receiver = new S2CMultiDispatcher(
			S2CAdapters.createServerCombatMessages(this),
			S2CAdapters.createServerMovementMessages(this),
			S2CAdapters.createServerEntityDataMessages(this));

	private final TimeProvider timeProvider;
	private final EntityCombatService entityCombatService;
	private final int proximityDistanceSquared;
	private final int socialAggroDistanceSquared;
	private final RelationsService relationsService;


	public AggroingBrain(
			Mob mob,
			MobOrderExecutor orderExecutor,
			TimeProvider timeProvider,
			RelationsService relationsService,
			ProximityAggroParameters proximityAggroParameters,
			EntityCombatService entityCombatService) {
		super(mob, orderExecutor);
		this.timeProvider = timeProvider;
		this.relationsService = relationsService;
		this.entityCombatService = entityCombatService;
		this.socialAggroDistanceSquared = proximityAggroParameters.getSocialAggroDistance() * proximityAggroParameters.getSocialAggroDistance();
		proximityDistanceSquared = proximityAggroParameters.getProximityAggroDistance() * proximityAggroParameters.getProximityAggroDistance();
	}

	@Override
	public MobDecision act(long now) {
		return MobDecision.UNDECIDED;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return receiver;
	}

	private void addAggro(ServerEntity attacker) {
		entityCombatService.enterCombat(mob, attacker, timeProvider.now(), true);
		mob.addAggro(attacker, 1);
	}

	private void checkProximityAggro(Entity entity) {
		ServerEntity target = (ServerEntity) entity;
		if (mob.isAggroWith(target) || !mob.isAlive()) {
			return;
		}
		SpacedVector3 targetPosition = target.getPosition();
		if (weightedDistanceSq(targetPosition, mob.getPosition(), DISTANCE_WEIGHTS) < proximityDistanceSquared) {
			if (relationsService.hates(mob, target)) {
				addAggro(target);
			}
		}
	}

	private double weightedDistanceSq(SpacedVector3 v1, SpacedVector3 v2, SpacedVector3 weights) {
		final double dx = (v2.getX() - v1.getX()) * weights.getX();
		final double dy = (v2.getY() - v1.getY()) * weights.getY();
		final double dz = (v2.getZ() - v1.getZ()) * weights.getZ();
		return dx * dx + dy * dy + dz * dz;
	}

	@Override
	public void teleportTo(PositionalData positionalData) {
	}

	@Override
	public void sendPlayback(Entity entity, RecordingPoint<AnimationState> playbackData) {
		checkProximityAggro(entity);
	}

	@Override
	public void restartRecorder(PositionalData positionalData) {
	}

	@Override
	public void combatStatusChanged(Entity entity, boolean isStart) {

	}

	@Override
	public void entityStartedSpellCast(Entity entity, Entity target, Spell spell) {

	}

	@Override
	public void entityCompletedSpellCast(Entity entity, Entity target, Spell spell) {

	}

	@Override
	public void entityStoppedSpellCast(Entity entity, Spell spell) {

	}

	@Override
	public void entityWasKilled(Entity attacker, Entity target) {

	}

	@Override
	public void entityDamaged(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school) {
		checkAggro(from, to);
	}

	private void checkAggro(Entity from, Entity to) {
		ServerEntity attacker = (ServerEntity) from;
		ServerEntity target = (ServerEntity) to;

		if (relationsService.protects(mob, target)) {
			if (SpacedVector3.distanceSq(target.getPosition(), mob.getPosition()) < socialAggroDistanceSquared) {
				addAggro(attacker);
			}
		}
	}

	@Override
	public void entityHealed(Entity from, Entity to, int amount, int newHealth, String source, MagicSchool school) {
	}

	@Override
	public void entityHeatAffected(Entity from, Entity to, int amount, int newHeat, String source, MagicSchool school) {
		if (amount > 0) {
			checkAggro(from, to);
		}
	}

	@Override
	public void entityMissed(Entity from, Entity to, String source, MagicSchool school) {
		checkAggro(from, to);
	}

	@Override
	public void effectApplied(Entity from, Entity on, String resource) {
	}

	@Override
	public void gainedAura(Entity entity, AuraInstance aura) {
	}

	@Override
	public void lostAura(Entity entity, AuraInstance aura) {
	}

	@Override
	public void cooldownConsumed(Cooldown coolDown) {
	}

	@Override
	public void cooldownData(CooldownData cooldownData) {
	}

	@Override
	public void entityAbsorbedDamaged(
			Entity attacker, Entity target, int absorbedDamage, int value, String causeName, MagicSchool school) {
		checkAggro(attacker, target);
	}

	@Override
	public void entityRecovered(
			Entity performer, Entity target, int amount, int value, String causeName, MagicSchool school) {

	}

	@Override
	public void updateStats(Entity entity, EntityStats stats) {
	}

	@Override
	public void entityAppeared(Entity entity, EntityData entityData, Map<ContainerType, ? extends ItemTemplate> items) {
		checkProximityAggro(entity);
	}

	@Override
	public void entityDisappeared(Entity entity) {
	}

	public void doRespawn(PositionalData positionalData, EntityStats stats) {
	}

	@Override
	public void entityDespawned(Entity entity) {
	}

	@Override
	public void entityChangedTarget(Entity entity, Entity target) {
	}

	@Override
	public void entityClearedTarget(Entity entity) {
	}

	@Override
	public void unknownEntityName(String name) {
	}

	@Override
	public void changedTarget(Entity newTarget) {
	}

	@Override
	public void clearedTarget() {
	}
}
