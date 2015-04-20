package se.fearlessgames.prototyping.model;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import se.mockachino.*;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;

public class VendorTest {

	private Vendor vendor;
	private Seller exchange;
	private Buyer buyer;
	private CallHandler callHandler;
	private Order lastAcceptedResupplyOrder;
	private ItemTemplate itemTemplate;

	@Before
	public void setUp() throws Exception {
		itemTemplate = new ItemTemplate(100);
		final AtomicInteger orderId = new AtomicInteger();

		callHandler = new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				Offer offer = (Offer) call.getArguments()[0];
				lastAcceptedResupplyOrder = new Order(orderId.incrementAndGet(), offer.getQuantity(), offer.getTotalPrice(), offer.getBuyer());
				return lastAcceptedResupplyOrder;
			}
		};

		exchange = mock(Seller.class);
		stubAnswer(new CallHandler() {
			@Override
			public Object invoke(Object obj, MethodCall call) throws Throwable {
				final Object[] args = call.getArguments();
				return new Offer() {

					private final Integer quantity = (Integer) args[0];

					@Override
					public int getTotalPrice() {
						return ((ItemTemplate)args[1]).getBasePrice() * quantity;
					}

					@Override
					public Buyer getBuyer() {
						return (Buyer) args[2];
					}

					@Override
					public int getQuantity() {
						return quantity;
					}
				};
			}
		}).on(exchange).requestOffer(anyInt(), itemTemplate, any(Buyer.class));
		stubAnswer(callHandler).on(exchange).acceptOffer(any(Offer.class), anyInt());

		vendor = new Vendor(3, exchange, new LinearItemPriceService(-1), itemTemplate);

		vendor.giveMoney(1000);

		buyer = new Buyer() {
			@Override
			public void receive(Order order, Collection<Item> items) {

			}
		};

	}

	@Test
	public void buyItemsWhenStockIsEmpty() throws Exception {
		Offer offer = vendor.requestOffer(1, itemTemplate, mock(Buyer.class));
		assertEquals(0, offer.getQuantity());
	}

	@Test
	public void orderFullRestockOnTick() throws Exception {
		vendor.tick();

		verifyOnce().on(exchange).requestOffer(3, itemTemplate, vendor);
		verifyOnce().on(exchange).acceptOffer(any(Offer.class), anyInt());
	}

	@Test
	public void orderFullRestockOnlyOnceOnMultipleTicks() throws Exception {
		vendor.tick();
		vendor.tick();
		vendor.tick();

		verifyOnce().on(exchange).requestOffer(3, itemTemplate, vendor);
		verifyOnce().on(exchange).acceptOffer(any(Offer.class), anyInt());
	}


	@Test
	public void addToStock() throws Exception {
		Item item1 = new Item(itemTemplate);
		Item item2 = new Item(itemTemplate);

		vendor.receive(mock(Order.class), Lists.newArrayList(item1, item2));
		assertEquals(2, vendor.getStockSize());
	}

	@Test
	public void buyItemsWhenItemsInStock() throws Exception {
		Item item1 = new Item(itemTemplate);
		Item item2 = new Item(itemTemplate);

		vendor.receive(mock(Order.class), Lists.newArrayList(item1, item2));

		final AtomicInteger boughtItems = new AtomicInteger();
		Offer offer = vendor.requestOffer(1, itemTemplate, new Buyer() {
			@Override
			public void receive(Order order, Collection<Item> items) {
				boughtItems.getAndAdd(items.size());
			}
		});
		vendor.acceptOffer(offer, offer.getTotalPrice());

		vendor.tick();

		assertEquals(1, boughtItems.get());
		assertEquals(1, vendor.getStockSize());
	}

	@Test
	public void buyItemsTriggersResupplyOrder() throws Exception {

		Item item1 = new Item(itemTemplate);
		Item item2 = new Item(itemTemplate);
		Item item3 = new Item(itemTemplate);
		vendor.receive(mock(Order.class), Lists.newArrayList(item1, item2, item3));

		buyFromVendor(2);

		vendor.tick();

		verifyOnce().on(exchange).requestOffer(2, itemTemplate, vendor);
	}


	@Test
	public void buyTwiceShouldNotOrderMoreThanMaxDesiredQueue() throws Exception {

		vendor.receive(mock(Order.class), Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));

		buyFromVendor(2);

		vendor.tick();

		verifyOnce().on(exchange).requestOffer(2, itemTemplate, vendor);

		buyFromVendor(1);

		vendor.tick();

		verifyOnce().on(exchange).requestOffer(1, itemTemplate, vendor);
	}

	@Test
	public void doNothingWhenAnOfferHasZeroQuantity() throws Exception {
		when(exchange.requestOffer(anyInt(), itemTemplate, vendor)).thenReturn(new EmptyOffer(vendor));

		vendor.tick();
		verifyNever().on(exchange).acceptOffer(any(Offer.class), anyInt());

	}

	@Test
	public void deliveryRemovesFromExpectedOrders() throws Exception {
		vendor.receive(mock(Order.class), Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));
		buyFromVendor(2);
		vendor.tick();


		vendor.receive(lastAcceptedResupplyOrder, Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate)));

		vendor.tick();
		Collection<Order> expectedOrders = vendor.getExpectedOrders();
		assertTrue(expectedOrders.isEmpty());
	}

	@Test
	public void partialDelivery() throws Exception {
		vendor.receive(mock(Order.class), Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));

		buyFromVendor(2);
		vendor.tick();

		Order partialOrder = new Order(lastAcceptedResupplyOrder.getOrderId(), 1, lastAcceptedResupplyOrder.getPrice(), lastAcceptedResupplyOrder.getBuyer());
		vendor.receive(partialOrder, Lists.newArrayList(new Item(itemTemplate)));

		Collection<Order> expectedOrders = vendor.getExpectedOrders();
		assertEquals(1, expectedOrders.size());
		assertEquals(1, Iterables.getOnlyElement(expectedOrders).getQuantity());

		vendor.tick();

		expectedOrders = vendor.getExpectedOrders();
		assertEquals(1, expectedOrders.size());
		assertEquals(1, Iterables.getOnlyElement(expectedOrders).getQuantity());
	}

	@Test
	public void dontAcceptOfferIfYouCantAffordIt() throws Exception {
		vendor.receive(mock(Order.class), Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));
		buyFromVendor(2);
		vendor.giveMoney(-vendor.getMoney());
		vendor.tick();

		verifyNever().on(exchange).acceptOffer(any(Offer.class), anyInt());
	}

	@Test
	public void averageItemPriceEmpty() throws Exception {
		assertEquals(itemTemplate.getBasePrice(), vendor.getAverageItemPrice());
	}

	@Test
	public void averageItemPriceAdd() throws Exception {

		vendor.tick();
		vendor.receive(lastAcceptedResupplyOrder, Lists.newArrayList(new Item(itemTemplate), new Item(itemTemplate), new Item(itemTemplate)));

		assertEquals(itemTemplate.getBasePrice(), vendor.getAverageItemPrice());
	}

	@Test
	public void vendorTriesToMakeAProfit() throws Exception {
		//vendor.

	}

	private Order buyFromVendor(int quantity) {
		Offer offer = vendor.requestOffer(quantity, itemTemplate, buyer);
		return vendor.acceptOffer(offer, offer.getTotalPrice());

	}
}
