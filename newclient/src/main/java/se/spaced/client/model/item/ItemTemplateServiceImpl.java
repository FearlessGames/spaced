package se.spaced.client.model.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.shared.activecache.ActiveCacheImpl;
import se.spaced.shared.activecache.KeyRequestHandler;

@Singleton
public class ItemTemplateServiceImpl extends ActiveCacheImpl<ItemTemplate, ItemTemplateData> {

	@Inject
	public ItemTemplateServiceImpl(final ServerConnection serverConnection) {
		super(new KeyRequestHandler<ItemTemplate>() {
			@Override
			public void requestKey(ItemTemplate key) {
				serverConnection.getReceiver().items().requestItemTemplateData(key.getPk());
			}
		});
	}
}
