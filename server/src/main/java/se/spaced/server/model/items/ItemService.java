package se.spaced.server.model.items;

import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.model.ServerEntity;

import java.util.Collection;

public interface ItemService {

	void persistItem(ServerItem item, ServerEntity owner);

	Collection<ServerItemTemplate> getAllTemplates();

	ServerItemTemplate getTemplateByName(String name);

	void addTemplate(ServerItemTemplate itemTemplate);

	ServerItem getItemByPk(UUID pk);

	void useItem(ServerEntity entity, ServerEntity target, ServerItem item);

	void deleteItem(ServerItem serverItem);

	ServerItemTemplate getTemplateByPk(UUID pk);

	ExchangeResult transferItem(ServerEntity from, ServerEntity to, ServerItem item);

	ExchangeResult exchangeItems(ServerEntity part1, ServerItem item1, ServerEntity part2, ServerItem item2);

	boolean isOwner(ServerEntity entity, ServerItem serverItem);

	UUID addVirtualItem(ServerItem serverItem);

	void removeVirtualItem(UUID item);

	boolean isVirtualItem(UUID pk);
}