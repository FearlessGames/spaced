package se.spaced.client.model;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUID;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.mockachino.annotations.*;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.client.model.player.PlayerTargetingListener;
import se.spaced.client.model.player.TargetInfo;
import se.spaced.client.net.messagelisteners.EntityCacheImpl;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.CreatureType;
import se.spaced.shared.model.EntityState;
import se.spaced.shared.model.Faction;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.security.SecureRandom;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class PlayerTargetingTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());
	private final UUID playerUuid = uuidFactory.randomUUID();
	private final UUID targetUuid = uuidFactory.randomUUID();

	private PlayerTargeting playerTargeting;
	@Mock
	private PlayerTargetingListener listener;
	@Mock
	private RelationResolver relationResolver;
	private ServerConnection serverConnection;
	private ClientEntity playerEntity;
	private ClientEntity targetEntity;
	private ActiveCache<Entity, ClientEntity> entityCache;
	private ListenerDispatcher<ClientEntityListener> playerListener;

	@Before
	public void setUp() {
		setupMocks(this);

		playerListener = ListenerDispatcher.create(ClientEntityListener.class);
		EntityData playerEntityData = new EntityData(playerUuid,
				"",
				new PositionalData(),
				new AppearanceData(),
				new CreatureType(""),
				new EntityStats(timeProvider),
				new Faction(""), AnimationState.IDLE, EntityState.ALIVE);
		playerEntity = new ClientEntity(playerEntityData, playerListener);
		targetEntity = new ClientEntity(targetUuid, timeProvider);
		targetEntity.setAlive(true);

		serverConnection = MockUtil.deepMock(ServerConnection.class);

		entityCache = new EntityCacheImpl(serverConnection);
		ListenerDispatcher<PlayerTargetingListener> dispatcher = ListenerDispatcher.create(PlayerTargetingListener.class);
		dispatcher.addListener(listener);

		PlayerEntityProvider playerProvider = new PlayerEntityProvider();
		playerProvider.setPlayerEntity(playerEntity);
		playerTargeting = new PlayerTargeting(playerProvider, dispatcher, relationResolver, serverConnection,
				entityCache);
	}

	@Test
	public void shouldChangeTarget() {
		playerTargeting.setTarget(targetEntity);
		verifyOnce().on(listener).newTarget(any(TargetInfo.class));
		assertEquals(targetEntity, playerEntity.getTarget());
	}

	@Test
	public void ignoresTargetUpdateWhenSameTarget() {
		playerTargeting.setTarget(targetEntity);
		playerTargeting.setTarget(targetEntity);

		verifyOnce().on(listener).newTarget(any(TargetInfo.class));
		assertEquals(targetEntity, playerEntity.getTarget());
	}

	@Test
	public void clearsTarget() {
		playerTargeting.setTarget(targetEntity);
		playerTargeting.clearTarget();

		verifyExactly(1).on(listener).newTarget(any(TargetInfo.class));
		verifyExactly(1).on(listener).targetCleared();
		assertEquals(null, playerEntity.getTarget());
	}

	@Test
	public void shouldSetNewHover() {
		entityCache.setValue(targetEntity, targetEntity);
		stubReturn(Relation.FRIENDLY).on(relationResolver).resolveRelation(playerEntity, targetEntity);

		playerTargeting.setHover(targetUuid);

		verifyOnce().on(listener).newHover(new TargetInfo(targetUuid, Relation.FRIENDLY, false, true));
	}

	@Test
	public void ignoresHoverUpdateWhenSameHover() {
		entityCache.setValue(targetEntity, targetEntity);
		stubReturn(Relation.FRIENDLY).on(relationResolver).resolveRelation(playerEntity, targetEntity);

		playerTargeting.setHover(targetUuid);
		playerTargeting.setHover(targetUuid);

		verifyOnce().on(listener).newHover(any(TargetInfo.class));
	}

	@Test
	public void clearsHover() {
		playerTargeting.clearHover();
		verifyOnce().on(listener).hoverCleared();
	}
}
