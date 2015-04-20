package se.spaced.server.persistence.dao.interfaces;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;

public interface ItemDao extends Dao<ServerItem> {
	boolean isOwner(ServerEntity entity, ServerItem serverItem);

	void persistVirtualItem(ServerItem item);
}
