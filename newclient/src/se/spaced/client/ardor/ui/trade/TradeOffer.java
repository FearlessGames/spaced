package se.spaced.client.ardor.ui.trade;

import com.google.common.collect.Lists;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.spaced.client.model.item.ClientItem;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.c2s.ClientTradeMessages;

import java.util.List;

public class TradeOffer {
	private final ClientTradeMessages tradeMessages;
	private final Entity other;
	private String checksum;
	private final List<ClientItem> itemsFromOther = Lists.newArrayList();
	private final List<ClientItem> itemsFromMe = Lists.newArrayList();

	public TradeOffer(ClientTradeMessages tradeMessages, Entity other, String checksum) {
		this.tradeMessages = tradeMessages;
		this.other = other;
		this.checksum = checksum;
	}

	public boolean addItem(Entity by, ClientItem item, String checksum) {
		this.checksum = checksum;
		if (by.equals(other)) {
			itemsFromOther.add(item);
			return false;
		} else {
			itemsFromMe.add(item);
			return true;
		}
	}

	@LuaMethod(name = "GetOther")
	public Entity getOther() {
		return other;
	}

	@LuaMethod(name = "GetChecksum")
	public String getChecksum() {
		return checksum;
	}

	@LuaMethod(name = "MyItems")
	public List<ClientItem> getItemsFromMe() {
		return itemsFromMe;
	}

	@LuaMethod(name = "OthersItems")
	public List<ClientItem> getItemsFromOther() {
		return itemsFromOther;
	}

	@LuaMethod(name = "Accept")
	public void accept() {
		tradeMessages.acceptOffer(checksum);
	}

	@LuaMethod(name = "Reject")
	public void reject() {
		tradeMessages.rejectOffer();
	}

	@LuaMethod(name = "Retract")
	public void retract() {
		tradeMessages.retractOffer();
	}

	@LuaMethod(name = "AddItem")
	public void addItemToOffer(ClientItem item) {
		if (item != null) {
			tradeMessages.addItemToOffer(item);
		}
	}
}
