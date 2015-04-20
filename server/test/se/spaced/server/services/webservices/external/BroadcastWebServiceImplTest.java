package se.spaced.server.services.webservices.external;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.shared.util.ListenerDispatcher;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;

public class BroadcastWebServiceImplTest {

	private BroadcastWebService broadcastWebService;
	private SmrtBroadcaster<S2CProtocol> broadcaster;
	private EntityService entityService;
	private UUIDFactory uuidFactory;
	private PlayerMockFactory playerMockFactory;

	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();
		TimeProvider timeProvider = new MockTimeProvider();
		playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		entityService = new EntityServiceImpl(uuidFactory, ListenerDispatcher.create(EntityServiceListener.class));
		CombatRepository combatRepo = mock(CombatRepository.class);
		EntityTargetService targetService = mock(EntityTargetService.class);
		broadcaster = new SmrtBroadcasterImpl(entityService, combatRepo, mock(VisibilityService.class));
		broadcastWebService = new BroadcastWebServiceImpl(broadcaster, entityService);
	}

	@Test
	public void sendPersonalMessage() throws Exception {
		ServerEntity entity = playerMockFactory.createPlayer("Player A");
		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entity, receiver);

		String message = "All your base are belong to us";
		UUID pk = entity.getPk();
		broadcastWebService.sendMessage(pk.toString(), message);

		verifyOnce().on(receiver.chat()).systemMessage(message);
	}

	@Test
	public void onlySendToCorrectPlayer() throws Exception {
		ServerEntity entityA = playerMockFactory.createPlayer("Player A");
		S2CProtocol receiverA = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entityA, receiverA);

		ServerEntity entityB = playerMockFactory.createPlayer("Player B");
		S2CProtocol receiverB = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entityB, receiverB);

		ServerEntity entityC = playerMockFactory.createPlayer("Player C");
		S2CProtocol receiverC = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entityC, receiverC);

		String message = "All your base are belong to us";
		UUID pk = entityA.getPk();
		BroadcastResultDTO resultDTO = broadcastWebService.sendMessage(pk.toString(), message);

		assertTrue(resultDTO.isSuccess());

		verifyOnce().on(receiverA.chat()).systemMessage(message);
		verifyNever().on(receiverB.chat()).systemMessage(message);
		verifyNever().on(receiverC.chat()).systemMessage(message);
	}

	@Test
	public void sendPersonalUnknownUuid() throws Exception {
		ServerEntity entity = playerMockFactory.createPlayer("Player A");
		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entity, receiver);

		String message = "All your base are belong to us";
		UUID pk = uuidFactory.randomUUID();
		BroadcastResultDTO resultDTO = broadcastWebService.sendMessage(pk.toString(), message);

		assertFalse(resultDTO.isSuccess());


		verifyNever().on(receiver.chat()).systemMessage(message);
	}

	@Test
	public void sendPersonalBadUuui() throws Exception {
		ServerEntity entity = playerMockFactory.createPlayer("Player A");
		S2CProtocol receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entity, receiver);

		String message = "All your base are belong to us";

		BroadcastResultDTO resultDTO = broadcastWebService.sendMessage("This is not a uuid", message);
		assertFalse(resultDTO.isSuccess());

		verifyNever().on(receiver.chat()).systemMessage(message);
	}

	@Test
	public void sendGlobal() throws Exception {
		ServerEntity entityA = playerMockFactory.createPlayer("Player A");
		S2CProtocol receiverA = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entityA, receiverA);

		ServerEntity entityB = playerMockFactory.createPlayer("Player B");
		S2CProtocol receiverB = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entityB, receiverB);

		ServerEntity entityC = playerMockFactory.createPlayer("Player C");
		S2CProtocol receiverC = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(entityC, receiverC);

		String message = "All your base are belong to us";
		BroadcastResultDTO resultDTO = broadcastWebService.sendGlobalMessage(message);

		assertTrue(resultDTO.isSuccess());

		verifyOnce().on(receiverA.chat()).systemMessage(message);
		verifyOnce().on(receiverB.chat()).systemMessage(message);
		verifyOnce().on(receiverC.chat()).systemMessage(message);
	}
}
