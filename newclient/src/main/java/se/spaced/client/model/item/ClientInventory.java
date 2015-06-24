package se.spaced.client.model.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;

import java.util.Collection;

public class ClientInventory implements SpacedInventory, InventoryData {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final UUID pk;
	private final Multimap<Integer, ClientItem> items;

	public ClientInventory(UUID id, Multimap<Integer, ClientItem> items) {
		pk = id;
		this.items = HashMultimap.create(items);
	}


	public ImmutableMultimap<Integer, ? extends SpacedItem> getItemMap() {
		return ImmutableMultimap.copyOf(items);
	}

	@LuaMethod(name = "GetItems")
	public ImmutableMap<Integer, Collection<ClientItem>> getItems() {
		return ImmutableMap.copyOf(items.asMap());
	}

	@Override
	@LuaMethod(name = "GetId")
	public UUID getPk() {
		return pk;
	}

	public void addItem(int position, ClientItem clientItem) {
		items.put(position, clientItem);
		log.info("addItem({}, {}) stackSize={}", position, clientItem, items.get(position).size());

	}

	public void removeItem(ClientItem clientItem, int position) {
		items.get(position).remove(clientItem);
		log.info("removeItem({})", clientItem);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SpacedInventory)) {
			return false;
		}
		SpacedInventory inventory = (SpacedInventory) o;
		UUID pk1 = getPk();
		UUID pk2 = inventory.getPk();
		if (pk1 != null && pk2 != null) {
			return pk1.equals(pk2);
		}
		return false;
	}

	@Override
	public int hashCode() {
		UUID pk = getPk();
		if (pk != null) {
			return getPk().hashCode();
		}
		return super.hashCode();
	}

	public void swapItems(int pos1, int pos2) {
		Collection<ClientItem> itemStack1 = items.removeAll(pos1);
		Collection<ClientItem> itemStack2 = items.removeAll(pos2);
		if (itemStack2 != null) {
			items.putAll(pos1, itemStack2);
		}
		if (itemStack1 != null) {
			items.putAll(pos2, itemStack1);
		}
	}
}
