package se.spaced.client.net.messagelisteners;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.util.ConcurrentTestHelper;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.CallHandler;
import se.mockachino.MethodCall;
import se.mockachino.annotations.Mock;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.client.ardor.ui.events.ItemEvents;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.*;
import se.spaced.client.model.listener.EquipmentListener;
import se.spaced.client.model.player.PlayerEntityProvider;
import se.spaced.client.model.player.PlayerEquipment;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.*;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.util.ListenerDispatcher;

import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class ServerEquipmentMessagesImplTest {
	ServerEquipmentMessagesImpl serverEquipmentMessages;
	@Mock
	private EventHandler eventHandler;
	@Mock
	private PlayerEntityProvider playerProvider;

	@Mock
	private PlayerEquipment playerEquipment;

	private ServerConnection serverConnection;
	private ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateService;
	private ActiveCache<Entity, ClientEntity> entityCache;

	private UUIDFactory uuidFactory;

	@Mock
	private EquipmentListener listener;
	private ClientEntity self;

	@Before
	public void setup() {
		setupMocks(this);

		uuidFactory = new UUIDMockFactory();
		serverConnection = MockUtil.deepMock(ServerConnection.class);
		InventoryProvider inventoryProvider = new InventoryProvider(uuidFactory);

		itemTemplateService = new ItemTemplateServiceImpl(serverConnection);
		ActiveCache<SpacedItem, ClientItem> itemLookup = new ItemLookup(itemTemplateService);
		entityCache = new EntityCacheImpl(serverConnection);

		ListenerDispatcher<EquipmentListener> dispatcher = ListenerDispatcher.create(EquipmentListener.class);
		dispatcher.addListener(listener);

		serverEquipmentMessages = new ServerEquipmentMessagesImpl(eventHandler, inventoryProvider,
				playerProvider, itemLookup, itemTemplateService, entityCache, dispatcher, playerEquipment);
		self = mock(ClientEntity.class);
		UUID selfPk = uuidFactory.combUUID();
		stubReturn(selfPk).on(self).getPk();
		stubReturn(self).on(playerProvider).get();
	}

	@Test
	public void equipCorrectItemTemplate() {
		UUID uuid = uuidFactory.combUUID();
		final ItemTemplate itemTemplate = new ItemTemplateProxy(uuid);
		final ItemTemplateData itemTemplateData = new ItemTemplateData(uuid,
				"Foo",
				null,
				Collections.<ItemType>emptySet(), new HashSet<AuraTemplate>(), Money.ZERO, null);

		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				itemTemplateService.setValue(itemTemplate, itemTemplateData);
				return null;
			}
		}).on(serverConnection.getReceiver().items()).requestItemTemplateData(uuid);
		ClientEntity entity = mock(ClientEntity.class);
		entityCache.setValue(entity, entity);
		serverEquipmentMessages.entityEquippedItem(entity, itemTemplate, ContainerType.LEGS);

		verifyOnce().on(listener).itemEquipped(entity, itemTemplateData, ContainerType.LEGS);
	}

	@Test
	public void equipItem() {
		UUID itemId = uuidFactory.combUUID();
		UUID templateId = uuidFactory.combUUID();
		final ItemTemplate itemTemplate = new ItemTemplateProxy(templateId);
		SpacedItem item = new ClientItemProxy(itemId, itemTemplate);

		final ItemTemplateData itemTemplateData = new ItemTemplateData(templateId,
				"Foo",
				null,
				Collections.<ItemType>emptySet(), new HashSet<AuraTemplate>(), Money.ZERO, null);

		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				itemTemplateService.setValue(itemTemplate, itemTemplateData);
				return null;
			}
		}).on(serverConnection.getReceiver().items()).requestItemTemplateData(templateId);

		serverEquipmentMessages.equippedItem(item, ContainerType.LEGS);

		verifyOnce().on(playerEquipment).equip(any(ClientItem.class), ContainerType.LEGS);

		ArgumentCatcher<ClientItem> itemCatcher = ArgumentCatcher.create(mAny(ClientItem.class));

		verifyOnce().on(eventHandler).fireAsynchEvent(ItemEvents.EQUIPPED_ITEM,
				self,
				match(itemCatcher),
				ContainerType.LEGS);

		assertEquals(itemId, itemCatcher.getValue().getPk());
		assertEquals("Foo", itemCatcher.getValue().getName());
	}


	@Test
	public void equippedItemNotAlreadyInInventory() throws Exception {
		final ItemTemplateData template = new ItemTemplateData(uuidFactory.combUUID(), "ItemA", new AppearanceData("a", "b"),
				Lists.newArrayList(ItemType.LEFT_WRIST), Sets.<ClientAuraInstance>newHashSet(), Money.ZERO, null);
		final SpacedItem item = new ClientItemProxy(uuidFactory.combUUID(), new ItemTemplateProxy(template.getPk()));

		final ConcurrentTestHelper helper = new ConcurrentTestHelper(1);
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				helper.reportReadyToStart();
				serverEquipmentMessages.equippedItem(item, ContainerType.LEFT_WRIST);
				helper.giveGoSignal();
			}
		});
		t1.start();
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				helper.awaitGoSignal();
				itemTemplateService.setValue(template, template);
				helper.reportFinished();
			}
		});
		t2.start();

		helper.awaitReadyForStart();
		helper.awaitFinish();


		verifyOnce().on(eventHandler).fireAsynchEvent(ItemEvents.EQUIPPED_ITEM, self, item, ContainerType.LEFT_WRIST);
	}
}
