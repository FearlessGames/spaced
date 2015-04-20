package se.fearlessgames.prototyping.ui;

import se.fearlessgames.prototyping.model.Exchange;
import se.fearlessgames.prototyping.model.ItemPriceService;
import se.fearlessgames.prototyping.model.ItemTemplate;
import se.fearlessgames.prototyping.model.LinearItemPriceService;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Simulator {
	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getLookAndFeel());

		final TickService tickService = new TickService();

		ItemTemplate itemTemplate = new ItemTemplate(100);
		ItemPriceService itemPriceService = new LinearItemPriceService(0);
		final Exchange exchange = new Exchange(20, itemPriceService, itemTemplate);
		exchange.giveMoney(2000);
		tickService.addTickListener(new TickListener() {
			@Override
			public void tick() {
				exchange.tick();
			}
		});



		SimPresenter simPresenter = new SimPresenter(tickService, exchange, itemPriceService, itemTemplate);
		simPresenter.start();

		ExchangePresenter exchangePresenter = new ExchangePresenter(tickService, exchange);
		exchangePresenter.start();


		ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(11);
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				tickService.tick();
			}
		}, 1, 1, TimeUnit.SECONDS);

	}
}
