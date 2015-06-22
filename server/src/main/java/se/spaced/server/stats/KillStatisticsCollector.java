package se.spaced.server.stats;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CEmptyReceiver;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.spawn.EntityTemplate;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.interfaces.KillEntryDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;
import se.spaced.shared.model.PositionalData;

public class KillStatisticsCollector extends S2CEmptyReceiver {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final KillEntryDao killEntryDao;

	@Inject
	public KillStatisticsCollector(SmrtBroadcaster<S2CProtocol> broadcaster, KillEntryDao killEntryDao) {
		this.killEntryDao = killEntryDao;
		broadcaster.addSpy(this);
	}

	@Override
	@AutoTransaction
	public void entityWasKilled(Entity attacker, Entity target) {
		//log.debug("Entity {} was killed by {}", target, attacker);
		ServerEntity killer = (ServerEntity) attacker;
		ServerEntity victim = (ServerEntity) target;
		EntityTemplate killerTemplate = killer.getTemplate();
		EntityTemplate victimTemplate = victim.getTemplate();
		KillEntry entry = killEntryDao.findByParticipants(killerTemplate, victimTemplate);
		if (entry == null) {
			entry = new KillEntry(killerTemplate, victimTemplate);
		}
		entry.increaseKillCount();
		killEntryDao.persist(entry);
	}

	@Override
	public void restartRecorder(PositionalData positionalData) {
	}
}
