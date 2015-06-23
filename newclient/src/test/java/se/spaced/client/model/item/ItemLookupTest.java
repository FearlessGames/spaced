package se.spaced.client.model.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.messages.protocol.AuraTemplate;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.ItemTemplateData;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.shared.model.AppearanceData;
import se.spaced.shared.model.Money;
import se.spaced.shared.model.items.ItemType;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

public class ItemLookupTest {

	private ItemLookup itemLookup;
	private ServerConnection connection;
	private UUIDMockFactory uuidMockFactory;
	private ItemTemplateServiceImpl itemTemplateCache;

	@Before
	public void setUp() throws Exception {
		uuidMockFactory = new UUIDMockFactory();
		connection = MockUtil.deepMock(ServerConnection.class);
		itemTemplateCache = new ItemTemplateServiceImpl(connection);
		itemLookup = new ItemLookup(itemTemplateCache);
	}

	@Test(timeout = 300)
	public void getItemHappyPath() throws Exception {
		ItemTemplate itemTemplate = new ItemTemplateProxy(uuidMockFactory.combUUID());
		SpacedItem item = new ClientItemProxy(uuidMockFactory.combUUID(), itemTemplate);
		
		
		final CountDownLatch done = new CountDownLatch(1);
		itemLookup.runWhenReady(item, new ItemJob() {
			@Override
			public void run(ClientItem value) {
				assertEquals("Model name", value.getModelPath());
				done.countDown();
			}
		});
		itemTemplateCache.setValue(itemTemplate, new ItemTemplateData(uuidMockFactory.combUUID(), "Foo template",
				new AppearanceData("Model name", "portrait"), Lists.newArrayList(ItemType.GLOVES), Sets.<AuraTemplate>newHashSet(),
				Money.ZERO, null));
		done.await();
	}
}
