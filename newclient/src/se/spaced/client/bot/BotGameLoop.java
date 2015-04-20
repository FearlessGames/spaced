package se.spaced.client.bot;

import com.google.inject.Inject;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.ardortech.math.VectorMath;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.PlayerTargeting;
import se.spaced.client.model.SpellDirectory;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.listener.LoginListener;
import se.spaced.client.model.player.PlayerActions;
import se.spaced.client.net.smrt.MessageQueue;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotGameLoop extends AbstractBotGameLoop implements LoginListener {
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final SpellDirectory spellDirectory;

	private final Random random;

	private final UserCharacter userCharacter;
	private final PlayerActions playerActions;
	private final PlayerTargeting targeting;
	private SpacedVector3 moveTarget;
	private ClientEntity attackTargetEntity;
	private ClientSpell attackSpell;

	private static final double MIN_MOVE_TIME = 0.1;
	private static final double MIN_MOVE_DISTANCE = 2;
	private static final double MOVE_SPEED = 0.5;

	private final ActionState connecting = new ConnectingState();
	private final ActionState lookingForMoveTargetState = new LookingForMoveTargetState();
	private final ActionState lookingForAttackTargetState = new LookingforAttackTargetState();
	private final ActionState attackingState = new AttackingState();
	private final ActionState movingState = new MovingState();
	private final ActionState deadState = new DeadState();

	private ActionState actionState = connecting;

	@Inject
	public BotGameLoop(
			BotConnection bot,
			SpellDirectory spellDirectory,
			UserCharacter userCharacter,
			MessageQueue queue,
			PlayerActions playerActions,
			PlayerTargeting targeting,
			ActiveCache<Entity, ClientEntity> entityCache) {
		super(queue, bot);

		this.spellDirectory = spellDirectory;
		this.entityCache = entityCache;
		this.userCharacter = userCharacter;
		this.playerActions = playerActions;
		this.targeting = targeting;

		random = new Random();
	}

	@Override
	public void update() {

		attackSpell = spellDirectory.getSpell("Lazor blast");
		if (attackSpell != null && actionState == connecting) {
			actionState = lookingForMoveTargetState;
		}

		if (userCharacter != null && userCharacter.getUserControlledEntity() != null && !userCharacter.isAlive() && actionState != deadState) {
			actionState = deadState;
		}

		actionState.act();
	}


	private abstract static class ActionState {
		abstract void act();
	}

	private static class ConnectingState extends ActionState {
		@Override
		void act() {
		}
	}

	private class LookingForMoveTargetState extends ActionState {
		@Override
		void act() {
			int i = random.nextInt(10);
			if (i > 5) {
				actionState = lookingForAttackTargetState;
			} else {
				SpacedVector3 pos = userCharacter.getPosition();
				double x = random.nextGaussian();
				double z = random.nextGaussian();
				moveTarget = pos.add(new SpacedVector3(x * 20, 0, z * 20));
				targeting.clearTarget();
				actionState = movingState;
			}
		}
	}

	private class LookingforAttackTargetState extends ActionState {

		@Override
		void act() {
			int count = entityCache.getValues().size();
			int entityIndex = random.nextInt(count);

			ClientEntity entity = new ArrayList<ClientEntity>(entityCache.getValues()).get(entityIndex);
			attackTargetEntity = entity;
			targeting.setTarget(entity);
			actionState = movingState;

		}
	}

	private class MovingState extends ActionState {
		@Override
		void act() {
			SpacedVector3 nextPosition = getNextPosition();

			if (nextPosition == null) {
				if (attackTargetEntity != null) {
					actionState = attackingState;
					return;
				} else {
					actionState = lookingForMoveTargetState;
					return;
				}
			}

			SpacedRotation rot = getRotation();
			userCharacter.setPositionalData(new PositionalData(nextPosition, rot));
		}

		private SpacedVector3 getNextPosition() {
			if (attackTargetEntity != null) {
				moveTarget = getAttackTargetEntityPosition();
			}

			SpacedVector3 currentPosition = userCharacter.getPosition();

			SpacedVector3 direction = moveTarget.subtract(currentPosition);
			SpacedVector3 normalizedDirection = direction.normalize();

			SpacedVector3 movement = normalizedDirection.scalarMultiply(MOVE_SPEED);

			SpacedVector3 newPosition = currentPosition.add(movement);

			double distance = SpacedVector3.distance(moveTarget, newPosition);
			double travelTime = distance / MOVE_SPEED;

			if (travelTime < MIN_MOVE_TIME || distance < MIN_MOVE_DISTANCE) {
				return null;
			} else {
				return currentPosition;
			}
		}

		private SpacedVector3 getAttackTargetEntityPosition() {
			SpacedVector3 currentPosition = userCharacter.getPosition();

			SpacedVector3 direction = attackTargetEntity.getPosition().subtract(currentPosition);

			SpacedVector3 normalizedDirection = direction.normalize();

			SpacedVector3 fireDistance = normalizedDirection.scalarMultiply(10);

			return attackTargetEntity.getPosition().subtract(fireDistance);
		}

		private SpacedRotation getRotation() {
			SpacedVector3 currentPosition = userCharacter.getPosition();

			SpacedVector3 direction = moveTarget.subtract(currentPosition);
			SpacedVector3 normalizedDirection = direction.normalize();

			return VectorMath.lookAt(normalizedDirection, SpacedVector3.PLUS_J);
		}
	}

	private class AttackingState extends ActionState {
		private long nextCastTime = 0;

		@Override
		void act() {
			// TODO: remove the sucky icky System.currentTimeMillis()
			if (attackTargetEntity.isAlive() && System.currentTimeMillis() > nextCastTime) {
				double distance = SpacedVector3.distance(attackTargetEntity.getPosition(), userCharacter.getPosition());
				if (distance < attackSpell.getRanges().getEnd() && distance > attackSpell.getRanges().getStart()) {
					playerActions.startSpellCast(attackSpell);
					nextCastTime = System.currentTimeMillis() + attackSpell.getCastTime() + 1000;
				} else {
					actionState = movingState;
				}
				return;
			}

			if (!attackTargetEntity.isAlive()) {
				targeting.clearTarget();
				attackTargetEntity = null;
				actionState = lookingForMoveTargetState;
			}
		}
	}

	private class DeadState extends ActionState {
		private boolean pendingResurrect;

		@Override
		void act() {
			if (!pendingResurrect) {
				playerActions.resurrect();
				pendingResurrect = true;
				targeting.clearTarget();
			} else {
				if (userCharacter.isAlive()) {
					actionState = lookingForMoveTargetState;
				}
			}
		}
	}

	@Override
	public void exit() {
		// TODO: Handle this method (and call it)
	}

	@Override
	public void successfulPlayerLogin() {
		actionState = lookingForMoveTargetState;
	}

	@Override
	public void failedPlayerLogin(final String message) {
		// Ignored, implemented elsewhere
	}

	@Override
	public void characterListUpdated(final List<EntityData> characters) {
		// Ignored, implemented elsewhere
	}

	@Override
	public void successfulPlayerLogout() {
		actionState = connecting;
	}
}