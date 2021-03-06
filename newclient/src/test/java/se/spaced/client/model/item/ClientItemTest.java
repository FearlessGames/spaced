package se.spaced.client.model.item;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.SystemTimeProvider;
import se.fearless.common.uuid.UUID;
import se.fearless.common.uuid.UUIDFactory;
import se.fearless.common.uuid.UUIDFactoryImpl;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ItemType;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;


public class ClientItemTest {
	private UUIDFactory uuidFactory;

	@Before
	public void setup() {
		uuidFactory = new UUIDFactoryImpl(new SystemTimeProvider(), new Random());
	}

	@Test
	public void testGetItemTypes() {
		UUID itemId = uuidFactory.randomUUID();
		Set<ItemType> itemTypes = new HashSet<>();
		itemTypes.add(ItemType.CONSUMABLE);
		itemTypes.add(ItemType.TROUSERS);

		ClientItem clientItem = new ClientItem(itemId,
				new ItemTemplateData(uuidFactory.randomUUID(),
						"item",
						new AppearanceData("model", ""),
						itemTypes,
						new HashSet<>(),
						Money.ZERO, null));
		Set<ItemType> clientItemTypes = clientItem.getItemTypes();
		assertEquals(clientItemTypes.size(), itemTypes.size());
		assertTrue(clientItemTypes.contains(ItemType.CONSUMABLE));
		assertTrue(clientItemTypes.contains(ItemType.TROUSERS));
	}

	@Test
	public void testIsOfType() {
		UUID itemId = uuidFactory.randomUUID();
		Set<ItemType> itemTypes = new HashSet<ItemType>();
		itemTypes.add(ItemType.CONSUMABLE);
		itemTypes.add(ItemType.TROUSERS);

		ClientItem clientItem = new ClientItem(itemId,
				new ItemTemplateData(uuidFactory.randomUUID(),
						"item",
						new AppearanceData("model", ""),
						itemTypes,
						new HashSet<>(),
						Money.ZERO, null));
		assertTrue(clientItem.isOfType(ItemType.CONSUMABLE));
		assertTrue(clientItem.isOfType(ItemType.TROUSERS));
		assertFalse(clientItem.isOfType(ItemType.GLOVES));
	}


}
