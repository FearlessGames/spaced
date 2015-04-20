package se.spaced.server.model.aura;

import com.google.inject.Inject;
import se.spaced.messages.protocol.AuraInstance;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class AuraUpdateBroadcaster implements AuraUpdateListener {
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;

	@Inject
	public AuraUpdateBroadcaster(SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		this.smrtBroadcaster = smrtBroadcaster;
	}

	@Override
	public void gainedAura(ServerEntity receiver, AuraInstance aura, ServerEntity performer) {
		smrtBroadcaster.create().toCombat(performer, receiver).toArea(receiver).send().
				combat().gainedAura(receiver, aura);
		smrtBroadcaster.create().toCombat(performer, receiver).toArea(receiver).send().
				entity().updateStats(receiver, receiver.getBaseStats());
	}

	@Override
	public void lostAura(ServerEntity entity, AuraInstance aura) {
		smrtBroadcaster.create().toCombat(entity, entity).toArea(entity).send().
				combat().lostAura(entity, aura);

		smrtBroadcaster.create().toCombat(entity, entity).toArea(entity).send().
				entity().updateStats(entity, entity.getBaseStats());
	}
}
