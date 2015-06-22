package se.spaced.server.mob;

import com.google.common.collect.MapMaker;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.spawn.SpawnListener;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

@Singleton
public class MobController implements Runnable, SpawnListener {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final Map<Mob, MobBrain> brains = new MapMaker().concurrencyLevel(2).makeMap();
	private final TimeProvider timeProvider;
	private final MobOrderExecutor mobOrderExecutor;
	private final ListenerDispatcher<SpawnListener> listenerDispatcher;
	private final LifetimeManager lifetimeManager;
	private final CountDownLatch latch = new CountDownLatch(1);

	@Inject
	public MobController(TimeProvider timeProvider, MobOrderExecutor mobOrderExecutor, ListenerDispatcher<SpawnListener> listenerDispatcher,
							 LifetimeManager lifetimeManager) {
		this.timeProvider = timeProvider;
		this.mobOrderExecutor = mobOrderExecutor;
		this.listenerDispatcher = listenerDispatcher;
		this.lifetimeManager = lifetimeManager;
	}

	@Override
	public void run() {
		log.info("Starting mob controller");
		listenerDispatcher.addListener(this);

		lifetimeManager.start();
		try {
			lifetimeManager.waitForStart();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		latch.countDown();
		long previousTime = timeProvider.now();
		while (!lifetimeManager.isDead()) {
			long timeStamp = timeProvider.now();
			step(previousTime, timeStamp);

			previousTime = timeStamp;
			try {
				timeProvider.sleep(300);
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			}
		}
		log.info("Shutting down mob controller");
	}

	public void step(long previousTime, long timeStamp) {
		for (MobBrain brain : brains.values()) {
			if (brain.getMob().isAlive()) {
				brain.act(timeStamp);
			}
		}
		mobOrderExecutor.executeMoveMap();
	}

	public void waitFor() throws InterruptedException {
		latch.await();
	}

	@Override
	public void entitySpawned(Mob mob, MobBrain brain) {
		log.debug("EntitySpawned: {}", mob);
		brains.put(mob, brain);
	}

	@Override
	public void entityDespawned(Mob mob) {
		brains.remove(mob);
	}
}
