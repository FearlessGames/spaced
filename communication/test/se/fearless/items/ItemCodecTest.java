package se.fearless.items;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.net.smrt.ServerToClientReadCodec;
import se.spaced.messages.protocol.InventoryData;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.server.model.Player;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.InventoryServiceImpl;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.PersistedInventory;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.net.mina.ServerToClientRequiredWriteCodec;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryInventoryDao;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.network.protocol.codec.SharedCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.fearless.CodecUtils.getInputStream;

public class ItemCodecTest {

	private ServerToClientRequiredWriteCodec writeCodec;
	private ByteArrayOutputStream outputStream;

	private ServerToClientReadCodec readCodec;
	private MockTimeProvider timeProvider;
	private UUIDFactory uuidFactory;
	private InventoryService inventoryService;
	private Player player;
	private ServerItemTemplate itemA;
	private ServerItemTemplate itemB;


	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();
		timeProvider = new MockTimeProvider();
		SharedCodec sharedCodec = new SharedCodec(timeProvider);
		writeCodec = new ServerToClientRequiredWriteCodec(sharedCodec, timeProvider);
		readCodec = new ServerToClientReadCodec(sharedCodec, new InventoryProvider(uuidFactory), timeProvider);

		outputStream = new ByteArrayOutputStream();
		InventoryDao inventoryDao = new InMemoryInventoryDao();
		inventoryService = new InventoryServiceImpl(inventoryDao, MockUtil.deepMock(SmrtBroadcasterImpl.class));
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		player = playerMockFactory.createPlayer("Abed");

		itemA = new ServerItemTemplate.Builder(uuidFactory.combUUID(), "ItemA", ItemType.HELMET).build();
		itemB = new ServerItemTemplate.Builder(uuidFactory.combUUID(), "ItemB", ItemType.HELMET).stackable(5).build();
	}

	private InventoryData writeAndReadInventory(PersistedInventory serverSideInventory) throws IOException {
		writeCodec.writeInventoryData(outputStream, serverSideInventory);
		InputStream inputStream = getInputStream(outputStream);
		return readCodec.readInventoryData(inputStream);
	}

	private ServerItem createItem(ServerItemTemplate itemTemplate) {
		ServerItem item = itemTemplate.create();
		item.setOwner(player);
		item.setPk(uuidFactory.combUUID());
		return item;
	}

	@Test
	public void sendEmptyInventory() throws Exception {
		PersistedInventory serverSideInventory = inventoryService.createInventory(player, 10, InventoryType.BAG);

		InventoryData inventoryData = writeAndReadInventory(serverSideInventory);

		assertEquals(serverSideInventory.getPk(), inventoryData.getPk());
		assertEquals(0, inventoryData.getItemMap().size());

	}

	@Test
	public void sendIventoryWithSomeItems() throws Exception {
		PersistedInventory serverSideInventory = inventoryService.createInventory(player, 10, InventoryType.BAG);
		ServerItem serverItemA = createItem(itemA);
		ServerItem serverItemB = createItem(itemB);

		serverSideInventory.addItem(serverItemA, 4);
		serverSideInventory.addItem(serverItemB, 7);

		InventoryData inventoryData = writeAndReadInventory(serverSideInventory);

		assertEquals(serverSideInventory.getPk(), inventoryData.getPk());

		assertEquals(2, inventoryData.getItemMap().size());

		SpacedItem clientItemA = Iterables.getFirst(inventoryData.getItemMap().get(4), null);
		assertEquals(serverItemA.getPk(), clientItemA.getPk());
		assertEquals(serverItemA.getTemplate().getPk(), clientItemA.getItemTemplate().getPk());

		SpacedItem clientItemB = Iterables.getFirst(inventoryData.getItemMap().get(7), null);
		assertEquals(serverItemB.getPk(), clientItemB.getPk());
		assertEquals(serverItemB.getTemplate().getPk(), clientItemB.getItemTemplate().getPk());


	}

	@Test
	public void sendIventoryWithSomeStacks() throws Exception {
		PersistedInventory serverSideInventory = inventoryService.createInventory(player, 10, InventoryType.BAG);
		ServerItem serverItemA = createItem(itemB);
		ServerItem serverItemB = createItem(itemB);
		ServerItem serverItemC = createItem(itemB);
		final ServerItem serverItemD = createItem(itemB);
		ServerItem serverItemE = createItem(itemB);

		serverSideInventory.addItem(serverItemA, 4);
		serverSideInventory.addItem(serverItemB, 4);
		serverSideInventory.addItem(serverItemC, 7);
		serverSideInventory.addItem(serverItemD, 7);
		serverSideInventory.addItem(serverItemE, 7);

		InventoryData inventoryData = writeAndReadInventory(serverSideInventory);

		assertEquals(serverSideInventory.getPk(), inventoryData.getPk());

		assertEquals(5, inventoryData.getItemMap().size());

		assertEquals(2, inventoryData.getItemMap().get(4).size());
		assertEquals(3, inventoryData.getItemMap().get(7).size());

		assertTrue(Iterables.tryFind(inventoryData.getItemMap().get(7), new Predicate<SpacedItem>() {
			@Override
			public boolean apply(SpacedItem item) {
				return item.getPk().equals(serverItemD.getPk());
			}
		}).isPresent());
	}
}
