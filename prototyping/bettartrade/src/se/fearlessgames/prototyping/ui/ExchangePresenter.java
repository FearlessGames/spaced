package se.fearlessgames.prototyping.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.prototyping.model.Exchange;

public class ExchangePresenter implements TickListener {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ExchangeView view;
	private TickService tickService;
	private Exchange exchange;

	public ExchangePresenter(TickService tickService, Exchange exchange) {
		this.tickService = tickService;
		this.exchange = exchange;
		view = new ExchangeView();

		tickService.addTickListener(this);

	}

	public void start() {
		view.setVisible(true);
	}

	@Override
	public void tick() {
		log.debug("Exchange.tick");
		exchange.tick();
		view.setNumberOfItemsInStock(exchange.getNumberOfItemsInStock());
		view.setNumberOfOutstandingOrders(exchange.getNumberOfOutstandingOrders());
		view.setNumberOfItemsInBacklog(exchange.getNumberOfItemsInBacklog());
		view.setMoney(exchange.getMoney());
	}
}
