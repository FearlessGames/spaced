package se.spaced.client.model;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.util.MockTimeProvider;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.client.net.messagelisteners.EntityCacheImpl;
import se.spaced.messages.protocol.Entity;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.model.*;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.model.stats.StatData;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.security.SecureRandom;

import static org.junit.Assert.*;

public class UnitIdResolverTest {
	private final MockTimeProvider timeProvider = new MockTimeProvider();
	private final UUIDFactory uuidFactory = new UUIDFactoryImpl(timeProvider, new SecureRandom());

	private UnitIdResolver resolver;
	private PlayerEntityProvider playerProvider;
	private ClientEntity entity;
	private ClientEntity playerEntity;

	private ListenerDispatcher<ClientEntityListener> entityDispatcher = ListenerDispatcher.create(ClientEntityListener.class);
	private ActiveCache<Entity, ClientEntity> entityCache;

	@Before
	public void setup() {
		entityCache = new EntityCacheImpl(null);
		EntityData data = new EntityData(uuidFactory.randomUUID(), "foobar", new PositionalData(), new AppearanceData("humanMale", "dudeFace"),
				new CreatureType("humanoid"), new EntityStats(timeProvider, new StatData(10, 10, 0, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)), new Faction(uuidFactory.randomUUID(), "noFaction"),
				AnimationState.IDLE,
				EntityState.ALIVE);

		playerEntity = new ClientEntity(data, entityDispatcher);
		entityCache.setValue(playerEntity, playerEntity);

		playerProvider = new PlayerEntityProvider();
		playerProvider.setPlayerEntity(playerEntity);

		entity = new ClientEntity(new EntityData(uuidFactory.randomUUID(), "kalle", new PositionalData(),
				new AppearanceData("smallRobot", "roboFace"), new CreatureType("mechanical"),
				new EntityStats(timeProvider, new StatData(8, 10, 0, 1.0, EntityStats.IN_COMBAT_COOLRATE, 0.0)), new Faction(uuidFactory.randomUUID(), "noFaction"),
				AnimationState.IDLE, EntityState.ALIVE), entityDispatcher);
		entityCache.setValue(entity, entity);
		resolver = new UnitIdResolver(entityCache, playerProvider);
	}

	@Test
	public void getByName() {
		ClientEntity result = resolver.resolveEntity("kalle");
		assertSame(entity, result);
	}

	@Test
	public void getByNameNoAvailable() {
		ClientEntity result = resolver.resolveEntity("slack");
		assertNull(result);
	}

	@Test
	public void getByUnitIdPlayer() {
		ClientEntity result = resolver.resolveEntity("PLAYER");
		assertSame(playerProvider.get(), result);
	}

	@Test
	public void getByUnitIdNone() {
		ClientEntity result = resolver.resolveEntity("NONE");
		assertNull(result);
	}


	@Test
	public void getTarget() {
		ClientEntity result = resolver.resolveEntity("TARGET");
		assertNull(result);

		playerEntity.setTarget(entity);

		result = resolver.resolveEntity("TARGET");
		assertSame(entity, result);
	}

	@Test
	public void getTargetTarget() {
		ClientEntity result = resolver.resolveEntity("TARGETTARGET");
		assertNull(result);

		playerEntity.setTarget(entity);
		entity.setTarget(entity);
		result = resolver.resolveEntity("TARGETTARGET");
		assertNotNull(result);
		assertSame(entity, result);
	}

	@Test
	public void getTargetPlayer() {
		ClientEntity result = resolver.resolveEntity("TARGETPLAYER");
		assertNull(result);
	}

	@Test
	public void getTargetTargetPet() {
		playerEntity.setTarget(entity);
		entity.setTarget(entity);
		ClientEntity result = resolver.resolveEntity("TARGETTARGETPET");
		assertNull(result);
	}


}

