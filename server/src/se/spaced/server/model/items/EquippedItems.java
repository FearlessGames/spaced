package se.spaced.server.model.items;

import com.google.common.collect.Maps;
import se.spaced.server.persistence.dao.impl.OwnedPersistableBase;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.shared.model.items.ContainerType;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.MapKeyEnumerated;
import java.util.Collections;
import java.util.Map;

@Entity
public class EquippedItems extends OwnedPersistableBase {

	public static final EquippedItems NONE = new EquippedItems();

	@ManyToMany(targetEntity = ServerItem.class, fetch = FetchType.EAGER)
	@MapKeyEnumerated
	private final Map<ContainerType, ServerItem> equippedItemMap = Maps.newEnumMap(ContainerType.class);

	EquippedItems() {
	}

	public EquippedItems(Persistable owner) {
		super(owner);
	}

	public Map<ContainerType, ServerItem> getEquippedItems() {
		return Collections.unmodifiableMap(equippedItemMap);
	}

	public ServerItem get(ContainerType type) {
		return equippedItemMap.get(type);
	}


	public void put(ServerItem item, ContainerType type) {
		equippedItemMap.put(type, item);
	}

	public ServerItem remove(ContainerType type) {
		return equippedItemMap.remove(type);
	}
}
