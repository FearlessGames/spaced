package se.fearlessgames.prototyping.ui;

import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.HeadlessException;

public class ExchangeView extends JFrame {
	private final JLabel numberOfItemsInStock = new JLabel("    ");
	private final JLabel numberOfOutstandingOrders = new JLabel("    ");
	private final JLabel numberOfItemsInBacklog = new JLabel("    ");
	private final JLabel money = new JLabel("    ");

	public ExchangeView() throws HeadlessException {
		setTitle("Item Exchange");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		JPanel panel = new JPanel();

		add(panel);

		TwoColumnBuilder twoColumnBuilder = new TwoColumnBuilder(panel);
		twoColumnBuilder.addRow("Number of items in stock", numberOfItemsInStock);
		twoColumnBuilder.addRow("Number of outstanding orders", numberOfOutstandingOrders);
		twoColumnBuilder.addRow("Number of items in backlog", numberOfItemsInBacklog);
		twoColumnBuilder.addRow("Money", money);

		setSize(400, 400);

	}

	public void setNumberOfItemsInStock(int numberOfItemsInStock) {
		this.numberOfItemsInStock.setText(String.valueOf(numberOfItemsInStock));

	}

	public void setNumberOfOutstandingOrders(int numberOfOutstandingOrders) {
		this.numberOfOutstandingOrders.setText(String.valueOf(numberOfOutstandingOrders));
	}

	public void setNumberOfItemsInBacklog(int numberOfItemsInBacklog) {
		this.numberOfItemsInBacklog.setText(String.valueOf(numberOfItemsInBacklog));
	}

	public void setMoney(long money) {
		this.money.setText(String.valueOf(money));
	}
}
