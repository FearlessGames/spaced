package se.fearlessgames.prototyping.trade.gui;

import se.fearlessgames.prototyping.trade.model.Item;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;
import se.fearlessgames.prototyping.trade.model.Vendor;
import se.fearlessgames.prototyping.trade.observers.VendorStockObserver;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VendorFrame extends JFrame implements VendorStockObserver {
	private final DefaultListModel itemListModel;
	private JTextArea textArea;

	public VendorFrame(final Vendor vendor) {
		super(vendor.getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel rootpanel = new JPanel();
		setSize(400, 250);
		textArea = new JTextArea();
		rootpanel.setLayout(new BorderLayout());
		JScrollPane jScrollPane = new JScrollPane(textArea);
		rootpanel.add(jScrollPane, BorderLayout.CENTER);
		JButton jb = new JButton("Buy");
		jb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vendor.purchaseRandom();
			}
		});
		rootpanel.add(jb, BorderLayout.SOUTH);
		itemListModel = new DefaultListModel();
		JList itemList = new JList(itemListModel);
		itemList.setVisibleRowCount(10);
		rootpanel.add(itemList, BorderLayout.EAST);
		getContentPane().add(rootpanel);
	}

	@Override
	public void notifyPurchase(Item item) {
		append("New vendor purchase: " + item.getName() + " at price " + item.getPrice());
		itemListModel.removeElement(item);
	}

	@Override
	public void notifyResupply(List<Item> items) {
		append("VendorStock was resupplied with " + items.size() + " items");
		for (Item item : items) {
			append(item.getName());
			itemListModel.addElement(item);
		}
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

	@Override
	public void notifyOrderedItems(ItemTemplate itemTemplate, int amount) {
		append("Placed an order of " + amount + " " + itemTemplate.getName());
	}

	private void append(String text) {
		textArea.append(text + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}

}
