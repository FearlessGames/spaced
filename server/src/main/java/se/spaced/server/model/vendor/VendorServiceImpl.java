package se.spaced.server.model.vendor;

import com.google.common.collect.HashMultimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.mob.brains.MobBrain;
import se.spaced.server.mob.brains.VendorBrain;
import se.spaced.server.model.Mob;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.currency.MoneyService;
import se.spaced.server.model.currency.MoneyUnderflowException;
import se.spaced.server.model.items.*;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class VendorServiceImpl implements VendorService {
	private ConcurrentHashMap<UUID, VendorBrain> vendors = new ConcurrentHashMap<UUID, VendorBrain>();
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ItemService itemService;
	private final InventoryService inventoryService;
	private final MoneyService moneyService;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;
	private final HashMultimap<UUID, ServerEntity> currentTradersByVendorPk = HashMultimap.create();
	private final ConcurrentHashMap<ServerItem, Player> itemsTraded = new ConcurrentHashMap<ServerItem, Player>();

	@Inject
	public VendorServiceImpl(
			ItemService itemService,
			InventoryService inventoryService,
			MoneyService moneyService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		this.itemService = itemService;
		this.inventoryService = inventoryService;
		this.moneyService = moneyService;
		this.smrtBroadcaster = smrtBroadcaster;
	}

	@Override
	public void registerVendor(VendorBrain vendorBrain) {
		vendorBrain.deflateWares();
		vendors.put(vendorBrain.getMob().getPk(), vendorBrain);
		log.debug("Registering new vendor: {}", vendorBrain.getMob().getName());
	}

	@Override
	public List<ServerItem> getWares(UUID vendorPk, Player player) {
		VendorBrain vendor = vendors.get(vendorPk);
		if (vendor == null) {
			throw new IllegalStateException("No vendor found");
		}
		vendor.lookAt(player);
		return vendor.getItemsForSale();
	}

	@Override
	public boolean isVendor(Entity vendor) {
		return vendors.containsKey(vendor.getPk());
	}

	@Override
	@AutoTransaction
	public void playerBuysItemFromVendor(
			Entity vendor,
			Player player,
			ServerItem item) {
		VendorBrain vendorBrain = vendors.get(vendor.getPk());
		Player tradingPlayer = itemsTraded.putIfAbsent(item, player);
		if (tradingPlayer != null) {
			return;
		}
		if (isAllowedToBuy(item, vendorBrain)) {
			try {
				moneyService.subtractMoney(item.getSellsFor(), player);
				vendorBrain.removeItemFromStock(item);
				itemService.persistItem(item, player);
				inventoryService.add(inventoryService.getInventory(player, InventoryType.BAG), item);
				smrtBroadcaster.create().to(player).send().vendor().boughtItem(item);
				smrtBroadcaster.create().to(getCurrentPeopleVendoring(vendor)).exclude(player).send().vendor().itemWasBought(
						item);
			} catch (MoneyUnderflowException e) {
				smrtBroadcaster.create().to(player).send().vendor().cannotAfford(item.getSellsFor().getCurrency().getName(),
						item.getSellsFor().getAmount());
			} catch (InventoryFullException e) {
				smrtBroadcaster.create().to(player).send().vendor().inventoryFull();
				moneyService.awardMoney(player, item.getSellsFor());
			}
		}
		itemsTraded.remove(item, player);

	}

	private boolean isAllowedToBuy(ServerItem item, VendorBrain vendorBrain) {
		return vendorBrain != null && vendorBrain.sellsItem(item) && itemService.isVirtualItem(item.getPk());
	}

	@Override
	public Set<ServerEntity> getCurrentPeopleVendoring(Entity vendor) {
		return currentTradersByVendorPk.get(vendor.getPk());
	}

	@Override
	@AutoTransaction
	public ServerItem playerSellsItemToVendor(Player player, Entity vendor, ServerItem serverItem) {
		VendorBrain vendorBrain = vendors.get(vendor.getPk());
		if (!itemService.isOwner(player, serverItem)) {
			throw new RuntimeException("Trying to sell an item that does not belong to the player. Worth dying for");
		}
		ServerItem newItem = vendorBrain.restock(serverItem.getTemplate());
		itemService.deleteItem(serverItem);
		moneyService.awardMoney(player, serverItem.getSellsFor());
		inventoryService.removeIfExists(serverItem);
		return newItem;
	}

	@Override
	public void initVendoring(Player player, Entity vendor) {
		currentTradersByVendorPk.put(vendor.getPk(), player);
	}

	@Override
	public void endVendoring(Player player, Entity vendor) {
		currentTradersByVendorPk.remove(vendor.getPk(), player);
	}

	@Override
	public void entitySpawned(Mob mob, MobBrain brain) {
	}

	@Override
	public void entityDespawned(Mob mob) {
		if (vendors.containsKey(mob.getPk())) {
			VendorBrain vendor = vendors.remove(mob.getPk());
			Set<ServerEntity> players = currentTradersByVendorPk.removeAll(mob.getPk());
			smrtBroadcaster.create().to(players).send().vendor().vendorDespawned(mob);
			log.debug("removing vendor: {}", mob.getName());
			for (SpacedItem item : vendor.getItemsForSale()) {
				itemService.removeVirtualItem(item.getPk());
			}
		}

	}

	@Override
	public void entityAdded(ServerEntity entity) {
	}

	@Override
	public void entityRemoved(ServerEntity entity) {
		currentTradersByVendorPk.values().remove(entity);
	}
}
