package se.spaced.server.model.items;

import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.ServerEntity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;


public class InventoryTest {

	private ServerEntity owner;
	private ServerItemTemplate template;

	@Before
	public void setUp() throws Exception {
		owner = mock(ServerEntity.class);
		template = mock(ServerItemTemplate.class);
		when(template.getPk()).thenReturn(new UUID(0, 1));
		when(template.getMaxStackSize()).thenReturn(1);
	}

	@Test
	public void inventoryFull() {


		PersistedInventory inventory = new PersistedInventory(owner, 2, InventoryType.BAG);

		ServerItem item1 = createMock();
		ServerItem item2 = createMock();
		ServerItem item3 = createMock();

		assertTrue(inventory.addItem(item1));
		assertTrue(inventory.addItem(item2));
		assertFalse(inventory.addItem(item3));
	}

	private ServerItem createMock() {
		ServerItem item = mock(ServerItem.class);
		when(item.getOwner()).thenReturn(owner);
		when(item.getTemplate()).thenReturn(template);
		return item;
	}

}
