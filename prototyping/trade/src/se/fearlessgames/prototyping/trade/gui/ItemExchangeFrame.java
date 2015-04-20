package se.fearlessgames.prototyping.trade.gui;

import se.fearlessgames.prototyping.trade.model.Item;
import se.fearlessgames.prototyping.trade.model.ItemExchange;
import se.fearlessgames.prototyping.trade.model.ItemTemplate;
import se.fearlessgames.prototyping.trade.model.Vendor;
import se.fearlessgames.prototyping.trade.observers.ItemExchangeObserver;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.util.List;

public class ItemExchangeFrame extends JFrame implements ItemExchangeObserver {
	private JTextArea textArea;
	private final DefaultListModel itemListModel;

	public ItemExchangeFrame(ItemExchange itemExchange) {
		super(itemExchange.getName());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		JPanel rootpanel = new JPanel();
		setSize(400, 250);
		textArea = new JTextArea();

		rootpanel.setLayout(new BorderLayout());

		JScrollPane jScrollPane = new JScrollPane(textArea);

		rootpanel.add(jScrollPane, BorderLayout.CENTER);


		itemListModel = new DefaultListModel();
		JList itemList = new JList(itemListModel);
		itemList.setVisibleRowCount(10);
		rootpanel.add(itemList, BorderLayout.EAST);

		getContentPane().add(rootpanel);
	}


	@Override
	public void notifyIncomingOrderFromVendor(ItemTemplate itemTemplate, int requestedAmount, Vendor vendor, boolean instantDelivery) {
		append(String.format("Incoming order from vendor (%s) for %d %s", vendor.getName(), requestedAmount, itemTemplate.getName()));
	}

	@Override
	public void notifyAddingToRestOrderQueue(ItemTemplate itemTemplate, int amountOfRestOrdersToCreate, Vendor vendor) {
		append("Created rest order for " + amountOfRestOrdersToCreate + " " + itemTemplate.getName() + "s");
	}

	@Override
	public void notifyInstantDeliveryToVendor(List<Item> bookedItems) {
		for (Item bookedItem : bookedItems) {
			append("Instant Delivery: " + bookedItem.getName());
			itemListModel.removeElement(bookedItem);
		}
	}

	@Override
	public void notifyNewScheduledDelivery(Vendor vendor, List<Item> bookedItems) {
		for (Item bookedItem : bookedItems) {
			append("Scheduled Delivery: " + bookedItem.getName());

		}
	}

	@Override
	public void notifyPeriodicRestChew() {
		append("Chewing rest orders");
	}

	@Override
	public void notifyGotResuppliedWith(Item item) {
		append("Got resupplied by supplier with item " + item.getName());
		itemListModel.addElement(item);
	}

	@Override
	public void notifyDeniedResuppliedDueToMaxStockFor(Item item) {
		append("Denied resupply due to max stock of item " + item.getName());
	}

	@Override
	public void notifyRunningDeliveryFromExchange(List<Item> bookedItems) {
		append("Sending booked items:");
		for (Item bookedItem : bookedItems) {
			append(bookedItem.getName());
		}

	}

	private void append(String text) {
		textArea.append(text + "\n");
		textArea.setCaretPosition(textArea.getDocument().getLength());

	}
}
