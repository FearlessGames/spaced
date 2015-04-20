package se.fearlessgames.prototyping.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.prototyping.model.Exchange;
import se.fearlessgames.prototyping.model.ItemTemplate;
import se.fearlessgames.prototyping.model.Supplier;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SupplierPresenter implements TickListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final SupplierView view;
	private TickService.TickHandler tickHandler;
	private TickService tickService;
	private Exchange exchange;
	private final Supplier supplier;

	public SupplierPresenter(TickService tickService, Exchange exchange, ItemTemplate itemTemplate) {
		this.tickService = tickService;
		this.exchange = exchange;
		this.view = new SupplierView();

		supplier = new Supplier(500, itemTemplate);
		supplier.registerExchange(exchange);

	}

	public void start() {
		view.setVisible(true);

		tickHandler = tickService.addTickListener(this);

		view.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				tickHandler.remove();
			}
		});

	}

	@Override
	public void tick() {
		log.debug("Supplier.tick");
		supplier.tick();
		view.setLocalStockSize(supplier.getLocalStockSize());
	}
}
