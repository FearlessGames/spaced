package se.spaced.server.model.items;

import com.google.common.collect.Iterables;
import se.spaced.messages.protocol.SpacedItem;

import static org.junit.Assert.assertEquals;

public class InventoryAssertUtils {
	private InventoryAssertUtils() {
	}

	public static int countItemsInInventory(Inventory inventory) {
		return inventory.getItemMap().size();
	}


	public static void assertItemCountInInventory(int expectedCount, Inventory inventory) {
		assertEquals("Incorrect number of items in invenory", expectedCount, countItemsInInventory(inventory));
	}


	public static SpacedItem firstItem(Inventory inventory, int pos) {
		return Iterables.getFirst(inventory.getItemMap().get(pos), null);
	}

}
