package se.spaced.server.model.items;

import com.google.common.collect.ImmutableCollection;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import java.util.ArrayList;
import java.util.List;

public class InventoryServiceImpl implements InventoryService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final InventoryDao inventoryDao;
	private final SmrtBroadcaster<S2CProtocol> broadcaster;

	@Inject
	public InventoryServiceImpl(InventoryDao inventoryDao, SmrtBroadcaster<S2CProtocol> broadcaster) {
		this.inventoryDao = inventoryDao;
		this.broadcaster = broadcaster;
	}


	@Override
	@AutoTransaction
	public PersistedInventory getInventory(ServerEntity player, InventoryType type) {
		return inventoryDao.findInventory(player, type);
	}

	@Override
	@AutoTransaction
	public PersistedInventory createInventory(ServerEntity entity, int capacity, InventoryType type) {
		if (getInventory(entity, type) != null) {
			throw new RuntimeException(String.format("Inventory of %s type already exists for player %s!", type, entity));
		}
		return inventoryDao.persist(new PersistedInventory(entity, capacity, type));
	}

	@Override
	@AutoTransaction
	public void add(Inventory inventory, ServerItem item) throws InventoryFullException {
		try {
			add(inventory, item, 1);  //the 1 position will be overwriten with the next free if its used already
		} catch (InventoryOutOfBoundsException e) {
			throw new RuntimeException("Adding to auto-position shouldn't be able to end up outside the inventory", e);
		}
	}


	@AutoTransaction
	protected void add(
			Inventory inventory,
			ServerItem item,
			int position) throws InventoryOutOfBoundsException, InventoryFullException {
		//Refreshing caused problem when it happend in the same transaction, so for now use the preloaded object
		//PersistedInventory persistedInventory = inventoryDao.refresh(inventory);
		PersistedInventory persistedInventory = (PersistedInventory) inventory;


		if (persistedInventory.isFull()) {
			throw new InventoryFullException();
		}

		int capacity = persistedInventory.getNrOfSlots();
		if (capacity < position || position < 1) {
			throw new InventoryOutOfBoundsException(position, 1, capacity);
		}


		if (inventoryDao.findInventory(item) != null) {
			throw new RuntimeException("Item already exists in some other inventory");
		}


		if (!persistedInventory.acceptsItemAtPosition(position, item)) {
			position = persistedInventory.getNextFreePositionForItem(item);
		}


		if (position == 0) {
			throw new InventoryFullException();
		}

		persistedInventory.addItem(item, position);

		inventoryDao.persist(persistedInventory);
		broadcaster.create().to(inventory.getOwner()).send().item().itemAdded(inventory, position, item);
	}

	@Override
	@AutoTransaction
	public void move(Inventory inventory1, int pos1, Inventory inventory2, int pos2) {
		move(inventory1, pos1, inventory1.getItemMap().get(pos1).size(), inventory2, pos2);
	}

	@Override
	public void move(Inventory inventory1, int pos1, int quantity, Inventory inventory2, int pos2) {
		PersistedInventory persistedInventory1;
		PersistedInventory persistedInventory2;

		if (inventory1.getPk().equals(inventory2.getPk())) {
			persistedInventory1 = inventoryDao.refresh(inventory1);
			persistedInventory2 = persistedInventory1;
		} else {
			persistedInventory1 = inventoryDao.refresh(inventory1);
			persistedInventory2 = inventoryDao.refresh(inventory2);
			if (!persistedInventory1.getOwner().equals(persistedInventory2.getOwner())) {
				throw new RuntimeException("Trying to move items between two diffrent owners inventory");
			}
		}


		if (isEmptyPosition(persistedInventory2, pos2)) {//if the pos2 is empty, we should always move
			moveStack(persistedInventory1, pos1, quantity, persistedInventory2, pos2);
			return;
		}


		if (isSameItemType(persistedInventory1, pos1, persistedInventory2, pos2)) { //if both pos have the same type, we should move as many as posible to stack 2
			quantity = Math.min(persistedInventory2.getFreeStackSizeOnPosition(pos2), quantity);
			moveStack(persistedInventory1, pos1, quantity, persistedInventory2, pos2);
			return;
		}


		if (persistedInventory1.getStackAtPosition(pos1).size() == quantity) { //else we swap, but only swap if we are moving the whole stack...
			swapItems(persistedInventory1, pos1, persistedInventory2, pos2);
			return;
		}

	}

	private void swapItems(PersistedInventory inventory1, int pos1, PersistedInventory inventory2, int pos2) {
		ImmutableCollection<ServerItem> stack1 = inventory1.getStackAtPosition(pos1);
		ImmutableCollection<ServerItem> stack2 = inventory2.getStackAtPosition(pos2);

		inventory1.clearStackAtPosition(pos1);
		inventory2.clearStackAtPosition(pos2);
		inventoryDao.persist(inventory1);
		inventoryDao.persist(inventory2);

		inventory1.addStackAtPosition(pos1, stack2);
		inventory2.addStackAtPosition(pos2, stack1);
		inventoryDao.persist(inventory1);
		inventoryDao.persist(inventory2);

		broadcaster.create().to(inventory1.getOwner()).send().item().itemsSwapped(inventory1, pos1, inventory2, pos2);
	}

	private void moveStack(PersistedInventory inventory1, int pos1, int quantity, PersistedInventory inventory2, int pos2) {
		ImmutableCollection<ServerItem> itemsToMove = getRangeOfItems(pos1, quantity, inventory1);
		List<SpacedItem> movedItems = new ArrayList<SpacedItem>();
		for (SpacedItem spacedItem : itemsToMove) {
			inventory1.removeItem((ServerItem) spacedItem);
			movedItems.add(spacedItem);
		}
		inventoryDao.persist(inventory1);

		inventory2.addStackAtPosition(pos2, itemsToMove);
		inventoryDao.persist(inventory2);


		broadcaster.create().to(inventory1.getOwner()).send().item().itemsMoved(inventory1, pos1, movedItems, inventory2, pos2);

	}


	private ImmutableCollection<ServerItem> getRangeOfItems(int pos1, int quantity, PersistedInventory inventory) {
		ImmutableCollection<ServerItem> items = inventory.getStackAtPosition(pos1);
		quantity = Math.min(quantity, items.size());
		return items.asList().subList(0, quantity);
	}


	private boolean isEmptyPosition(PersistedInventory inventory, int pos) {
		return inventory.getItemTemplateOnPosition(pos) == null;
	}

	private boolean isSameItemType(PersistedInventory persistedInventory1, int pos1, PersistedInventory persistedInventory2, int pos2) {
		ItemTemplate itemTemplate1 = persistedInventory1.getItemTemplateOnPosition(pos1);
		ItemTemplate itemTemplate2 = persistedInventory2.getItemTemplateOnPosition(pos2);

		if (itemTemplate1 == itemTemplate2) {
			return true;
		}

		if (itemTemplate1 == null || itemTemplate2 == null) {
			return false;
		}

		return itemTemplate1.getPk().equals(itemTemplate2.getPk());
	}


	@Override
	@AutoTransaction
	public void removeIfExists(ServerItem serverItem) {
		PersistedInventory persistedInventory = inventoryDao.findInventory(serverItem);
		if (persistedInventory != null) {
			int positionOfTheRemovedItem = persistedInventory.removeItem(serverItem);
			inventoryDao.persist(persistedInventory);
			broadcaster.create().to(persistedInventory.getOwner()).send().item().itemRemoved(persistedInventory, positionOfTheRemovedItem, serverItem);
		}
	}
}


