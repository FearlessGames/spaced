package se.spaced.client.net.messagelisteners;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.ClientEntityProxy;
import se.spaced.client.model.PlaybackService;
import se.spaced.client.model.UserCharacter;
import se.spaced.client.model.control.ClientTeleporter;
import se.spaced.client.model.item.ItemTemplateServiceImpl;
import se.spaced.client.model.listener.ClientEntityListener;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.CreatureType;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.EntityState;
import se.spaced.shared.model.Faction;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.model.stats.EntityStats;
import se.spaced.shared.network.protocol.codec.datatype.EntityData;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collections;
import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.spy;
import static se.mockachino.Mockachino.verifyOnce;
import static se.mockachino.matchers.Matchers.match;
import static se.mockachino.matchers.MatchersBase.mAny;

public class ServerEntityDataMessagesImplTest {

	private ServerEntityDataMessagesImpl messages;
	private EntityCacheImpl entityCache;
	private TimeProvider timeProvider;
	private PlaybackService playbackService;

	@Before
	public void setUp() throws Exception {
		ServerConnection serverConnection = mock(ServerConnection.class);
		entityCache = new EntityCacheImpl(serverConnection);
		timeProvider = new MockTimeProvider();
		UserCharacter userCharacter = mock(UserCharacter.class);
		ClientTeleporter clientTeleporter = mock(ClientTeleporter.class);
		ActiveCache<ItemTemplate, ItemTemplateData> itemCache = new ItemTemplateServiceImpl(serverConnection);
		EventHandler eventHandler = mock(EventHandler.class);
		playbackService = spy(new PlaybackService(entityCache, timeProvider));
		messages = new ServerEntityDataMessagesImpl(ListenerDispatcher.create(ClientEntityListener.class),
				entityCache,
				timeProvider,
				userCharacter,
				clientTeleporter,
				itemCache,
				ListenerDispatcher.create(EquipmentListener.class),
				eventHandler,
				playbackService
		);
	}

	@Test
	public void entityAppeared() throws Exception {
		UUID id = new UUID(1, 2);
		EntityData data = new EntityData(id,
				"Foo",
				new PositionalData(),
				new AppearanceData(),
				new CreatureType(new UUID(5, 8), "CreatureType"),
				new EntityStats(timeProvider),
				new Faction("The A Team"),
				AnimationState.IDLE,
				EntityState.ALIVE,
				UUID.ZERO,
				EnumSet.noneOf(EntityInteractionCapability.class));
		ClientEntityProxy entity = new ClientEntityProxy(id);
		assertFalse(entityCache.isKnown(entity));
		messages.entityAppeared(entity, data, Collections.emptyMap());

		ArgumentCatcher<ClientEntity> argumentCatcher = ArgumentCatcher.create(mAny(ClientEntity.class));
		verifyOnce().on(playbackService).add(match(argumentCatcher));
		assertEquals(id, argumentCatcher.getValue().getPk());
		assertTrue(entityCache.isKnown(entity));
	}

	@Test
	public void entityAppearedTwice() throws Exception {
		UUID id = new UUID(1, 2);
		EntityData data = new EntityData(id,
				"Foo",
				new PositionalData(),
				new AppearanceData(),
				new CreatureType(new UUID(5, 8), "CreatureType"),
				new EntityStats(timeProvider),
				new Faction("The A Team"),
				AnimationState.IDLE,
				EntityState.ALIVE,
				UUID.ZERO,
				EnumSet.noneOf(EntityInteractionCapability.class));
		ClientEntityProxy entity = new ClientEntityProxy(id);
		messages.entityAppeared(entity, data, Collections.emptyMap());
		ClientEntityProxy entity2 = new ClientEntityProxy(id);

		messages.entityAppeared(entity2, data, Collections.emptyMap());

		ArgumentCatcher<ClientEntity> argumentCatcher = ArgumentCatcher.create(mAny(ClientEntity.class));
		verifyOnce().on(playbackService).add(match(argumentCatcher));
		assertEquals(id, argumentCatcher.getValue().getPk());
		assertTrue(entityCache.isKnown(entity));

	}

}
