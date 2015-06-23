package se.spaced.server;

import com.google.inject.Inject;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.MobController;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.spawn.MobSpawn;
import se.spaced.server.model.spawn.SpawnPattern;
import se.spaced.server.model.spawn.SpawnPatternTemplate;
import se.spaced.server.model.spawn.SpawnService;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.SpawnPatternTemplateDao;
import se.spaced.shared.util.random.RandomProvider;

import java.util.List;

public class ServerSetup {
	private final SpawnPatternTemplateDao spawnPatternTemplateDao;
	private final TransactionManager transactionManager;
	private final ActionScheduler scheduler;
	private final SpawnService spawnService;
	private final EntityService entityService;
	private final MobController mobController;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final TimeProvider timeProvider;

	static final Logger log = LoggerFactory.getLogger(ServerSetup.class);
	private final RandomProvider randomProvider;

	@Inject
	public ServerSetup(
			TransactionManager transactionManager,
			ActionScheduler scheduler,
			SpawnService spawnService,
			EntityService entityService,
			MobController mobController,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster,
			TimeProvider timeProvider,
			SpawnPatternTemplateDao spawnPatternTemplateDao, RandomProvider randomProvider) {
		this.transactionManager = transactionManager;
		this.scheduler = scheduler;
		this.spawnService = spawnService;
		this.entityService = entityService;
		this.mobController = mobController;
		this.smrtBroadcaster = smrtBroadcaster;
		this.timeProvider = timeProvider;
		this.spawnPatternTemplateDao = spawnPatternTemplateDao;
		this.randomProvider = randomProvider;
	}

	public void setup() throws InterruptedException {
		log.info("Waiting for mob controller to start...");
		mobController.waitFor();
		log.info("Waiting for mob controller to start... done");

		Transaction transaction = transactionManager.beginTransaction();
		log.info("Booting up spawn service...");


		List<SpawnPatternTemplate> spawnPatternTemplateList = spawnPatternTemplateDao.findAll();
		for (SpawnPatternTemplate spawnPatternTemplate : spawnPatternTemplateList) {
			SpawnPattern spawnPattern = spawnPatternTemplate.createSpawnPattern(scheduler, entityService,
					spawnService, smrtBroadcaster, timeProvider, randomProvider);
			for (MobSpawn mobSpawn : spawnPattern.getMobSpawns()) {
				mobSpawn.start(timeProvider.now());
			}
		}

		transaction.commit();
		log.info("Booting up spawn service... done");
	}
}
