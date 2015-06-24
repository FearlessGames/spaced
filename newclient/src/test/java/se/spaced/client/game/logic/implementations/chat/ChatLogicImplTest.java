package se.spaced.client.game.logic.implementations.chat;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.util.MockTimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.UnitIdResolver;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.security.SecureRandom;

import static se.mockachino.Mockachino.*;


public class ChatLogicImplTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());

	private ChatLogicListener view;
	private UnitIdResolver unitIdResolver;
	private ChatLogicImpl chatLogic;
	private ListenerDispatcher<ChatLogicListener> dispatcher;
	private UserCharacter self;
	private ClientEntity player;

	ListenerDispatcher<UserCharacterListener> userDispatcher = ListenerDispatcher.create(UserCharacterListener.class);
	private final ListenerDispatcher<ClientEntityListener> entityDispatcher = ListenerDispatcher.create(ClientEntityListener.class);
	private ServerConnection serverConnection;
	private C2SProtocol receiver;

	@Before
	public void setUp() {
		view = mock(ChatLogicListener.class);
		unitIdResolver = mock(UnitIdResolver.class);

		dispatcher = ListenerDispatcher.create(ChatLogicListener.class);
		serverConnection = mock(ServerConnection.class);
		receiver = MockUtil.deepMock(C2SProtocol.class);
		stubReturn(receiver).on(serverConnection).getReceiver();
		chatLogic = new ChatLogicImpl(dispatcher, serverConnection);
		dispatcher.addListener(view);

		EntityData data = new EntityData(uuidFactory.randomUUID(), "kaka",
				new PositionalData(), new AppearanceData("scaryShit", "scaryFace"),
				new CreatureType("god"), new EntityStats(timeProvider, new StatData(20, 10, 0.2, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)), new Faction(uuidFactory.randomUUID(), "noFaction"),
				AnimationState.IDLE,
				EntityState.ALIVE);
		ClientEntity entity = new ClientEntity(data, entityDispatcher);
		self = new UserCharacter(userDispatcher);
		self.setControlledEntity(entity);

		player = new ClientEntity(new EntityData(uuidFactory.randomUUID(), "krka",
				new PositionalData(), new AppearanceData("weakShit", "sillyFace"),
				new CreatureType("lulz"), new EntityStats(timeProvider, new StatData(200, 10, 3.1415626, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)), new Faction(uuidFactory.randomUUID(), "noFaction"),
				AnimationState.IDLE,
				EntityState.ALIVE), entityDispatcher);
	}

	@Test
	public void updatesViewWithPlayerSaid() {
		String player = "playerName";
		String message = "I can haz message";

		chatLogic.playerSaid(player, message);

		verifyOnce().on(view).playerSaid(player, message);
	}

	@Test
	public void updatesViewWithPlayerWhispered() {
		String message = "I can haz message";

		chatLogic.playerWhispered(player.getName(), message);

		verifyOnce().on(view).playerWhispered(player.getName(), message);
	}

	@Test
	public void sendsSayMessage() {
		String message = "Hey dude!";

		chatLogic.say(message);
		verifyOnce().on(receiver.chat()).say(message);
	}

	@Test
	public void sendsWhisperMessage() {
		String toPlayer = "krka";
		String message = "Hey dude!";

		stubReturn(player).on(unitIdResolver).resolveEntity(toPlayer);

		chatLogic.whisper(toPlayer, message);

		verifyOnce().on(receiver.chat()).whisper(toPlayer, message);
	}
}
