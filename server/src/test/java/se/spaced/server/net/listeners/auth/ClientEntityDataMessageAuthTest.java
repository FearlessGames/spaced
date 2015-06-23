package se.spaced.server.net.listeners.auth;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.mockachino.matchers.Matchers;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.aura.AuraService;
import se.spaced.server.model.aura.ServerAuraInstance;
import se.spaced.server.model.combat.EntityCombatService;
import se.spaced.server.model.combat.EntityTargetService;
import se.spaced.server.model.entity.*;
import se.spaced.server.model.movement.UnstuckServiceImpl;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryEquipmentDao;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Map;

import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;

public class ClientEntityDataMessageAuthTest {
	private ClientConnection clientConnection;
	private S2CProtocol receiver;
	private EntityTargetService entityTargetService;
	private EntityService entityService;
	private EntityCombatService entityCombatService;
	private TimeProvider timeProvider;
	private AppearanceService appearanceService;
	private ListenerDispatcher<AppearanceService> appearanceListeners;
	private VisibilityService visibilityService;
	private PlayerMockFactory factory;

	@Before
	public void setup() {
		UUIDFactory uuidFactory = mock(UUIDFactory.class);
		timeProvider = new MockTimeProvider();

		entityCombatService = mock(EntityCombatService.class);
		entityService = new EntityServiceImpl(uuidFactory, ListenerDispatcher.create(EntityServiceListener.class));
		entityTargetService = mock(EntityTargetService.class);
		appearanceListeners = ListenerDispatcher.create(AppearanceService.class);
		AuraService auraService = mock(AuraService.class);
		when(auraService.getAllAuras(any(ServerEntity.class))).thenReturn(ImmutableSet.<ServerAuraInstance>of());
		appearanceService = new AppearanceServiceImpl(entityService, new InMemoryEquipmentDao(), entityTargetService,
				auraService, entityCombatService);
		visibilityService = new VisibilityServiceImpl(entityService, appearanceListeners);
		appearanceListeners.addListener(appearanceService);

		factory = new PlayerMockFactory.Builder(timeProvider,
				PlayerMockFactory.NULL_UUID_FACTORY).build();

		Player player = factory.createPlayer("Protagonist");
		UUID pk = new UUID(112233, 556677);
		player.setPk(pk);


		clientConnection = mock(ClientConnection.class);
		receiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(player, receiver);
		stubReturn(receiver).on(clientConnection).getReceiver();
		stubReturn(player).on(clientConnection).getPlayer();
	}

	@Test
	public void testWhoRequestDespawnedEntity() throws Exception {
		ClientEntityDataMessageAuth entityDataMessageAuth = new ClientEntityDataMessageAuth(clientConnection,
				entityTargetService, entityCombatService, entityService, mock(UnstuckServiceImpl.class),
				visibilityService);

		Player player = factory.createPlayer("Foo");
		UUID pk = new UUID(123456, 98765);
		player.setPk(pk);

		entityDataMessageAuth.whoRequest(player);

		verifyNever().on(receiver.entity()).entityAppeared(Matchers.any(Entity.class),
				Matchers.any(EntityData.class),
				any(Map.class));
	}

	@Test
	public void testWhoRequestEntity() throws Exception {
		UUID pk = new UUID(123456, 98765);
		Player target = factory.createPlayer("foo");
		target.setPk(pk);

		S2CProtocol targetReceiver = MockUtil.deepMock(S2CProtocol.class);
		entityService.addEntity(target, targetReceiver);

		ClientEntityDataMessageAuth entityDataMessageAuth = new ClientEntityDataMessageAuth(clientConnection,
				entityTargetService, entityCombatService, entityService, mock(UnstuckServiceImpl.class),
				visibilityService);

		entityDataMessageAuth.whoRequest(target);
		verifyOnce().on(receiver.entity()).entityAppeared(target, any(EntityData.class), any(Map.class));
	}
}
