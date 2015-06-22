package se.spaced.server.mob.brains;

import com.google.common.base.Predicates;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.ServerMovementMessages;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.MobWhisperer;
import se.spaced.server.mob.brains.util.CooldownPredicate;
import se.spaced.server.mob.brains.util.ProximityPredicate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spawn.WhisperMessage;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.RecordingPoint;

import java.util.EnumSet;

public class ProximityWhisperBrain implements MobBrain, ServerMovementMessages {
	private final S2CProtocol receiver = new S2CMultiDispatcher(S2CAdapters.createServerMovementMessages(this));
	private final Mob mob;
	private final WhisperMessage whisperMessage;
	private final MobWhisperer mobWhisperer;

	CooldownPredicate cooldownPredicate;
	private final ProximityPredicate proximityPredicate;

	public ProximityWhisperBrain(
			Mob mob,
			WhisperMessage whisperMessage,
			MobOrderExecutor mobOrderExecutor,
			TimeProvider timeProvider) {
		this.mob = mob;
		this.whisperMessage = whisperMessage;
		mobWhisperer = new MobWhisperer(mobOrderExecutor, mob);
		proximityPredicate = new ProximityPredicate(mob, whisperMessage.getDistance());
		cooldownPredicate = new CooldownPredicate(timeProvider, whisperMessage.getTimeout());
	}

	@Override
	public MobDecision act(long now) {
		return MobDecision.UNDECIDED;
	}

	@Override
	public Mob getMob() {
		return mob;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return receiver;
	}

	@Override
	public void teleportTo(PositionalData positionalData) {
	}

	@Override
	public void sendPlayback(Entity entity, RecordingPoint<AnimationState> recordingPoint) {
		checkProximity((ServerEntity) entity, whisperMessage.getMessage());
	}

	protected void checkProximity(ServerEntity entity, String message) {
		if (Predicates.and(proximityPredicate, cooldownPredicate).apply(entity)) {
			mobWhisperer.whisperEntity(entity, message);
			cooldownPredicate.updateLastTime(entity);
		}
	}

	@Override
	public EnumSet<EntityInteractionCapability> getInteractionCapabilities() {
		return EnumSet.noneOf(EntityInteractionCapability.class);
	}

	@Override
	public void restartRecorder(PositionalData positionalData) {
	}
}
