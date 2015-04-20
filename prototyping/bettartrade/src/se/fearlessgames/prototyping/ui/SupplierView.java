package se.fearlessgames.prototyping.ui;

import se.spaced.shared.tools.ui.TwoColumnBuilder;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.HeadlessException;

public class SupplierView extends JFrame {
	private final JLabel localStockSize = new JLabel();

	public SupplierView() throws HeadlessException {
		JPanel panel = new JPanel();
		add(panel);

		TwoColumnBuilder builder = new TwoColumnBuilder(panel);


		builder.addRow("Local stock size: ", localStockSize);

		setTitle("Supplier");

		setSize(400, 400);

	}

	public void setLocalStockSize(int localStockSize) {
		this.localStockSize.setText(String.valueOf(localStockSize));
	}
}
