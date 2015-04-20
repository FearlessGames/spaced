package se.fearlessgames.prototyping.ui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimView extends JFrame {
	public SimView(final Presenter presenter) throws HeadlessException {

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());

		JButton spawnSupplierButton = new JButton("Spawn Supplier");
		spawnSupplierButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.spawnSupplier();
			}
		});
		add(spawnSupplierButton);


		JButton spawnVendorButton = new JButton("Spawn Vendor");
		spawnVendorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.spawnVendor();
			}
		});
		add(spawnVendorButton);

		pack();
	}


	public interface Presenter {

		void spawnSupplier();

		void spawnVendor();
	}
}
