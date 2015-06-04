package se.spaced.messages.protocol.s2c;

import se.smrt.core.SmrtProtocol;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;

@SmrtProtocol
public interface ServerTradeMessages {
	void tradeInitiated(Entity other, String checksum);

	void tradeInitFailed(String reason);

	void itemAdded(Entity by, SpacedItem item, String checksum);

	void negotiating();

	void addItemToOfferFail(String reason);

	void rejected(Entity other);

	void aborted(Entity other);

	void accepted(Entity other);

	void completed();

	void tradeFailedToComplete(String name);

	void playerMoneyUpdate(String currency, long updateAmount, long totalAmount);

	void playerMoneySubtracted(String currency, long amount, long totalAmount);

}
