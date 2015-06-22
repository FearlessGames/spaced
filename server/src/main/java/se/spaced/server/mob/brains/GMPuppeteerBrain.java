package se.spaced.server.mob.brains;

import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.ServerMovementMessages;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.model.Mob;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.RecordingPoint;

import java.util.EnumSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GMPuppeteerBrain implements MobBrain, ServerMovementMessages {
	private final Queue<PositionalData> teleportCommands = new ConcurrentLinkedQueue();
	private final MobBrain delegate;
	private final MobOrderExecutor orderExecutor;

	public GMPuppeteerBrain(MobBrain delegate, MobOrderExecutor orderExecutor) {
		this.delegate = delegate;
		this.orderExecutor = orderExecutor;
	}

	@Override
	public MobDecision act(long now) {
		PositionalData teleportMove = teleportCommands.poll();
		if (teleportMove == null) {
			return delegate.act(now);
		}
		orderExecutor.teleportTo(getMob(), teleportMove);
		return MobDecision.DECIDED;
	}

	@Override
	public Mob getMob() {
		return delegate.getMob();
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return new S2CMultiDispatcher(S2CAdapters.createServerMovementMessages(this), delegate.getSmrtReceiver());
	}

	@Override
	public EnumSet<EntityInteractionCapability> getInteractionCapabilities() {
		return delegate.getInteractionCapabilities();
	}

	@Override
	public void teleportTo(PositionalData positionalData) {
		teleportCommands.add(positionalData);
	}

	@Override
	public void sendPlayback(Entity entity, RecordingPoint<AnimationState> recordingPoint) {
	}

	@Override
	public void restartRecorder(PositionalData positionalData) {
	}

	public MobBrain getDelegate() {
		return delegate;
	}
}
