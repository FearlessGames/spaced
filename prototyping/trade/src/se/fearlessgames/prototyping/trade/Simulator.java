package se.fearlessgames.prototyping.trade;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.fearlessgames.common.util.uuid.UUIDFactoryImpl;
import se.fearlessgames.prototyping.trade.gui.ItemExchangeFrame;
import se.fearlessgames.prototyping.trade.gui.SupplierFrame;
import se.fearlessgames.prototyping.trade.gui.VendorFrame;
import se.fearlessgames.prototyping.trade.model.ItemDeliveryService;
import se.fearlessgames.prototyping.trade.model.ItemExchange;
import se.fearlessgames.prototyping.trade.model.ItemStock;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;
import se.fearlessgames.prototyping.trade.model.Supplier;
import se.fearlessgames.prototyping.trade.model.Vendor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Simulator {
	private final ItemDeliveryService itemDeliveryService;
	private final Map<ItemTemplate, ItemStock> stock;
	private final ItemExchange exchange;
	private final Vendor v1;
	private final Supplier s1;
	private ItemTemplate blasterTemplate = new ItemTemplate(new UUIDFactoryImpl(new SystemTimeProvider(), new Random()), "Mega blaster", 100);

	public static void main(String[] args) {
		Simulator simulator = new Simulator();
		simulator.start();
	}

	public Simulator() {
		itemDeliveryService = new ItemDeliveryService(2);
		stock = createExchangeWantedStock();
		exchange = new ItemExchange(stock, itemDeliveryService, "Item exchange 1");
		v1 = new Vendor(exchange, ImmutableMap.<ItemTemplate, Integer>builder().put(blasterTemplate, 5).build(), "Vendor 1");
		s1 = new Supplier(exchange, ImmutableMap.<ItemTemplate, Integer>builder().put(blasterTemplate, 10).build(), itemDeliveryService, "Supplier 1");
	}

	public void start() {
		VendorFrame vf = new VendorFrame(v1);
		ItemExchangeFrame ief = new ItemExchangeFrame(exchange);
		SupplierFrame supplierFrame = new SupplierFrame(s1);
		int exchangeHeight = ief.getHeight();
		supplierFrame.setLocation(0, 0);
		supplierFrame.setVisible(true);
		ief.setVisible(true);
		ief.setLocation(0, exchangeHeight);

		vf.setVisible(true);
		int vendorHeight = exchangeHeight + supplierFrame.getHeight();
		vf.setLocation(0, vendorHeight);

		v1.setObserver(vf);
		s1.setObserver(supplierFrame);
		exchange.setObserver(ief);

		exchange.startHandleRestOrders();
		s1.startResuply();
		v1.resuplyToInitialState();
	}

	private Map<ItemTemplate, ItemStock> createExchangeWantedStock() {
		HashMap<ItemTemplate, ItemStock> stockHashMap = Maps.newHashMap();
		stockHashMap.put(blasterTemplate, new ItemStock(10));
		return stockHashMap;
	}


}
