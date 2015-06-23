package se.spaced.server.model.combat;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.util.ConcurrentTestHelper;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.player.PlayerMockFactory;

import java.util.ConcurrentModificationException;
import java.util.List;

import static org.junit.Assert.*;
import static se.mockachino.Mockachino.*;

public class EntityTargetServiceImplTest extends ScenarioTestBase {
	private ServerEntity targetingEntity;
	private ServerEntity target;
	private ServerEntity observingEntity;
	private S2CProtocol targetingReceiver;
	private S2CProtocol targetReceiver;
	private S2CProtocol observerReceiver;

	@Before
	public void setup() {
		targetingReceiver = MockUtil.deepMock(S2CProtocol.class);
		targetReceiver = MockUtil.deepMock(S2CProtocol.class);
		observerReceiver = MockUtil.deepMock(S2CProtocol.class);
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).startingPosition(new SpacedVector3(10, 20, 30)).build();
		targetingEntity = playerMockFactory.createPlayer("Targeting");
		target = playerMockFactory.createPlayer("Target");

		observingEntity = playerMockFactory.createPlayer("Observer");

		entityService.addEntity(targetingEntity, targetingReceiver);
		entityService.addEntity(target, targetReceiver);
		entityService.addEntity(observingEntity, observerReceiver);
	}

	@Test
	public void setTarget() {
		entityTargetService.setTarget(targetingEntity, target);

		verifyOnce().on(targetingReceiver.entity()).changedTarget(target);
		verifyOnce().on(targetReceiver.entity()).entityChangedTarget(targetingEntity, target);

		assertEquals(target, entityTargetService.getCurrentTarget(targetingEntity));
	}

	@Test
	public void everyoneGetsUpdates() {
		entityTargetService.setTarget(observingEntity, targetingEntity);
		entityTargetService.setTarget(targetingEntity, target);

		verifyOnce().on(observerReceiver.entity()).entityChangedTarget(targetingEntity, target);

		entityTargetService.clearTarget(targetingEntity);
		verifyOnce().on(observerReceiver.entity()).entityClearedTarget(targetingEntity);

		entityTargetService.clearTarget(observingEntity);

		getData(observerReceiver.entity()).resetCalls();
		entityTargetService.setTarget(targetingEntity, target);
		verifyOnce().on(observerReceiver.entity()).entityChangedTarget(targetingEntity, target);
	}

	@Test
	public void removedEntityIsRemoved() {
		entityTargetService.setTarget(observingEntity, targetingEntity);
		entityTargetService.setTarget(targetingEntity, target);
		entityService.removeEntity(targetingEntity);

		assertEquals(null, entityTargetService.getCurrentTarget(targetingEntity));
		assertTrue(entityTargetService.getEntitiesTargeting(target).isEmpty());
		verifyOnce().on(observerReceiver.entity()).entityClearedTarget(targetingEntity);
		verifyOnce().on(observerReceiver.entity()).clearedTarget();
	}

	@Test
	public void removedEntityWithoutTargetIsRemoved() {
		entityService.removeEntity(targetingEntity);

		assertEquals(null, entityTargetService.getCurrentTarget(targetingEntity));
		assertTrue(entityTargetService.getEntitiesTargeting(target).isEmpty());
	}



	@Test
	public void testClearTarget() throws Exception {
		entityTargetService.setTarget(targetingEntity, target);
		entityTargetService.clearTarget(targetingEntity);

		verifyOnce().on(targetingReceiver.entity()).clearedTarget();
		verifyOnce().on(targetReceiver.entity()).entityClearedTarget(targetingEntity);
	}

	@Test
	public void noNotificationOnRetarget() {
		entityTargetService.setTarget(targetingEntity, target);

		getData(targetingReceiver.entity()).resetCalls();
		getData(targetReceiver.entity()).resetCalls();

		entityTargetService.setTarget(targetingEntity, target);

		verifyNever().on(targetingReceiver.entity()).entityChangedTarget(targetingEntity, target);
		verifyNever().on(targetReceiver.entity()).entityChangedTarget(targetingEntity, target);
	}

	@Test
	public void noNotificationOnClearTargetTwice() {
		entityTargetService.setTarget(targetingEntity, target);
		entityTargetService.clearTarget(targetingEntity);


		getData(targetingReceiver.entity()).resetCalls();
		getData(targetReceiver.entity()).resetCalls();

		entityTargetService.clearTarget(targetingEntity);

		verifyNever().on(targetingReceiver.entity()).clearedTarget();
		verifyNever().on(targetReceiver.entity()).entityClearedTarget(targetingEntity);

	}

	@Test
	public void concurrentClearAndSetTarget() throws Exception {
		int nrOfPlayers = 60;
		final ConcurrentTestHelper concurrentTestHelper = new ConcurrentTestHelper(nrOfPlayers);
		final List<Player> players = Lists.newArrayList();
		
		for (int i = 0; i < nrOfPlayers; i++) {
			final Player p = playerFactory.createPlayer("Player" + i);
			players.add(p);
			S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
			entityService.addEntity(p, receiver);

			Runnable task = new Runnable() {
				@Override
				public void run() {
					concurrentTestHelper.reportReadyToStart();
					concurrentTestHelper.awaitGoSignal();
					try {
						for (int times = 0; times < 8; times++) {
							entityTargetService.setTarget(p, target);
							entityTargetService.clearTarget(p);
						}
					} catch (ConcurrentModificationException e) {
						fail(e.toString());
					}finally {
						concurrentTestHelper.reportFinished();
					}

				}
			};
			Thread thread = new Thread(task);
			thread.start();
		}
		concurrentTestHelper.awaitReadyForStart();
		concurrentTestHelper.giveGoSignal();
		entityService.removeEntity(target);
		concurrentTestHelper.awaitFinish();
	}
}
