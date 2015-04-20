package se.spaced.client.model.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.activecache.ActiveCache;
import se.spaced.shared.activecache.ActiveCacheImpl;
import se.spaced.shared.activecache.KeyRequestHandler;

@Singleton
public class ItemLookup extends ActiveCacheImpl<SpacedItem, ClientItem> {


	@Inject
	public ItemLookup(final ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache) {
		super(new SpacedItemKeyRequestHandler(itemTemplateCache));
		SpacedItemKeyRequestHandler itemKeyRequestHandler = (SpacedItemKeyRequestHandler) keyRequestHandler;
		itemKeyRequestHandler.setItemCache(this);
	}

	private static class SpacedItemKeyRequestHandler implements KeyRequestHandler<SpacedItem> {
		private final ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache;
		private ActiveCache<SpacedItem, ClientItem> itemCache;

		SpacedItemKeyRequestHandler(ActiveCache<ItemTemplate, ItemTemplateData> itemTemplateCache) {
			this.itemTemplateCache = itemTemplateCache;
		}


		@Override
		public void requestKey(final SpacedItem item) {
			itemTemplateCache.runWhenReady(item.getItemTemplate(), new ItemTemplateJob() {
				@Override
				public void run(ItemTemplateData value) {
					itemCache.setValue(item, new ClientItem(item.getPk(), value));
				}
			});

		}

		public void setItemCache(ActiveCache<SpacedItem, ClientItem> itemCache) {
			this.itemCache = itemCache;
		}
	}
}
