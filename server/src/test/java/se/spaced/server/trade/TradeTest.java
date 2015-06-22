package se.spaced.server.trade;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.fearlessgames.common.mock.MockUtil;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.fearlessgames.common.util.uuid.UUIDMockFactory;
import se.hiflyer.fettle.StateMachineTemplate;
import se.hiflyer.fettle.export.DotExporter;
import se.hiflyer.fettle.impl.AbstractTransitionModel;
import se.mockachino.*;
import se.mockachino.order.*;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryService;
import se.spaced.server.model.items.InventoryServiceImpl;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;
import se.spaced.server.persistence.dao.impl.inmemory.InMemoryInventoryDao;
import se.spaced.shared.model.items.ItemType;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class TradeTest {
	private UUIDMockFactory uuidFactory;
	private TimeProvider timeProvider;
	private ServerEntity initiator;
	private ServerEntity collaborator;
	private ServerItem toyCar;
	private ServerItem screw;
	private Inventory initiatorsInventory;
	private Inventory collaboratorInventory;
	private TradeCallback tradeCallback;
	private TradeTransitionModelProvider tradeTransitionModelProvider;

	@Before
	public void setUp() throws Exception {
		uuidFactory = new UUIDMockFactory();
		timeProvider = new MockTimeProvider();
		tradeCallback = mock(TradeCallback.class);
		tradeTransitionModelProvider = new TradeTransitionModelProvider(tradeCallback);
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		initiator = playerMockFactory.createPlayer("Mr Initiator");
		collaborator = playerMockFactory.createPlayer("Mr Collaborator");


		InventoryService inventoryService = new InventoryServiceImpl(new InMemoryInventoryDao(), MockUtil.deepMock(SmrtBroadcasterImpl.class));
		initiatorsInventory = inventoryService.createInventory(initiator, 100, InventoryType.BAG);
		collaboratorInventory = inventoryService.createInventory(collaborator, 100, InventoryType.BAG);


		ServerItemTemplate carTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Toy car",
				ItemType.JETPACK).build();
		ServerItemTemplate screwTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Rusty screw",
				ItemType.MAIN_HAND_ITEM).build();
		toyCar = carTemplate.create();
		toyCar.setPk(uuidFactory.combUUID());
		screw = screwTemplate.create();
		screw.setPk(uuidFactory.combUUID());
	}

	@Test
	public void testTradeNotInitiated() {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		assertEquals(TradeState.START, trade.getCurrentState());
	}

	@Test
	public void testTradeInitiated() {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();
		assertEquals(TradeState.NEGOTIATING, trade.getCurrentState());
		OrderingContext orderingContext = Mockachino.newOrdering();
		orderingContext.verify().on(tradeCallback).initiated(initiator, collaborator, any(String.class));
		orderingContext.verify().on(tradeCallback).negotiating(initiator, collaborator);
	}

	@Test
	public void testAddItems() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();


		String emptyTradeCheck = trade.getChecksum();

		trade.addItemFromInitiator(toyCar);
		ArrayList<ServerItem> initiatorItems = Lists.newArrayList(trade.getItemsFromInitiator());
		assertTrue(initiatorItems.contains(toyCar));
		assertEquals(1, initiatorItems.size());
		verifyOnce().on(tradeCallback).itemAdded(initiator, collaborator, toyCar, any(String.class));

		String carAddedCheck = trade.getChecksum();
		assertFalse(emptyTradeCheck.equals(carAddedCheck));

		trade.addItemFromCollaborator(screw);
		ArrayList<ServerItem> collaboratorItems = Lists.newArrayList(trade.getItemsFromCollaborator());
		assertTrue(collaboratorItems.contains(screw));
		assertEquals(1, collaboratorItems.size());
		verifyOnce().on(tradeCallback).itemAdded(collaborator, initiator, screw, any(String.class));

		String screwAddedCheck = trade.getChecksum();
		assertFalse(emptyTradeCheck.equals(screwAddedCheck));
		assertFalse(screwAddedCheck.equals(carAddedCheck));
	}


	@Test
	public void testAddItemsMultipleTimes() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		String emptyTradeCheck = trade.getChecksum();

		trade.addItemFromInitiator(toyCar);
		trade.addItemFromInitiator(toyCar);
		ArrayList<ServerItem> initiatorItems = Lists.newArrayList(trade.getItemsFromInitiator());
		assertTrue(initiatorItems.contains(toyCar));
		assertEquals(1, initiatorItems.size());
		verifyOnce().on(tradeCallback).itemAdded(initiator, collaborator, toyCar, any(String.class));

		String carAddedCheck = trade.getChecksum();
		assertFalse(emptyTradeCheck.equals(carAddedCheck));

		trade.addItemFromCollaborator(screw);
		trade.addItemFromCollaborator(screw);
		ArrayList<ServerItem> collaboratorItems = Lists.newArrayList(trade.getItemsFromCollaborator());
		assertTrue(collaboratorItems.contains(screw));
		assertEquals(1, collaboratorItems.size());
		verifyOnce().on(tradeCallback).itemAdded(collaborator, initiator, screw, any(String.class));
	}


	@Test
	public void testInitiatorAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.initiatorAccepted(trade.getChecksum());

		assertEquals(TradeState.INITIATOR_ACCEPT, trade.getCurrentState());
	}

	@Test
	public void testCollaboratorAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.collaboratorAccepted(trade.getChecksum());

		assertEquals(TradeState.COLLABORATOR_ACCEPT, trade.getCurrentState());
	}

	@Test
	public void testBothAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.addItemFromInitiator(toyCar);

		trade.initiatorAccepted(trade.getChecksum());
		trade.collaboratorAccepted(trade.getChecksum());

		assertEquals(TradeState.COMPLETED, trade.getCurrentState());
	}

	@Test
	public void testInitiatorItemAddedAfterOneAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.collaboratorAccepted(trade.getChecksum());

		trade.addItemFromInitiator(toyCar);

		assertEquals(TradeState.NEGOTIATING, trade.getCurrentState());
		assertEquals(1, Lists.newArrayList(trade.getItemsFromInitiator()).size());
	}

	@Test
	public void testCollaboratorItemAddedAfterOneAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.collaboratorAccepted(trade.getChecksum());

		trade.addItemFromCollaborator(screw);

		assertEquals(TradeState.NEGOTIATING, trade.getCurrentState());
	}

	@Test(expected = IllegalStateException.class)
	public void testAddItemAfterTradeIsCompleted() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.addItemFromInitiator(toyCar);

		trade.initiatorAccepted(trade.getChecksum());
		trade.collaboratorAccepted(trade.getChecksum());

		trade.addItemFromCollaborator(screw);
	}

	@Test
	public void testCollaboratorRejects() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.collaboratorRejected();

		assertEquals(TradeState.ABORTED, trade.getCurrentState());
	}

	@Test
	public void testCollaboratorRejectsWhenInitiatorAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.initiatorAccepted(trade.getChecksum());

		trade.collaboratorRejected();

		assertEquals(TradeState.ABORTED, trade.getCurrentState());
	}

	@Test
	public void testInitiatorRejects() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.initiatorRejected();

		assertEquals(TradeState.ABORTED, trade.getCurrentState());
	}

	@Test
	public void testInitiatorRejectsWhenCollaboratorAccepts() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.collaboratorAccepted(trade.getChecksum());

		trade.initiatorRejected();

		assertEquals(TradeState.ABORTED, trade.getCurrentState());
	}

	@Test(expected = IllegalStateException.class)
	public void testAddItemAfterTradeIsRejected() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		try {
			trade.initiated();

			trade.collaboratorAccepted(trade.getChecksum());
			trade.initiatorRejected();


			trade.addItemFromCollaborator(screw);
		} catch (Exception e) {
			assertEquals(0, Lists.newArrayList(trade.getItemsFromCollaborator()).size());
			throw e;
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testAddItemAfterTradeIsRejected2() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		try {
			trade.initiated();

			trade.collaboratorAccepted(trade.getChecksum());
			trade.initiatorRejected();


			trade.addItemFromInitiator(toyCar);
		} catch (Exception e) {
			assertEquals(0, Lists.newArrayList(trade.getItemsFromInitiator()).size());
			throw e;
		}
	}

	@Test
	public void testAbortFromNegotiating() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.abortTrade();

		assertEquals(TradeState.ABORTED, trade.getCurrentState());
	}

	@Test
	public void testAbortFromInitiatorAccept() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.initiatorAccepted(trade.getChecksum());

		trade.abortTrade();

		assertEquals(TradeState.ABORTED, trade.getCurrentState());
	}

	@Test(expected = IllegalStateException.class)
	public void testAddItemAfterTradeIsAborted() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.abortTrade();

		trade.addItemFromCollaborator(screw);
	}

	@Test(expected = IllegalStateException.class)
	public void testCantAbortCompletedTrade() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();

		trade.addItemFromInitiator(toyCar);

		trade.initiatorAccepted(trade.getChecksum());
		trade.collaboratorAccepted(trade.getChecksum());

		trade.abortTrade();
	}

	@Test
	public void testTradeInitiatorContainsItem() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();
		assertFalse(trade.containsItem(toyCar));
		trade.addItemFromInitiator(toyCar);
		assertTrue(trade.containsItem(toyCar));
	}

	@Test
	public void testTradeCollaboratorContainsItem() throws Exception {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();
		assertFalse(trade.containsItem(toyCar));
		trade.addItemFromCollaborator(toyCar);
		assertTrue(trade.containsItem(toyCar));
	}

	@Test
	public void testCollaboratorCloseTrade() {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();
		trade.collaboratorRejected();
		assertEquals(TradeState.ABORTED, trade.getCurrentState());
		OrderingContext orderingContext = Mockachino.newOrdering();
		orderingContext.verify().on(tradeCallback).collaboratorRejected(initiator, collaborator);
		orderingContext.verify().on(tradeCallback).aborted(initiator, collaborator);
	}

	@Test
	public void testInitiatorCloseTrade() {
		Trade trade = new Trade(initiator, collaborator, tradeTransitionModelProvider.get());
		trade.initiated();
		trade.initiatorRejected();
		assertEquals(TradeState.ABORTED, trade.getCurrentState());

		OrderingContext orderingContext = Mockachino.newOrdering();
		orderingContext.verify().on(tradeCallback).initiatorRejected(initiator, collaborator);
		orderingContext.verify().on(tradeCallback).aborted(initiator, collaborator);
	}

	@Test
	public void testDot() throws Exception {
		StateMachineTemplate<TradeState, TradeActions> transitionModel = tradeTransitionModelProvider.get();
		DotExporter<TradeState, TradeActions> exp = new DotExporter<TradeState, TradeActions>((AbstractTransitionModel<TradeState, TradeActions>) transitionModel,
				"Trade");
		exp.asDot(System.out, false);
	}
}
