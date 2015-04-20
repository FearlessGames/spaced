package se.fearlessgames.prototyping.ui;

import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VendorView extends JFrame {
	private final JLabel stockSize = new JLabel();
	private final JLabel money = new JLabel();
	private final JLabel sellPrice = new JLabel();


	interface Presenter {

		void buyOne();
		void buyAll();
	}
	public VendorView(final Presenter presenter) throws HeadlessException {

		JPanel panel = new JPanel();
		add(panel);

		TwoColumnBuilder builder = new TwoColumnBuilder(panel);

		builder.addRow("Local stock size: ", stockSize);
		builder.addRow("Money", money);
		builder.addRow("Sell price", sellPrice);

		JButton buyOne = new JButton("Buy one");
		buyOne.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.buyOne();
			}
		});
		JButton buyAll = new JButton("Buy all in stock");
		buyAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.buyAll();
			}
		});
		builder.addRow(buyOne, buyAll);


		setTitle("Vendor");
		setSize(400, 400);
	}

	public void setStockSize(int stockSize) {
		this.stockSize.setText(String.valueOf(stockSize));
	}

	public void setMoney(long money) {
		this.money.setText(String.valueOf(money));
	}

	public void setSellPrice(int sellPrice) {
		this.sellPrice.setText(String.valueOf(sellPrice));
	}

}
