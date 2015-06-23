package se.spaced.server.mob.brains;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.s2c.S2CMultiDispatcher;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.messages.protocol.s2c.ServerMovementMessages;
import se.spaced.messages.protocol.s2c.adapter.S2CAdapters;
import se.spaced.server.mob.MobDecision;
import se.spaced.server.mob.MobOrderExecutor;
import se.spaced.server.mob.MobWhisperer;
import se.spaced.server.mob.brains.util.CooldownPredicate;
import se.spaced.server.mob.brains.util.ProximityPredicate;
import se.spaced.server.model.Mob;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ItemService;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.EntityInteractionCapability;
import se.spaced.shared.model.PositionalData;
import se.spaced.shared.playback.RecordingPoint;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VendorBrain implements MobBrain, ServerMovementMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final S2CProtocol receiver = new S2CMultiDispatcher(S2CAdapters.createServerMovementMessages(this));
	private final Mob mob;
	private final List<ServerItemTemplate> itemTypesForSale;

	private final MobOrderExecutor mobOrderExecutor;
	private final MobWhisperer mobWhisperer;
	CooldownPredicate cooldownPredicate;

	private final ProximityPredicate proximityPredicate;
	private final ItemService itemService;
	private Map<UUID, ServerItem> itemsForSale = new ConcurrentHashMap<UUID, ServerItem>();

	public VendorBrain(
			Mob mob,
			List<ServerItemTemplate> itemTypesForSale,
			MobOrderExecutor mobOrderExecutor,
			long whisperTimeout,
			double whisperDistance,
			TimeProvider timeProvider,
			ItemService itemService) {
		this.mob = mob;
		this.itemTypesForSale = itemTypesForSale;
		this.mobOrderExecutor = mobOrderExecutor;
		this.itemService = itemService;
		mobWhisperer = new MobWhisperer(mobOrderExecutor, mob);
		proximityPredicate = new ProximityPredicate(mob, whisperDistance);
		cooldownPredicate = new CooldownPredicate(timeProvider, whisperTimeout);
	}

	@Override
	public MobDecision act(long now) {
		return MobDecision.UNDECIDED;
	}

	@Override
	public Mob getMob() {
		return mob;
	}

	@Override
	public S2CProtocol getSmrtReceiver() {
		return receiver;
	}

	@Override
	public EnumSet<EntityInteractionCapability> getInteractionCapabilities() {
		return EnumSet.of(EntityInteractionCapability.VENDOR);
	}

	public List<ServerItem> getItemsForSale() {
		ArrayList<ServerItem> serverItems = Lists.newArrayList();
		serverItems.addAll(itemsForSale.values());
		return serverItems;
	}

	@Override
	public void teleportTo(PositionalData positionalData) {
	}

	@Override
	public void sendPlayback(Entity entity, RecordingPoint<AnimationState> recordingPoint) {
		StringBuilder sb = new StringBuilder("Come get fresh items. New in stock: ");
		ServerItemTemplate firstItem = itemTypesForSale.get(0);
		sb.append(firstItem.getName()).append(" for just ").append(firstItem.getSellsFor().getAmount()).append(" ").append(
				firstItem.getSellsFor().getCurrency().getName());
		checkProximity((ServerEntity) entity, sb.toString());
	}

	private void checkProximity(ServerEntity entity, String message) {
		if (Predicates.and(proximityPredicate, cooldownPredicate).apply(entity)) {
			mobWhisperer.whisperEntity(entity, message);
			cooldownPredicate.updateLastTime(entity);
		}
	}

	@Override
	public void restartRecorder(PositionalData positionalData) {
	}

	public void deflateWares() {
		for (ServerItemTemplate template : itemTypesForSale) {
			if (template.getSellsFor() != null) {
				ServerItem serverItem = template.create();
				UUID uuid = itemService.addVirtualItem(serverItem);
				itemsForSale.put(uuid, serverItem);
			} else {
				log.warn("Vendor {} has item {} without price, will not add to stock ",
						this.getMob().getName(),
						template.getName());
			}

		}
	}

	public boolean sellsItem(SpacedItem item) {
		return getItemsForSale().contains(item);
	}

	public ServerItem restock(ServerItemTemplate itemType) {
		if (!alreadyInStock(itemType)) {
			ServerItem serverItem = itemType.create();
			itemService.addVirtualItem(serverItem);
			itemsForSale.put(serverItem.getPk(), serverItem);
			return serverItem;
		}
		return null;
	}

	private boolean alreadyInStock(ServerItemTemplate serverItemTemplate) {
		for (ServerItem item : itemsForSale.values()) {
			if (item.getTemplate().getPk().equals(serverItemTemplate.getPk())) {
				return true;
			}
		}
		return false;
	}

	public void removeItemFromStock(ServerItem item) {
		ServerItemTemplate template = (ServerItemTemplate) item.getItemTemplate();
		itemsForSale.remove(item.getPk());
		restock(template);
	}

	public void lookAt(ServerEntity entity) {
		if (entity != null) {
			mobOrderExecutor.lookAt(mob, entity);
		}
	}

	public void addItemToStock(ServerItem item) {
		itemsForSale.put(item.getPk(), item);
	}
}
