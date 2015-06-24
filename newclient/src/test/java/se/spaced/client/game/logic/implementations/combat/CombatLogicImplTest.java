package se.spaced.client.game.logic.implementations.combat;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.model.ClientAuraService;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.cooldown.ClientCooldownService;
import se.spaced.client.model.listener.AbilityModelListener;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.UserCharacterListener;
import se.spaced.client.net.messagelisteners.EntityCacheImpl;
import se.spaced.client.net.messagelisteners.ServerCombatMessagesImpl;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.c2s.C2SProtocol;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.stubReturn;


public class CombatLogicImplTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());

	private UserCharacter player;
	private ClientEntity mob;
	private EventHandler eventHandler;

	ListenerDispatcher<UserCharacterListener> userDispatcher = ListenerDispatcher.create(UserCharacterListener.class);
	ListenerDispatcher<ClientEntityListener> entityDispatcher = ListenerDispatcher.create(ClientEntityListener.class);
	private ServerConnection serverConnection;
	private C2SProtocol receiver;

	private ServerCombatMessagesImpl serverCombatMessages;
	private ActiveCache<Entity, ClientEntity> entityCache;
	private ClientCooldownService cooldownService;

	@Before
	public void setUp() {
		serverConnection = mock(ServerConnection.class);
		entityCache = new EntityCacheImpl(serverConnection);

		EntityData data = new EntityData(uuidFactory.randomUUID(), "foobar",
				new PositionalData(), new AppearanceData("humanFemale", "humanFemaleFace"),
				new CreatureType("humanoid"),
				new EntityStats(timeProvider, new StatData(20, 10, 0.51, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)),
				new Faction(uuidFactory.randomUUID(), "noFaction"), AnimationState.IDLE, EntityState.ALIVE);
		player = new UserCharacter(userDispatcher);
		ClientEntity entity = new ClientEntity(data, entityDispatcher);
		entityCache.setValue(entity, entity);
		player.setControlledEntity(entity);

		receiver = mock(C2SProtocol.class);
		stubReturn(receiver).on(serverConnection).getReceiver();

		mob = new ClientEntity(new EntityData(uuidFactory.randomUUID(), "troggan",
				new PositionalData(), new AppearanceData("bigHonkinDragon", "dragonFace"),
				new CreatureType("dragonkin"),
				new EntityStats(timeProvider, new StatData(200, 10, 5.3, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)),
				new Faction(uuidFactory.randomUUID(), "noFaction"), AnimationState.IDLE, EntityState.ALIVE), entityDispatcher);
		entityCache.setValue(mob, mob);

		eventHandler = mock(EventHandler.class);
		ListenerDispatcher<AbilityModelListener> dispatcher = ListenerDispatcher.create(AbilityModelListener.class);
		serverCombatMessages = new ServerCombatMessagesImpl(player, eventHandler, dispatcher, entityCache, null,
				cooldownService, timeProvider, mock(ClientAuraService.class));

		entityCache.setValue(player.getUserControlledEntity(), player.getUserControlledEntity());
		entityCache.setValue(mob, mob);
	}

	@Test
	public void attackMessageFromServer() {
		double hp = mob.getBaseStats().getCurrentHealth().getValue();

		serverCombatMessages.entityDamaged(player.getUserControlledEntity(),
				mob,
				10,
				(int) (hp - 10),
				"Nuke",
				MagicSchool.FIRE);
//		ArgumentCaptor<SpacedEntity> argument = ArgumentCaptor.forClass(SpacedEntity.class);
//		assertTrue(player.isUserControlledEntity(argument.getValue()));

		assertEquals(hp - 10, mob.getBaseStats().getCurrentHealth().getValue(), 0.000001);
	}
}
