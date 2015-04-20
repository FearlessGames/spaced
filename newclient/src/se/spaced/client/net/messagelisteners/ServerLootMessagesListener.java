package se.spaced.client.net.messagelisteners;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.client.ardor.ui.events.ItemEvents;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.model.item.ItemJob;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.ServerLootMessages;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.events.EventHandler;

public class ServerLootMessagesListener implements ServerLootMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ActiveCache<SpacedItem, ClientItem> itemCache;
	private final EventHandler eventHandler;

	@Inject
	public ServerLootMessagesListener(ActiveCache<SpacedItem, ClientItem> itemCache, EventHandler eventHandler) {
		this.itemCache = itemCache;
		this.eventHandler = eventHandler;
	}

	@Override
	public void receivedLoot(SpacedItem item) {
		itemCache.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem clientItem) {
				eventHandler.fireAsynchEvent(ItemEvents.RECEIVED_LOOT, clientItem);
			}
		});
	}
}
