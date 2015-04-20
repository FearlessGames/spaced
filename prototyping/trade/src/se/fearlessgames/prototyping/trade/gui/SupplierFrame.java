package se.fearlessgames.prototyping.trade.gui;

import se.fearlessgames.prototyping.trade.model.Item;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;
import se.fearlessgames.prototyping.trade.model.Supplier;
import se.fearlessgames.prototyping.trade.observers.SupplierObserver;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SupplierFrame extends JFrame implements SupplierObserver {
	private JTextArea textArea;

	public SupplierFrame(final Supplier supplier) {
		super(supplier.getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel rootpanel = new JPanel();
		setSize(400, 250);
		textArea = new JTextArea();
		rootpanel.setLayout(new BoxLayout(rootpanel, BoxLayout.Y_AXIS));
		JScrollPane jScrollPane = new JScrollPane(textArea);
		rootpanel.add(jScrollPane);

		JButton jb = new JButton("Kill");
		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				supplier.kill();
			}
		});
		rootpanel.add(jb);
		getContentPane().add(rootpanel);
	}


	@Override
	public void startedResupplying(ItemTemplate itemTemplate) {
		append("sent delivery request to delivery service for item template " + itemTemplate.getName());
	}

	@Override
	public void notifyCancelAllDeliveries() {
		append("cancel all deliveries due to death");
	}

	@Override
	public void notifyRunningDeliveryFromSupplier(Item item) {
		append("Sending delivery of item " + item.getName());
	}

	private void append(String string) {
		textArea.append(string + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
}
