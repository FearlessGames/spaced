package se.spaced.server.model.vendor;

import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Entity;
import se.spaced.server.mob.brains.VendorBrain;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.entity.EntityServiceListener;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.spawn.SpawnListener;

import java.util.List;
import java.util.Set;

public interface VendorService extends EntityServiceListener, SpawnListener {
	void registerVendor(VendorBrain vendorBrain);

	List<ServerItem> getWares(UUID vendorPk, Player player);

	void playerBuysItemFromVendor(Entity vendor, Player player, ServerItem item);

	ServerItem playerSellsItemToVendor(Player player, Entity vendor, ServerItem serverItem);

	void initVendoring(Player player, Entity vendor);

	void endVendoring(Player player, Entity vendor);

	Set<ServerEntity> getCurrentPeopleVendoring(Entity vendor);

	boolean isVendor(Entity vendor);
}
