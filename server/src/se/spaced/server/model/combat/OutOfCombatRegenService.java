package se.spaced.server.model.combat;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;

public class OutOfCombatRegenService extends S2CEmptyReceiver {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final SmrtBroadcaster<S2CProtocol> broadcaster;


	@Inject
	public OutOfCombatRegenService(SmrtBroadcaster<S2CProtocol> broadcaster) {
		this.broadcaster = broadcaster;
		broadcaster.addSpy(this);
	}

	@Override
	public void combatStatusChanged(Entity e, boolean isStart) {
		ServerEntity entity = (ServerEntity) e;
		if (isStart) {
			disableOutOfCombatRegen(entity);
		} else if (entity.isAlive()) {
			enableOutOfCombatRegen(entity);
		}
		broadcaster.create().toCombat(entity).toArea(entity).send().entity().updateStats(entity, entity.getBaseStats());
	}

	public void disableOutOfCombatRegen(ServerEntity entity) {
		log.debug("Zero hp regen for {}", entity.getName());
		entity.getBaseStats().getOutOfCombatHealthRegen().disable();
		entity.getBaseStats().getOutOfCombatShieldRecovery().disable();
	}

	public void enableOutOfCombatRegen(ServerEntity entity) {
		log.debug("Reset OOC hp regen for {}", entity.getName());
		entity.getBaseStats().getOutOfCombatHealthRegen().enable();
		entity.getBaseStats().getOutOfCombatShieldRecovery().enable();
	}
}
