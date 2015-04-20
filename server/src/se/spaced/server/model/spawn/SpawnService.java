package se.spaced.server.model.spawn;

import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;

public interface SpawnService {

	MobLifecycle getOwner(ServerEntity mob);

	void registerMob(MobLifecycle owner, Mob entity, MobBrain brain);

	void unregisterMob(MobLifecycle owner, Mob entity);

	S2CProtocol getReceiver();
}
