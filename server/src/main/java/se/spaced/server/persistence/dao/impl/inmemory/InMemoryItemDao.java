package se.spaced.server.persistence.dao.impl.inmemory;

import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.persistence.dao.interfaces.ItemDao;

public class InMemoryItemDao extends InMemoryDao<ServerItem> implements ItemDao {
	@Override
	public boolean isOwner(ServerEntity entity, ServerItem serverItem) {
		return entity.equals(serverItem.getOwner());
	}

	@Override
	public void persistVirtualItem(ServerItem item) {
		persist(item);
	}
}