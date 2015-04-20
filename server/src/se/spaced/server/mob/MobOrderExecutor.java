package se.spaced.server.mob;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.fearlessgames.common.util.TimeProvider;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.SpellCombatService;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.movement.MovementService;
import se.spaced.server.model.movement.TransportationMode;
import se.spaced.server.model.spell.ServerSpell;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.MovementPoint;
import se.spaced.shared.world.area.PathPlanner;
import se.spaced.shared.world.area.PolygonGraph;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class MobOrderExecutor implements EntityServiceListener {
	private final MovementService movementService;
	private final TimeProvider timeProvider;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;

	private final ConcurrentHashMap<Mob, MobOrder> orderMap = new ConcurrentHashMap<Mob, MobOrder>();
	private final SpellCombatService spellCombatService;
	public static final double CLOSE_ENOUGH_SQ = 0.5 * 0.5;

	private long lastExecutionTime;
	private final PathPlanner pathPlanner;

	@Inject
	public MobOrderExecutor(
			MovementService movementService, TimeProvider timeProvider,
			SmrtBroadcaster<S2CProtocol> broadcaster,
			SpellCombatService spellCombatService,
			PolygonGraph polygonGraph) {
		this.movementService = movementService;
		this.timeProvider = timeProvider;
		this.broadcaster = broadcaster;
		this.spellCombatService = spellCombatService;

		lastExecutionTime = timeProvider.now();
		pathPlanner = new PathPlanner(polygonGraph);
	}

	public MobOrder getOrder(Mob mob) {
		MobOrder mobOrder = orderMap.get(mob);
		if (mobOrder == null) {
			mobOrder = new MobOrder(timeProvider, movementService, mob);
			MobOrder mobOrder2 = orderMap.putIfAbsent(mob, mobOrder);
			if (mobOrder2 == null) {
				return mobOrder;
			}
		}
		return mobOrder;
	}

	@LuaMethod(global = true, name = "WalkTo")
	public void walkTo(Mob mob, SpacedVector3 target) {
		moveTo(mob, target, TransportationMode.WALK);
	}

	@LuaMethod(global = true, name = "RunTo")
	public void runTo(Mob mob, SpacedVector3 target) {
		moveTo(mob, target, TransportationMode.RUN);
	}

	@LuaMethod(global = true, name = "MoveTo")
	public void moveTo(Mob mob, SpacedVector3 target, TransportationMode mode) {
		SpacedVector3 nextWayPoint = target;
		if (mode.isWalkmeshBound()) {
			nextWayPoint = pathPlanner.getNextWayPoint(mob.getPosition(), target, mob.getName());
			validateWaypoint(target, nextWayPoint, mob.getPosition());
		}
		getOrder(mob).walkTo(nextWayPoint, mob.getSpeed(mode));
	}

	private void validateWaypoint(
			SpacedVector3 target,
			SpacedVector3 nextWayPoint, SpacedVector3 currentPosition) {
		if (nextWayPoint.isNaN()) {
			throw new RuntimeException(String.format("Bad waypoint when going from %s to %s", currentPosition, target));
		}
	}

	@LuaMethod(global = true, name = "StopWalking")
	public void stopWalking(Mob mob) {
		getOrder(mob).walkTo(null, 0.0);
	}

	@LuaMethod(global = true, name = "LookAt")
	public void lookAt(Mob mob, ServerEntity target) {
		getOrder(mob).lookAt(target);
	}

	public void executeMoveMap() {
		long now = timeProvider.now();
		double elapsedTime = (now - lastExecutionTime) / 1000.0;
		lastExecutionTime = now;

		for (Map.Entry<Mob, MobOrder> entry : orderMap.entrySet()) {
			Mob mob = entry.getKey();
			MobOrder order = entry.getValue();

			if (!mob.isAlive()) {
				move(mob, order, AnimationState.DEAD);
				order.getRecorder().forceSend();
				orderMap.remove(mob);
				continue;
			}

			if (order.getTeleportTo() != null) {
				order.getRecorder().add(now, mob.getPosition(), mob.getRotation(), AnimationState.TELEPORT_OUT);
				move(now, mob, order, order.getTeleportTo().getRotation(), order.getTeleportTo().getPosition(), AnimationState.TELEPORT_IN);
				order.setTeleportTo(null);
				continue;
			}

			SpacedVector3 currentPosition = mob.getPosition();
			SpacedVector3 walkTo = order.getWalkTo();
			double speed = order.getSpeed();
			if (walkTo != null) {
				if (SpacedVector3.distanceSq(currentPosition, walkTo) <= CLOSE_ENOUGH_SQ) {
					order.walkTo(null, 0.0);
					walkTo = null;

					move(mob, order, AnimationState.IDLE);
				}
			}

			SpacedVector3 upVector = SpacedVector3.PLUS_J;
			if (walkTo != null) {
				SpacedRotation newRot = VectorMath.lookAt(VectorMath.getDirection(currentPosition, walkTo), upVector);

				SpacedVector3 newPos = VectorMath.moveTowards(currentPosition, walkTo, speed, elapsedTime);

				move(now, mob, order, newRot, newPos, AnimationState.WALK);
			} else {
				ServerEntity target = order.getLookAt();
				if (target != null) {

					SpacedRotation rotation = VectorMath.lookAt(VectorMath.getDirection(currentPosition,
							target.getPosition()), upVector);

					move(now, mob, order, rotation, currentPosition, AnimationState.IDLE);
					order.lookAt(null);
				}
			}
		}
	}

	private void move(Mob mob, MobOrder order, AnimationState state) {
		order.getRecorder().add(timeProvider.now(), mob.getPosition(), mob.getRotation(), state);
	}

	private void move(long now, Mob mob, MobOrder order, SpacedRotation rot, SpacedVector3 pos, AnimationState state) {
		movementService.moveAndRotateEntity(mob, new MovementPoint<AnimationState>(now, state, pos, rot));
		move(mob, order, state);
	}

	@LuaMethod(name = "Say", global = true)
	public void say(Mob performer, String message) {
		broadcaster.create().toArea(performer).send().chat().playerSaid(performer.getName(), message);
	}

	@LuaMethod(name = "Whisper", global = true)
	public void whisper(Mob whisperer, ServerEntity listener, String message) {
		broadcaster.create().to(listener).send().chat().whisperFrom(whisperer.getName(), message);
	}

	@LuaMethod(name = "Emote", global = true)
	public void emote(Mob performer, String emoteFile, String emoteText) {
		broadcaster.create().toArea(performer).send().chat().emote(performer, emoteFile, emoteText);
	}

	@LuaMethod(name = "CastSpell", global = true)
	public void castSpell(Mob performer, ServerEntity target, ServerSpell spell) {
		spellCombatService.startSpellCast(performer, target, spell, timeProvider.now(), null);
	}

	@Override
	public void entityAdded(ServerEntity entity) {
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		orderMap.remove(entity);
	}

	public void teleportTo(Mob mob, PositionalData positionalData) {
		getOrder(mob).teleportTo(positionalData);
	}

	@LuaMethod(name = "BackAwayFrom", global = true)
	public void backAwayFrom(Mob mob, SpacedVector3 p2, double desiredDistance) {
		SpacedVector3 p1 = mob.getPosition();

		// dest = p2 + (p2 - p1).normalize() * -range = p2 + -range * (p2 - p1).normalize()
		SpacedVector3 tmp = p2.subtract(p1); // tmp = p2 - p1

		if (tmp.getNormSq() == 0.0) {
			// TODO: what to do here? Probably really rare
		}
		walkTo(mob, tmp.normalize().scalarMultiply(-desiredDistance).add(p2));

	}
}
