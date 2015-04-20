package se.fearlessgames.prototyping.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LinearItemPriceServiceTest {

	private ItemTemplate itemTemplate;

	@Before
	public void setUp() throws Exception {
		itemTemplate = new ItemTemplate(100);
	}

	@Test
	public void basePriceIsSameForDesiredStock() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-1);
		Item item = new Item(itemTemplate);
		assertEquals(item.getBasePrice(), itemPriceService.getPriceForItemBasedOnStock(100, 100, item.getBasePrice()));
	}

	@Test
	public void priceIsHalfFor50PercentOverDesired() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-1);
		Item item = new Item(itemTemplate);
		assertEquals(item.getBasePrice() / 2, itemPriceService.getPriceForItemBasedOnStock(100, 150, item.getBasePrice()));

	}

	@Test
	public void priceIs50PercentMoreFor50PercentBelowDesired() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-1);
		Item item = new Item(itemTemplate);
		assertEquals(150, itemPriceService.getPriceForItemBasedOnStock(100, 50, item.getBasePrice()));

	}

	@Test
	public void priceIsDoubleFor100PercentBelowDesired() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-1);
		Item item = new Item(itemTemplate);
		assertEquals(200, itemPriceService.getPriceForItemBasedOnStock(100, 0, item.getBasePrice()));

	}

	@Test
	public void dontPriceBelowZero() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-1);
		Item item = new Item(itemTemplate);
		assertEquals(0, itemPriceService.getPriceForItemBasedOnStock(100, 300, item.getBasePrice()));
	}

	@Test
	public void priceIs100PercentMoreFor50PercentBelowDesiredWithDoubleSlope() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-2);
		Item item = new Item(itemTemplate);
		assertEquals(200, itemPriceService.getPriceForItemBasedOnStock(100, 50, item.getBasePrice()));

	}


	@Test
	public void graphStuff() throws Exception {
		ItemPriceService itemPriceService = new LinearItemPriceService(-1);
		Item item = new Item(itemTemplate);
		for (int i = 1; i < 200; i += 10) {
			System.out.println(i + "\t" + itemPriceService.getPriceForItemBasedOnStock(100, i, item.getBasePrice()));
		}

	}
}
