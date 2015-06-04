package se.spaced.messages.protocol.c2s;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;

@SmrtProtocol
public interface ClientTradeMessages {
	void initiateTrade(Entity collaborator);

	void addItemToOffer(SpacedItem item);

	void acceptOffer(String checksum);

	void rejectOffer();

	void retractOffer();
}
