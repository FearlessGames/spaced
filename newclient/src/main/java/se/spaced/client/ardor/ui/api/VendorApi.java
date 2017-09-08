package se.spaced.client.ardor.ui.api;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.ClientEntity;
import se.spaced.client.model.InventoryProvider;
import se.spaced.client.model.item.ClientItem;
import se.spaced.client.net.messagelisteners.ServerVendorMessagesImpl;
import se.spaced.client.net.smrt.ServerConnection;
import se.spaced.client.statistics.Analytics;
import se.spaced.client.statistics.Trackables;
import se.spaced.messages.protocol.SpacedItem;

import java.util.Collection;

@Singleton
public class VendorApi {

	private final ServerConnection serverConnection;
	private final ServerVendorMessagesImpl serverVendorMessages;
	private final InventoryProvider inventoryProvider;
	private final Analytics analytics;

	@Inject
	public VendorApi(
			ServerConnection serverConnection,
			ServerVendorMessagesImpl serverVendorMessages,
			InventoryProvider inventoryProvider, Analytics analytics) {
		this.serverConnection = serverConnection;
		this.serverVendorMessages = serverVendorMessages;
		this.inventoryProvider = inventoryProvider;
		this.analytics = analytics;
	}

	@LuaMethod(name = "GetVendorStock", global = true)
	public void requestListOfItemsForSale(ClientEntity vendor) {
		analytics.track(Trackables.VendorEvents.GET_STOCK);
		if (getActiveVendor() == null || !getActiveVendor().equals(vendor)) {
			serverConnection.getReceiver().vendor().requestVendorStock(vendor);
		}
	}

	@LuaMethod(name = "BuyItem", global = true)
	public void buyItem(ClientEntity vendor, ClientItem item) {
		analytics.track(Trackables.VendorEvents.BUY);
		serverConnection.getReceiver().vendor().playerBuysItemFromVendor(vendor, item);
	}

	@LuaMethod(name = "SellItem", global = true)
	public void sellItem(ClientEntity vendor, ClientItem item) {
		sell(vendor, Lists.newArrayList(item));
	}

	@LuaMethod(name = "SellItems", global = true)
	public void sellItems(ClientEntity vendor, int position) {
		ImmutableCollection<? extends SpacedItem> stackItems = inventoryProvider.getPlayerInventory().getItemMap().get(position);
		sell(vendor, stackItems);
	}

	private void sell(ClientEntity vendor, Collection<? extends SpacedItem> items) {
		serverConnection.getReceiver().vendor().playerSellsItemsToVendor(vendor, items);
	}


	@LuaMethod(name = "GetActiveVendor", global = true)
	public ClientEntity getActiveVendor() {
		return serverVendorMessages.getActiveVendor();
	}

	@LuaMethod(name = "StartVendoring", global = true)
	public void startVendoring(ClientEntity vendor) {
		analytics.track(Trackables.VendorEvents.INIT);
		serverVendorMessages.setVendoringActive(vendor);
		serverConnection.getReceiver().vendor().startVendoring(vendor);

	}

	@LuaMethod(name = "StopVendoring", global = true)
	public void stopVendoring(ClientEntity vendor) {
		analytics.track(Trackables.VendorEvents.CLOSE);
		serverVendorMessages.stopVendoring();
		serverConnection.getReceiver().vendor().endVendoring(vendor);
	}

	@LuaMethod(name = "ResetVendoring", global = true)
	public void resetVendoring() {
		serverVendorMessages.stopVendoring();
	}
}
