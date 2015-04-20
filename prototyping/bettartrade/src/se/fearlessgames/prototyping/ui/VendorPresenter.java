package se.fearlessgames.prototyping.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.prototyping.model.Buyer;
import se.fearlessgames.prototyping.model.Exchange;
import se.fearlessgames.prototyping.model.Item;
import se.fearlessgames.prototyping.model.ItemPriceService;
import se.fearlessgames.prototyping.model.ItemTemplate;
import se.fearlessgames.prototyping.model.Offer;
import se.fearlessgames.prototyping.model.Order;
import se.fearlessgames.prototyping.model.Vendor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

public class VendorPresenter implements TickListener, VendorView.Presenter {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final VendorView view;
	private final TickService.TickHandler tickHandler;
	private final Vendor vendor;
	private final ItemPriceService itemPriceService;
	private final ItemTemplate itemTemplate;

	public VendorPresenter(TickService tickService, Exchange exchange, ItemPriceService itemPriceService, ItemTemplate itemTemplate) {
		this.itemPriceService = itemPriceService;
		this.itemTemplate = itemTemplate;
		view = new VendorView(this);
		tickHandler = tickService.addTickListener(this);
		vendor = new Vendor(10, exchange, itemPriceService, itemTemplate);
		vendor.giveMoney(1000);
	}

	public void start() {
		view.setVisible(true);

		view.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				tickHandler.remove();
			}
		});
	}

	@Override
	public void tick() {
		log.debug("Vendor.tick");
		vendor.tick();
		updateView();
		//buyItems(1);
	}

	private void buyItems(int quantity) {
		log.debug("BuyItems {}", quantity);
		Offer offer = vendor.requestOffer(quantity, itemTemplate, new Buyer() {
			@Override
			public void receive(Order order, Collection<Item> items) {
				log.info("Bought {} items", items.size());
			}
		});
		if (offer.getQuantity() > 0) {
			vendor.acceptOffer(offer, offer.getTotalPrice());
		}
		updateView();
	}

	private void updateView() {
		view.setStockSize(vendor.getStockSize());
		view.setMoney(vendor.getMoney());
		view.setSellPrice(vendor.getSellPrice());
	}

	@Override
	public void buyOne() {
		buyItems(1);
		updateView();
	}

	@Override
	public void buyAll() {
		buyItems(vendor.getStockSize());
		updateView();
	}
}
