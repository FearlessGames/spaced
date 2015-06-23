package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.VendorEvents;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemJob;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.ServerVendorMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.Job;
import se.spaced.shared.events.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Singleton
public class ServerVendorMessagesImpl implements ServerVendorMessages {
	private final ActiveCache<Entity, ClientEntity> entityCache;
	private final ActiveCache<SpacedItem, ClientItem> itemCache;
	private final EventHandler eventHandler;
	private final Logger log = LoggerFactory.getLogger(getClass());
	private ClientEntity activeVendor;

	@Inject
	public ServerVendorMessagesImpl(
			ActiveCache<Entity, ClientEntity> entityCache,
			ActiveCache<SpacedItem, ClientItem> itemCache, EventHandler eventHandler) {
		this.entityCache = entityCache;
		this.itemCache = itemCache;
		this.eventHandler = eventHandler;
	}

	@Override
	public void vendorStockItems(Entity entity, final List<? extends SpacedItem> items) {
		log.debug("Received a list of items for sale: {}", items);

		entityCache.runWhenReady(entity, new Job<ClientEntity>() {
			@Override
			public void run(final ClientEntity vendor) {
				final AtomicInteger countdown = new AtomicInteger(items.size());
				final List<ClientItem> clientItems = new ArrayList<ClientItem>(items.size());
				for (SpacedItem item : items) {
					itemCache.runWhenReady(item, new ItemJob() {
						@Override
						public void run(ClientItem clientItem) {
							clientItems.add(clientItem);
							log.debug("adding {} to list of items", clientItem);
							if (countdown.decrementAndGet() == 0) {
								eventHandler.fireAsynchEvent(VendorEvents.VENDOR_STOCK_ITEMS, vendor, clientItems);
								log.debug("Done updating incoming vendor items {}", clientItems);
							}
						}
					});
				}
			}
		});
	}

	@Override
	public void cannotAfford(String currency, long amount) {
		eventHandler.fireAsynchEvent(VendorEvents.VENDOR_CANNOT_AFFORD);
	}

	@Override
	public void vendorOutOfRange(Entity vendor) {
		log.debug("got vendor out of range reply for vendor {}", vendor);
		eventHandler.fireAsynchEvent(VendorEvents.VENDOR_OUT_OF_RANGE);
	}

	@Override
	public void vendorAddedItem(SpacedItem newItem) {
		itemCache.runWhenReady(newItem, new ItemJob() {
			@Override
			public void run(ClientItem value) {
				eventHandler.fireAsynchEvent(VendorEvents.VENDOR_ADDED_ITEM, value);
			}
		});
	}

	@Override
	public void boughtItem(SpacedItem item) {
		itemCache.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem value) {
				eventHandler.fireAsynchEvent(VendorEvents.VENDOR_BOUGHT_ITEM, value);
			}
		});
	}

	@Override
	public void itemWasBought(SpacedItem item) {
		itemCache.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem value) {
				eventHandler.fireAsynchEvent(VendorEvents.VENDOR_BOUGHT_ITEM, value);
			}
		});
	}

	@Override
	public void vendorDespawned(Entity vendor) {
		eventHandler.fireAsynchEvent(VendorEvents.VENDOR_DESPAWNED);
	}

	@Override
	public void inventoryFull() {
		eventHandler.fireAsynchEvent(VendorEvents.VENDOR_INVENTORY_FULL);
	}


	public ClientEntity getActiveVendor() {
		return this.activeVendor;
	}

	public void setVendoringActive(ClientEntity activeVendor) {
		this.activeVendor = activeVendor;
	}

	public void stopVendoring() {
		activeVendor = null;
	}
}
