package se.spaced.server.model.items;

import com.google.common.base.Predicate;
import com.google.common.collect.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.persistence.dao.impl.PersistableBase;

import javax.persistence.*;
import java.util.Map;

/**
 * An inventory is a container for stored items such as a bag or bank space.
 */
@Entity
public class PersistedInventory extends PersistableBase implements SpacedInventory, Inventory {
	@Transient
	private final Logger log = LoggerFactory.getLogger(getClass());

	private int nrOfSlots;

	@ManyToOne(fetch = FetchType.EAGER)
	private ServerEntity owner;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKeyColumn(name = "position")
	@Fetch(FetchMode.SUBSELECT)
	private final Map<Integer, ServerItemStack> stackMap = Maps.newHashMap();

	@Enumerated(EnumType.STRING)
	private InventoryType type;

	protected PersistedInventory() {
	}

	public PersistedInventory(ServerEntity owner, int nrOfSlots, InventoryType type) {
		this.owner = owner;
		this.nrOfSlots = nrOfSlots;
		this.type = type;

		for (int i = 0; i < nrOfSlots; i++) {
			stackMap.put(i + 1, new ServerItemStack());
		}
	}

	@Override
	public int getNrOfSlots() {
		return nrOfSlots;
	}

	@Override
	public ServerEntity getOwner() {
		return owner;
	}

	@Override
	public InventoryType getType() {
		return type;
	}


	public boolean addItem(ServerItem item, int position) {
		if (!owner.equals(item.getOwner())) {
			log.warn("Trying to add {} owned by {} to inventory owned by {}", item, item.getOwner(), owner);
			return false;
		}

		if (!acceptsItemAtPosition(position, item)) {
			return false;
		}

		stackMap.get(position).add(item);

		return true;
	}

	public boolean addItem(ServerItem item) {
		int nextFreePositionForItem = getNextFreePositionForItem(item);
		if (nextFreePositionForItem != 0) {
			return addItem(item, nextFreePositionForItem);
		}
		return false;

	}


	public ImmutableCollection<ServerItem> getStackAtPosition(int position) {
		return stackMap.get(position).getAll();
	}

	public void clearStackAtPosition(int position) {
		stackMap.get(position).clear();
	}

	public void addStackAtPosition(int pos, ImmutableCollection<ServerItem> stack) {
		stackMap.get(pos).addAll(stack);
	}

	public int removeItem(ServerItem item) {
		if (item == null) {
			return 0;
		}

		for (Map.Entry<Integer, ServerItemStack> entry : stackMap.entrySet()) {

			if (entry.getValue().contains(item)) {
				entry.getValue().removeItem(item);
				return entry.getKey();
			}
		}

		return 0;
	}

	@Override
	public boolean isFull() {
		for (Map.Entry<Integer, ServerItemStack> entry : stackMap.entrySet()) {
			if (!entry.getValue().isFull()) {
				return false;
			}
		}
		return true;
	}

	public boolean acceptsItemAtPosition(int position, ServerItem item) {
		return stackMap.get(position).isOfType(item.getTemplate()) && !stackMap.get(position).isFull();
	}


	public int getNextFreePositionForItem(ServerItem item) {
		for (Integer position : stackMap.keySet()) {
			if (acceptsItemAtPosition(position, item)) {
				return position;
			}
		}
		return 0;
	}

	public boolean acceptsItem(ServerItem item) {
		return getNextFreePositionForItem(item) != 0;
	}

	@Override
	public ImmutableMultimap<Integer, ? extends SpacedItem> getItemMap() {
		Multimap<Integer, SpacedItem> map = HashMultimap.create();
		for (Map.Entry<Integer, ServerItemStack> integerServerItemStackEntry : stackMap.entrySet()) {
			for (ServerItem item : integerServerItemStackEntry.getValue().getAll()) {
				map.put(integerServerItemStackEntry.getKey(), item);
			}
		}
		return ImmutableMultimap.copyOf(map);
	}

	@Override
	public boolean contains(final ServerItem item) {
		return Iterables.tryFind(stackMap.values(), new Predicate<ServerItemStack>() {
			@Override
			public boolean apply(ServerItemStack serverItemStack) {
				return serverItemStack.contains(item);
			}
		}).isPresent();
	}

	@Override
	public ServerItem getItem(UUID itemPk) {
		for (ServerItemStack stack : stackMap.values()) {
			ServerItem item = stack.get(itemPk);
			if (item != null) {
				return item;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "PersistedInventory{" +
				", type=" + type +
				", owner=" + owner +
				", " + stackMap.size() + "/" + nrOfSlots + " items" +
				'}';
	}


	public ItemTemplate getItemTemplateOnPosition(int pos) {
		return stackMap.get(pos).getItemTemplate();
	}

	public int getFreeStackSizeOnPosition(int pos2) {
		ServerItemStack serverItemStack = stackMap.get(pos2);
		return serverItemStack.getMaxStackSize() - serverItemStack.size();
	}
}
