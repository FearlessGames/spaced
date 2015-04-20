package se.fearlessgames.prototyping.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import se.mockachino.matchers.matcher.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;
import static se.mockachino.matchers.MatchersBase.mAny;

public class ExchangeTest {

	private Exchange exchange;
	private ItemPriceService priceService;
	private int desiredStockCount;
	private ItemTemplate itemTemplate;

	@Before
	public void setUp() throws Exception {
		itemTemplate = new ItemTemplate(100);
		priceService = mock(ItemPriceService.class);
		desiredStockCount = 10000;
		exchange = new Exchange(desiredStockCount, priceService, itemTemplate);
	}

	@Test
	public void addOrder() throws Exception {
		exchange.giveMoney(1000);
		Offer offer = exchange.requestOffer(3, itemTemplate, mock(Buyer.class));
		exchange.acceptOffer(offer, offer.getTotalPrice());

		assertEquals(1, exchange.getNumberOfOutstandingOrders());
	}

	@Test
	public void orderIdAreDifferent() throws Exception {
		exchange.giveMoney(1000000);
		Set<Integer> ids = Sets.newHashSet();
		for (int i = 0; i < 1000; i++) {

			Offer offer = exchange.requestOffer(i % 5, itemTemplate, mock(Buyer.class));

			int id = exchange.acceptOffer(offer, offer.getTotalPrice()).getOrderId();

			boolean added = ids.add(id);
			assertTrue("Duplicate id: " + id, added);
		}
	}

	@Test
	public void addItems() throws Exception {
		exchange.giveMoney(1000);
		assertEquals(0, exchange.getNumberOfItemsInStock());

		exchange.addItems(Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate)));

		assertEquals(2, exchange.getNumberOfItemsInStock());
	}

	@Test
	public void tickHandlesOrders() throws Exception {
		exchange.giveMoney(1000);
		Buyer buyer = mock(Buyer.class);

		Offer offer = exchange.requestOffer(3, itemTemplate, buyer);
		Order acceptOrder = exchange.acceptOffer(offer, offer.getTotalPrice());

		exchange.addItems(Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));

		exchange.tick();

		ArgumentCatcher<List> catcher = ArgumentCatcher.create(mAny(List.class));

		verifyOnce().on(buyer).receive(acceptOrder, match(catcher));

		List<Item> values = catcher.getValue();

		assertEquals(3, values.size());
		assertEquals(0, exchange.getNumberOfOutstandingOrders());
	}

	@Test
	public void noOrderUntilItsAccepted() throws Exception {
		exchange.giveMoney(1000);
		Buyer buyer = mock(Buyer.class);
		Offer offer = exchange.requestOffer(3, itemTemplate, buyer);
		assertEquals(0, exchange.getNumberOfOutstandingOrders());
		exchange.acceptOffer(offer, offer.getTotalPrice());
		assertEquals(1, exchange.getNumberOfOutstandingOrders());

		exchange.tick();

		verifyNever().on(buyer).receive(any(Order.class), any(List.class));
	}

	@Test
	public void tickWhenTooFewItems() throws Exception {
		exchange.giveMoney(1000);
		Buyer buyer = mock(Buyer.class);
		Offer offer = exchange.requestOffer(3, itemTemplate, buyer);
		Order acceptedOrder = exchange.acceptOffer(offer, offer.getTotalPrice());


		exchange.addItems(Lists.newArrayList(new Item(itemTemplate)));

		exchange.tick();

		ArgumentCatcher<List> catcher = ArgumentCatcher.create(mAny(List.class));

		verifyOnce().on(buyer).receive(acceptedOrder, match(catcher));

		List<Item> values = catcher.getValue();

		assertEquals(1, values.size());
		assertEquals(1, exchange.getNumberOfOutstandingOrders());
	}

	@Test
	public void sendItemsInTwoTicks() throws Exception {
		exchange.giveMoney(1000);
		Buyer buyer = mock(Buyer.class);

		Offer offer = exchange.requestOffer(3, itemTemplate, buyer);
		Order acceptedOrder = exchange.acceptOffer(offer, offer.getTotalPrice());

		exchange.addItems(Lists.newArrayList(new Item(itemTemplate)));


		exchange.tick();

		exchange.addItems(Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate)));

		exchange.tick();

		ArgumentCatcher<List> catcher = ArgumentCatcher.create(mAny(List.class));

		verifyExactly(2).on(buyer).receive(any(Order.class), match(catcher));

		List<List> values = catcher.getValues();

		assertEquals(1, values.get(0).size());
		assertEquals(2, values.get(1).size());
		assertEquals(0, exchange.getNumberOfOutstandingOrders());
	}

	@Test
	public void sendItemsToMultipleReceivers() throws Exception {
		exchange.giveMoney(1000);
		Buyer buyer1 = mock(Buyer.class);
		Buyer buyer2 = mock(Buyer.class);

		Offer offer1 = exchange.requestOffer(2, itemTemplate, buyer1);
		Order order1 = exchange.acceptOffer(offer1, offer1.getTotalPrice());

		Offer offer2 = exchange.requestOffer(2, itemTemplate, buyer2);
		Order order2 = exchange.acceptOffer(offer2, offer2.getTotalPrice());

		exchange.addItems(Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));

		exchange.tick();
		exchange.tick();

		ArgumentCatcher<List> catcher1 = ArgumentCatcher.create(mAny(List.class));
		ArgumentCatcher<List> catcher2 = ArgumentCatcher.create(mAny(List.class));

		verifyOnce().on(buyer1).receive(order1, match(catcher1));
		verifyOnce().on(buyer2).receive(order2, match(catcher2));

		List<Item> values1 = catcher1.getValue();
		List<Item> values2 = catcher2.getValue();

		assertEquals(2, values1.size());
		assertEquals(2, values2.size());
		assertEquals(0, exchange.getNumberOfOutstandingOrders());

	}

	@Test
	public void sendItemsToMultipleReceiversWhenThereAreNotEnough() throws Exception {
		exchange.giveMoney(1000);
		Buyer buyer1 = mock(Buyer.class);
		Buyer buyer2 = mock(Buyer.class);
		Buyer buyer3 = mock(Buyer.class);

		Offer offer1 = exchange.requestOffer(2, itemTemplate, buyer1);
		Order order1 = exchange.acceptOffer(offer1, offer1.getTotalPrice());


		Offer offer2 = exchange.requestOffer(3, itemTemplate, buyer2);
		Order order2 = exchange.acceptOffer(offer2, offer2.getTotalPrice());


		Offer offer3 = exchange.requestOffer(1, itemTemplate, buyer3);
		Order order3 = exchange.acceptOffer(offer3, offer3.getTotalPrice());


		ArrayList<Item> items = Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate), new Item(
				itemTemplate));
		exchange.addItems(items);

		exchange.tick();
		exchange.tick();

		ArgumentCatcher<List> catcher1 = ArgumentCatcher.create(mAny(List.class));
		ArgumentCatcher<List> catcher2 = ArgumentCatcher.create(mAny(List.class));

		verifyOnce().on(buyer1).receive(order1, match(catcher1));
		verifyOnce().on(buyer2).receive(order2, match(catcher2));

		List<Item> values1 = catcher1.getValue();
		List<Item> values2 = catcher2.getValue();

		assertEquals(2, values1.size());
		assertEquals(2, values2.size());
		assertEquals(2, exchange.getNumberOfOutstandingOrders());

		exchange.addItems(Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate)));

		getData(buyer2).resetCalls();
		getData(buyer3).resetCalls();

		exchange.tick();
			exchange.tick();

		ArgumentCatcher<List> catcher22 = ArgumentCatcher.create(mAny(List.class));
		ArgumentCatcher<List> catcher3 = ArgumentCatcher.create(mAny(List.class));

		// Needs to be any(Order) since it is changed when it's partially delivered in previous ticks
		verifyOnce().on(buyer2).receive(any(Order.class), match(catcher22));
		verifyOnce().on(buyer3).receive(order3, match(catcher3));


		List<Item> values22 = catcher22.getValue();
		List<Item> values3 = catcher3.getValue();

		assertEquals(1, values22.size());
		assertEquals(1, values3.size());
		assertEquals(0, exchange.getNumberOfOutstandingOrders());

	}

	@Test
	public void exchangePaysForItems() throws Exception {
		int basePrice = 100;
		exchange.giveMoney(10 * basePrice);
		Collection<Item> items = Lists.newArrayList(new Item(itemTemplate));
		long monies = exchange.addItems(items);
		assertEquals(basePrice, monies);

		assertEquals(9* basePrice, exchange.getMoney());
	}

	@Test
	public void exchangeWontBuyWhenOutOfMoney() throws Exception {
		int basePrice = 100;
		int exchangeFunds = basePrice / 3;
		exchange.giveMoney(exchangeFunds);
		Collection<Item> items = Lists.newArrayList(new Item(itemTemplate));
		long monies = exchange.addItems(items);
		assertEquals(0, monies);

		assertEquals(exchangeFunds, exchange.getMoney());

	}

	@Test
	public void exchangeIncludesPriceInOffer() throws Exception {
		when(priceService.getPriceForItemBasedOnStock(anyInt(), anyInt(), anyInt())).thenReturn(25);
		Offer offer = exchange.requestOffer(1, itemTemplate, mock(Buyer.class));

		assertEquals(25, offer.getTotalPrice());

		verifyOnce().on(priceService).getPriceForItemBasedOnStock(desiredStockCount, 0, itemTemplate.getBasePrice());
	}
}
