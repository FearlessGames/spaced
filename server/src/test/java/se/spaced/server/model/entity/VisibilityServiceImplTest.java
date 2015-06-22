package se.spaced.server.model.entity;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.ConcurrentTestHelper;
import se.mockachino.*;
import se.mockachino.matchers.*;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.playback.MovementPoint;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class VisibilityServiceImplTest extends ScenarioTestBase {
	private SpacedRotation rotation = SpacedRotation.IDENTITY;
	private PlayerMockFactory playerMockFactory;
	private Player player1;
	private Player player2;
	private S2CProtocol player1receiver;
	private S2CProtocol player2receiver;

	@Before
	public void setup() {
		playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).
				appearanceData(new PersistedAppearanceData("notInitialized", "foo")).
				creatureType(new PersistedCreatureType(uuidFactory.combUUID(), "humanoid")).
				faction(new PersistedFaction(uuidFactory.combUUID(), "faction")).
				genderGenerator(PlayerMockFactory.GenderGenerator.RANDOM).
				startingPosition(new SpacedVector3(0, 0, 0)).
				build();
		player1 = playerMockFactory.createPlayer("alice");
		player2 = playerMockFactory.createPlayer("bob");
		player1receiver = MockUtil.deepMock(S2CProtocol.class);
		player2receiver = MockUtil.deepMock(S2CProtocol.class);
	}

	@Test
	public void testWalkWithinRangeDoesNotIssueManyEntityAppeared() {

		entityService.addEntity(player1, player1receiver);
		entityService.addEntity(player2, player2receiver);

		movementService.moveAndRotateEntity(player1, new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, SpacedVector3.ZERO, rotation));
		movementService.moveAndRotateEntity(player1,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(1, 0, 0), rotation));
		movementService.moveAndRotateEntity(player1,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(1, 1, 0), rotation));

		Mockachino.verifyOnce().on(player2receiver.entity()).entityAppeared(player1,
				player1.createEntityData(),
				any(Map.class));
		Mockachino.verifyNever().on(player1receiver.entity()).entityAppeared(player1,
				player1.createEntityData(),
				any(Map.class));
		Mockachino.verifyNever().on(player2receiver.entity()).entityDisappeared(player1);
	}

	@Test
	public void testWalkOutOfRange() {
		entityService.addEntity(player1, player1receiver);
		entityService.addEntity(player2, player2receiver);

		Mockachino.verifyOnce().on(player1receiver.entity()).entityAppeared(player2,
				player2.createEntityData(),
				any(Map.class));
		Mockachino.verifyOnce().on(player2receiver.entity()).entityAppeared(player1,
				player1.createEntityData(),
				any(Map.class));

		movementService.moveAndRotateEntity(player1,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 0, 0), rotation));
		movementService.moveAndRotateEntity(player2,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 0, 0), rotation));

		// Same as before
		Mockachino.verifyOnce().on(player1receiver.entity()).entityAppeared(player2,
				player2.createEntityData(),
				any(Map.class));
		Mockachino.verifyOnce().on(player2receiver.entity()).entityAppeared(player1,
				player1.createEntityData(),
				any(Map.class));

		movementService.moveAndRotateEntity(player1,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(2000, 0, 0), rotation));

		Mockachino.verifyOnce().on(player1receiver.entity()).entityAppeared(player2,
				player2.createEntityData(),
				any(Map.class));
		Mockachino.verifyOnce().on(player2receiver.entity()).entityAppeared(player1,
				player1.createEntityData(),
				any(Map.class));

		Mockachino.verifyOnce().on(player1receiver.entity()).entityDisappeared(player2);
		Mockachino.verifyOnce().on(player2receiver.entity()).entityDisappeared(player1);

		// Reset and move closer
		Mockachino.getData(player1receiver.entity()).resetCalls();
		Mockachino.getData(player2receiver.entity()).resetCalls();

		// Move back in range
		movementService.moveAndRotateEntity(player1,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(100, 0, 0), rotation));
		Mockachino.verifyOnce().on(player1receiver.entity()).entityAppeared(Matchers.any(Entity.class),
				Matchers.any(EntityData.class),
				any(Map.class));
		Mockachino.verifyOnce().on(player1receiver.entity()).entityAppeared(player2,
				player2.createEntityData(),
				any(Map.class));
		Mockachino.verifyOnce().on(player2receiver.entity()).entityAppeared(player1,
				player1.createEntityData(),
				any(Map.class));
	}

	@Test
	public void spawnLotsInsideRange() throws Exception {
		entityService.addEntity(player1, player1receiver);
		entityService.addEntity(player2, player2receiver);

		final ConcurrentTestHelper testHelper = new ConcurrentTestHelper(150);

		final List<S2CProtocol> receivers = Lists.newArrayListWithCapacity(testHelper.getNumberOfThreads());
		for (int i = 0; i < testHelper.getNumberOfThreads(); i++) {
			final int id = i;
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					try {
						ServerEntity entity = playerMockFactory.createPlayer("player" + id);
						S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
						receivers.add(receiver);
						testHelper.reportReadyToStart();

						testHelper.awaitGoSignal();

						entityService.addEntity(entity, receiver);
					} finally {
						testHelper.reportFinished();
					}
				}
			};
			Thread thread = new Thread(runner);
			thread.start();
		}
		testHelper.awaitReadyForStart();
		testHelper.giveGoSignal();
		testHelper.awaitFinish();

		verifyExactly(testHelper.getNumberOfThreads() + 1).on(player1receiver.entity()).entityAppeared(any(Entity.class),
				any(EntityData.class),
				any(Map.class));
		verifyExactly(testHelper.getNumberOfThreads() + 1).on(player2receiver.entity()).entityAppeared(any(Entity.class),
				any(EntityData.class),
				any(Map.class));
		for (S2CProtocol receiver : receivers) {
			verifyExactly(testHelper.getNumberOfThreads() + 1).on(receiver.entity()).entityAppeared(any(Entity.class),
					any(EntityData.class),
					any(Map.class));
		}
	}

	@Test
	public void queryWhileAdding() throws Exception {
		entityService.addEntity(player1, player1receiver);
		entityService.addEntity(player2, player2receiver);

		final ConcurrentTestHelper testHelper = new ConcurrentTestHelper(150);

		final List<S2CProtocol> receivers = Lists.newArrayListWithCapacity(testHelper.getNumberOfThreads());
		for (int i = 0; i < testHelper.getNumberOfThreads(); i++) {
			final int id = i;
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					try {
						ServerEntity entity = playerMockFactory.createPlayer("player" + id);
						S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
						receivers.add(receiver);
						testHelper.reportReadyToStart();

						testHelper.awaitGoSignal();

						entityService.addEntity(entity, receiver);
						visibilityService.updateEntityPosition(entity);
						visibilityService.invokeForNearby(entity, new Job<Collection<ServerEntity>>() {
							@Override
							public void run(Collection<ServerEntity> value) {
							}
						});
					} finally {
						testHelper.reportFinished();
					}
				}
			};
			Thread thread = new Thread(runner);
			thread.start();
		}
		testHelper.awaitReadyForStart();
		testHelper.giveGoSignal();
		testHelper.awaitFinish();

		verifyExactly(testHelper.getNumberOfThreads() + 1).on(player1receiver.entity()).entityAppeared(any(Entity.class),
				any(EntityData.class),
				any(Map.class));
		verifyExactly(testHelper.getNumberOfThreads() + 1).on(player2receiver.entity()).entityAppeared(any(Entity.class),
				any(EntityData.class),
				any(Map.class));
		for (S2CProtocol receiver : receivers) {
			verifyExactly(testHelper.getNumberOfThreads() + 1).on(receiver.entity()).entityAppeared(any(Entity.class),
					any(EntityData.class),
					any(Map.class));
		}
	}

	@Test
	public void spawnLotsInsideRangeAndLogOut() throws Exception {
		entityService.addEntity(player1, player1receiver);
		entityService.addEntity(player2, player2receiver);

		final ConcurrentTestHelper testHelper = new ConcurrentTestHelper(150);

		final List<S2CProtocol> receivers = Lists.newArrayListWithCapacity(testHelper.getNumberOfThreads());
		for (int i = 0; i < testHelper.getNumberOfThreads(); i++) {
			final int id = i;
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					try {
						ServerEntity entity = playerMockFactory.createPlayer("player" + id);
						S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
						receivers.add(receiver);
						testHelper.reportReadyToStart();

						testHelper.awaitGoSignal();

						entityService.addEntity(entity, receiver);
						entityService.removeEntity(entity);
					} finally {
						testHelper.reportFinished();
					}
				}
			};
			Thread thread = new Thread(runner);
			thread.start();
		}
		testHelper.awaitReadyForStart();
		testHelper.giveGoSignal();
		testHelper.awaitFinish();

		verifyExactly(testHelper.getNumberOfThreads() + 1).on(player1receiver.entity()).entityAppeared(any(Entity.class),
				any(EntityData.class),
				any(Map.class));
		verifyExactly(testHelper.getNumberOfThreads() + 1).on(player2receiver.entity()).entityAppeared(any(Entity.class),
				any(EntityData.class),
				any(Map.class));

		verifyExactly(testHelper.getNumberOfThreads()).on(player1receiver.entity()).entityDisappeared(any(Entity.class));
		verifyExactly(testHelper.getNumberOfThreads()).on(player2receiver.entity()).entityDisappeared(any(Entity.class));

	}

}
