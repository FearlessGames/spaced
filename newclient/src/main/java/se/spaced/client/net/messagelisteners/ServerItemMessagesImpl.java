package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.ItemEvents;
import se.spaced.client.model.ClientSpell;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.ClientInventory;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemJob;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.s2c.ServerItemMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;

import java.util.List;

public class ServerItemMessagesImpl implements ServerItemMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final EventHandler eventHandler;
	private final InventoryProvider inventory;

	private final ActiveCache<SpacedItem, ClientItem> itemCache;
	private final ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache;
	private final ActiveCache<Spell, ClientSpell> spellCache;

	@Inject
	public ServerItemMessagesImpl(EventHandler eventHandler, InventoryProvider inventory, ActiveCache<SpacedItem, ClientItem> itemCache,
											ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache, ActiveCache<Spell, ClientSpell> spellCache) {
		this.eventHandler = eventHandler;
		this.inventory = inventory;
		this.itemCache = itemCache;
		this.itemTemplateCache = itemTemplateCache;
		this.spellCache = spellCache;
	}

	@Override
	public void sendInventory(InventoryData inventory) {
		log.info("Got inventory {}", inventory);
		for (SpacedItem item : inventory.getItemMap().values()) {
			ClientItem clientItem = (ClientItem) item;
			addToItemTemplateCache(clientItem.getItemTemplateData());
			itemCache.setValue(clientItem, clientItem);
		}
		this.inventory.setPlayerInventory((ClientInventory) inventory);
		eventHandler.fireAsynchEvent(ItemEvents.INVENTORY_UPDATED, inventory);
	}

	@Override
	public void itemTemplateDataResponse(final ItemTemplateData data) {
		log.debug("itemTemplateDataResponse: {} - {}", data.getName(), data.getOnClickSpell());
		addToItemTemplateCache(data);
	}

	private void addToItemTemplateCache(final ItemTemplateData data) {
		if (data.getOnClickSpell() != null) {
			spellCache.runWhenReady(data.getOnClickSpell(), new Job<ClientSpell>() {
				@Override
				public void run(ClientSpell value) {
					data.setOnClickSpell(value);
					itemTemplateCache.setValue(data, data);
				}
			});
		} else {
			itemTemplateCache.setValue(data, data);
		}
	}

	@Override
	public void itemAdded(SpacedInventory inventory, final int position, final SpacedItem incomingItem) {
		itemCache.runWhenReady(incomingItem, new ItemJob() {
			@Override
			public void run(ClientItem item) {
				ClientInventory inventory = ServerItemMessagesImpl.this.inventory.getPlayerInventory();
				inventory.addItem(position, item);
				eventHandler.fireAsynchEvent(ItemEvents.INVENTORY_ADD_ITEM, inventory, item, position);
			}
		});
	}

	@Override
	public void itemRemoved(SpacedInventory inventory, int position, SpacedItem item) {
		ClientItem value = itemCache.getValue(item);
		if (value != null) {
			ClientInventory clientInventory = this.inventory.getPlayerInventory();
			clientInventory.removeItem(value, position);
			eventHandler.fireAsynchEvent(ItemEvents.INVENTORY_REMOVE_ITEM, clientInventory, value, position);
			itemCache.delete(item);
		}
	}

	@Override
	public void itemsSwapped(SpacedInventory inventory1, int pos1, SpacedInventory inventory2, int pos2) {
		// TODO: handle the case where you can have more than one inventory
		ClientInventory playerInventory = inventory.getPlayerInventory();
		playerInventory.swapItems(pos1, pos2);
		eventHandler.fireAsynchEvent(ItemEvents.INVENTORY_UPDATED, playerInventory);
	}

	@Override
	public void itemsMoved(SpacedInventory inventory1, int pos1, List<? extends SpacedItem> movedItems, SpacedInventory inventory2, int pos2) {

		//move from inventory 1 to inventory 2 with the supplied quantity
		ClientInventory playerInventory = inventory.getPlayerInventory();

		for (SpacedItem movedItem : movedItems) {
			//playerInventory.removeItem(movedItem);
			//playerInventory.addItem(pos2, movedItem);
		}

		eventHandler.fireAsynchEvent(ItemEvents.INVENTORY_UPDATED, playerInventory);

	}
}
