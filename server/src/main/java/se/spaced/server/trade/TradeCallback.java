package se.spaced.server.trade;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;

public interface TradeCallback {
	void initiated(ServerEntity initiator, ServerEntity collaborator, String checksum);

	void initiatorAccepted(ServerEntity initiator, ServerEntity collaborator);

	void collaboratorAccepted(ServerEntity initiator, ServerEntity collaborator);

	void itemAdded(ServerEntity by, ServerEntity other, ServerItem item, String checksum);

	void negotiating(ServerEntity initiator, ServerEntity collaborator);

	void collaboratorRejected(ServerEntity initiator, ServerEntity collaborator);

	void initiatorRejected(ServerEntity initiator, ServerEntity collaborator);

	void aborted(ServerEntity initiator, ServerEntity collaborator);

	void tradeCompleted(TradeTransaction transaction);
}
