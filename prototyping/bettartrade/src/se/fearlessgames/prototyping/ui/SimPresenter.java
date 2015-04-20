package se.fearlessgames.prototyping.ui;

import se.fearlessgames.prototyping.model.Exchange;
import se.fearlessgames.prototyping.model.ItemPriceService;
import se.fearlessgames.prototyping.model.ItemTemplate;

public class SimPresenter implements SimView.Presenter {
	private final SimView simView;
	private final TickService tickService;
	private final Exchange exchange;
	private final ItemPriceService itemPriceService;
	private final ItemTemplate itemTemplate;

	public SimPresenter(TickService tickService, Exchange exchange, ItemPriceService itemPriceService, ItemTemplate itemTemplate) {
		this.tickService = tickService;
		this.exchange = exchange;
		this.itemPriceService = itemPriceService;
		this.itemTemplate = itemTemplate;
		simView = new SimView(this);

	}

	public void start() {
		simView.setVisible(true);

	}

	@Override
	public void spawnSupplier() {
		SupplierPresenter supplierPresenter = new SupplierPresenter(tickService, exchange, itemTemplate);
		supplierPresenter.start();

	}

	@Override
	public void spawnVendor() {
		VendorPresenter vendorPresenter = new VendorPresenter(tickService, exchange, itemPriceService, itemTemplate);
		vendorPresenter.start();
	}
}
