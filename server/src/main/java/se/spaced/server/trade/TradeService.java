package se.spaced.server.trade;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.items.ServerItem;

public interface TradeService extends EntityServiceListener {

	TradeInitResult initiateTrade(ServerEntity initiator, ServerEntity collaborator);

	TradeAddItemResult addItem(ServerEntity entity, ServerItem item);

	TradeAcceptResult acceptOffer(ServerEntity entity, String checksum);

	void closeTrade(ServerEntity entity);

	void retractOffer(ServerEntity entity);

}

