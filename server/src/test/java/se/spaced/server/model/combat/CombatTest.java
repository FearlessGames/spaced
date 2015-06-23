package se.spaced.server.model.combat;

import com.google.common.collect.Lists;
import org.junit.Test;
import se.fearless.common.util.ConcurrentTestHelper;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.spawn.MobTemplate;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class CombatTest extends ScenarioTestBase {

	@Test
	public void testMerge() {
		Combat combat1 = new Combat(currentActionService,
				actionScheduler,
				EntityCombatServiceImpl.COMBAT_TIMEOUT,
				smrtBroadcaster,
				entityCombatService);
		Combat combat2 = new Combat(currentActionService,
				actionScheduler,
				EntityCombatServiceImpl.COMBAT_TIMEOUT,
				smrtBroadcaster,
				entityCombatService);

		Player a = playerFactory.createPlayer("alice");
		Player b = playerFactory.createPlayer("bob");

		MobTemplate mobTemplate = new MobTemplate.Builder(uuidFactory.randomUUID(), "CarolDave").stamina(100).build();
		Mob c = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);
		Mob d = mobTemplate.createMob(timeProvider, uuidFactory.randomUUID(), randomProvider);

		combat1.addParticipant(a);
		combat1.addParticipant(c);

		tick(100);
		combat1.updateLastAction(timeProvider.now(), a);

		combat2.addParticipant(b);
		combat2.addParticipant(d);

		tick(100);
		combat1.updateLastAction(timeProvider.now(), a);


		Combat result = Combat.merge(combat1, combat2, actionScheduler, currentActionService, smrtBroadcaster);

		assertTrue(result.getParticipants().contains(a));
		assertTrue(result.getParticipants().contains(b));
		assertTrue(result.getParticipants().contains(c));
		assertTrue(result.getParticipants().contains(d));

	}

	@Test
	public void testCombatEnds() {
		Player a = playerFactory.createPlayer("alice");
		Player b = playerFactory.createPlayer("bob");
		Combat combat = new Combat(currentActionService,
				actionScheduler,
				EntityCombatServiceImpl.COMBAT_TIMEOUT,
				smrtBroadcaster,
				entityCombatService);
		combat.addParticipant(a);
		combat.addParticipant(b);

		combat.updateLastAction(timeProvider.now(), a);


		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT - 1);

		combat.updateLastAction(timeProvider.now(), a);
		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT - 1);

		combat.updateLastAction(timeProvider.now(), a);
		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT / 2);

		combat.updateLastAction(timeProvider.now(), a);
		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT / 2);

		combat.updateLastAction(timeProvider.now(), a);
		tick(EntityCombatServiceImpl.COMBAT_TIMEOUT);
	}

	@Test
	public void concurrentAddingAndRemoving() throws Exception {
		final Player a = playerFactory.createPlayer("alice");
		final Player b = playerFactory.createPlayer("bob");



		final Combat combat = new Combat(currentActionService,
				actionScheduler,
				EntityCombatServiceImpl.COMBAT_TIMEOUT,
				smrtBroadcaster,
				entityCombatService);

		combat.addParticipant(a);
		combat.addParticipant(b);
		combat.addHostility(a, b);


		final Random rand = new Random(12512);
		final List<Player> playersAlreadyInCombat = Lists.newArrayList();

		int numberOfPlayers = 1000;
		for (int i = 0; i < numberOfPlayers; i++) {
			Player player = playerFactory.createPlayer("player" + i);
			playersAlreadyInCombat.add(player);
			combat.addParticipant(player);
			if (rand.nextBoolean()) {
				combat.addHostility(player, b);
			} else {
				combat.addHostility(player, a);
			}
		}

		final List<Player> playersToAddToCombat = Lists.newArrayList();
		for (int i = numberOfPlayers; i < 2* numberOfPlayers; i++) {
			Player player = playerFactory.createPlayer("player" + i);
			playersToAddToCombat.add(player);
		}

		final ConcurrentTestHelper testHelper = new ConcurrentTestHelper(2);

		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				testHelper.reportReadyToStart();
				testHelper.awaitGoSignal();

				for (Player player : playersToAddToCombat) {
					combat.addParticipant(player);
					if (rand.nextBoolean()) {
						combat.addHostility(player, a);
					} else {
						combat.addHostility(player, b);
					}
				}
				testHelper.reportFinished();
			}
		});

		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				testHelper.reportReadyToStart();
				testHelper.awaitGoSignal();
				for (Player player : playersAlreadyInCombat) {
					combat.removeParticipant(player);
				}
				testHelper.reportFinished();
			}
		});

		t1.start();
		t2.start();
		testHelper.reportReadyToStart();
		testHelper.giveGoSignal();
		testHelper.awaitFinish();
	}
}
