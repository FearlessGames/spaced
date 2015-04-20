package se.fearlessgames.prototyping.model;

import org.junit.Before;
import org.junit.Test;
import se.mockachino.matchers.matcher.*;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.*;
import static se.mockachino.matchers.MatchersBase.mAny;

public class SupplierTest {
	private ItemTemplate itemTemplate;

	@Before
	public void setUp() throws Exception {
		itemTemplate = new ItemTemplate(100);
	}

	@Test
	public void createOneItem() throws Exception {
		Supplier supplier = new Supplier(2, itemTemplate);
		supplier.tick();

		assertEquals(1, supplier.getLocalStockSize());

	}

	@Test
	public void createTwoItems() throws Exception {
		Supplier supplier = new Supplier(2, itemTemplate);
		supplier.tick();
		supplier.tick();

		assertEquals(2, supplier.getLocalStockSize());

	}

	@Test
	public void createItemsWhenLocalStockIsFull() throws Exception {
		Supplier supplier = new Supplier(2, itemTemplate);
		supplier.tick();
		supplier.tick();
		supplier.tick();

		assertEquals(2, supplier.getLocalStockSize());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createItemAndDeliverToExchange() throws Exception {
		Supplier supplier = new Supplier(2, itemTemplate);

		Exchange exchange = mock(Exchange.class);

		supplier.registerExchange(exchange);

		supplier.tick();

		ArgumentCatcher<Collection> catcher = ArgumentCatcher.create(mAny(Collection.class));
		verifyOnce().on(exchange).addItems(match(catcher));
		List<Collection> values = catcher.getValues();

		assertEquals(1, values.get(0).size());

	}
}
