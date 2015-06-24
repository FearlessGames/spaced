package se.spaced.client.net.messagelisteners;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.mock.MockUtil;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.annotations.Mock;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.ClientInventory;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemLookup;
import se.spaced.client.model.item.ItemTemplateServiceImpl;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.*;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ItemType;

import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.setupMocks;

public class ServerItemMessagesImplTest {
	ServerItemMessagesImpl serverItemMessages;
	@Mock
	private EventHandler eventHandler;

	private ActiveCache<SpacedItem, ClientItem> itemLookup;
	private ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateService;

	private UUIDFactory uuidFactory;

	@Before
	public void setup() {
		setupMocks(this);

		uuidFactory = new UUIDMockFactory();
		ServerConnection serverConnection = MockUtil.deepMock(ServerConnection.class);
		InventoryProvider inventoryProvider = new InventoryProvider(uuidFactory);

		itemTemplateService = new ItemTemplateServiceImpl(serverConnection);
		itemLookup = new ItemLookup(itemTemplateService);
		ActiveCache<Spell, ClientSpell> spellCache = new SpellCacheImpl(serverConnection);

		serverItemMessages = new ServerItemMessagesImpl(eventHandler, inventoryProvider, itemLookup, itemTemplateService, spellCache);
	}


	@Test
	public void itemsSentInInventoryAreStoredInClientSideCache() throws Exception {
		ItemTemplateData template = new ItemTemplateData(uuidFactory.combUUID(), "ItemA", new AppearanceData("a", "b"),
				Lists.newArrayList(ItemType.LEFT_WRIST), Sets.<ClientAuraInstance>newHashSet(), Money.ZERO, null);
		ClientItem item = new ClientItem(uuidFactory.combUUID(), template);
		Multimap<Integer, ClientItem> items = ImmutableMultimap.<Integer, ClientItem>builder().put(2, item).build();
		InventoryData inventoryData = new ClientInventory(uuidFactory.combUUID(), items);
		serverItemMessages.sendInventory(inventoryData);

		assertTrue(itemTemplateService.isKnown(template));
		assertTrue(itemLookup.isKnown(item));
	}

}
