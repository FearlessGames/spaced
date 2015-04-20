package se.spaced.server.net.listeners.auth;

import org.apache.mina.core.session.IoSession;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.model.PersistedPositionalData;
import se.spaced.server.model.Player;
import se.spaced.server.model.combat.CombatRepository;
import se.spaced.server.model.combat.CombatRepositoryImpl;
import se.spaced.server.model.entity.EntityService;
import se.spaced.server.model.entity.EntityServiceImpl;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.entity.VisibilityService;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.ClientConnectionImpl;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.net.mina.ClientListenerFactory;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Random;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;


public class ClientChatMessagesAuthTest {
	private SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private EntityService entityService;
	private ClientConnectionImpl clientConnection1;
	private ClientConnectionImpl clientConnection2;
	private S2CProtocol player1Receiver;
	private S2CProtocol player2Receiver;
	private ClientChatMessagesAuth chat1;
	private ClientChatMessagesAuth chat2;
	private Player player1;
	private Player player2;
	private UUIDFactoryImpl uuidFactory;
	private TimeProvider timeProvider;

	@Before
	public void setUp() {
		timeProvider = new MockTimeProvider();
		uuidFactory = new UUIDFactoryImpl(new SystemTimeProvider(), new Random());
		entityService = new EntityServiceImpl(uuidFactory, ListenerDispatcher.create(EntityServiceListener.class));

		PersistedCreatureType creatureType = mock(PersistedCreatureType.class);
		PersistedPositionalData positionalData = new PersistedPositionalData();
		PersistedAppearanceData appearance = mock(PersistedAppearanceData.class);
		PersistedFaction faction = mock(PersistedFaction.class);
		PlayerMockFactory playerFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player1 = playerFactory.createPlayer("player1");
		player2 = playerFactory.createPlayer("player2");
		player1Receiver = MockUtil.deepMock(S2CProtocol.class);
		player2Receiver = MockUtil.deepMock(S2CProtocol.class);

		CombatRepository combatRepository = new CombatRepositoryImpl();
		smrtBroadcaster = new SmrtBroadcasterImpl(entityService, combatRepository, mock(VisibilityService.class));

		ClientListenerFactory clientListenerProvider = mock(ClientListenerFactory.class);
		clientConnection1 = new ClientConnectionImpl(mock(IoSession.class), uuidFactory.randomUUID(), player1Receiver, clientListenerProvider, smrtBroadcaster);
		clientConnection1.setPlayer(player1);

		clientConnection2 = new ClientConnectionImpl(mock(IoSession.class), uuidFactory.randomUUID(), player2Receiver, clientListenerProvider, smrtBroadcaster);
		clientConnection2.setPlayer(player2);

		chat1 = new ClientChatMessagesAuth(smrtBroadcaster, clientConnection1, entityService);
		chat2 = new ClientChatMessagesAuth(smrtBroadcaster, clientConnection2, entityService);

		entityService.addEntity(player1, player1Receiver);

		entityService.addEntity(player2, player2Receiver);
	}

	@Test
	public void testSay() {
		chat1.say("hej");
		verifyOnce().on(player1Receiver.chat()).playerSaid(eq(player1.getName()), eq("hej"));
		verifyOnce().on(player2Receiver.chat()).playerSaid(eq(player1.getName()), eq("hej"));

		chat2.say("läget?");
		verifyOnce().on(player1Receiver.chat()).playerSaid(eq(player2.getName()), eq("läget?"));
		verifyOnce().on(player2Receiver.chat()).playerSaid(eq(player2.getName()), eq("läget?"));
	}

	@Test
	public void testWhisper() {
		chat1.whisper(player2.getName(), "hej");
		verifyOnce().on(player1Receiver.chat()).whisperTo(eq(player2.getName()), eq("hej"));
		verifyOnce().on(player2Receiver.chat()).whisperFrom(eq(player1.getName()), eq("hej"));

		chat2.whisper(player1.getName(), "läget?");
		verifyOnce().on(player1Receiver.chat()).whisperFrom(eq(player2.getName()), eq("läget?"));
		verifyOnce().on(player2Receiver.chat()).whisperTo(eq(player1.getName()), eq("läget?"));
	}
}
