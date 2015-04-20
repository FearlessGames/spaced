package se.spaced.server.mob.brains;

import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.model.Mob;
import se.spaced.shared.model.EntityInteractionCapability;

import java.util.EnumSet;

public interface MobBrain {

	MobDecision act(long now);

	Mob getMob();

	S2CProtocol getSmrtReceiver();

	EnumSet<EntityInteractionCapability> getInteractionCapabilities();
}
