package se.spaced.server.persistence.dao.impl.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.dao.interfaces.Persistable;

public class InMemoryEquipmentDao extends OwnedInMemoryDao<EquippedItems> implements EquipmentDao {
	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public synchronized EquippedItems findByOwner(Persistable owner) {
		EquippedItems equippedItems = super.findByOwner(owner);
		if (equippedItems == null) { //todo: refactor tests to not do this here!
			equippedItems = new EquippedItems(owner);
			persist(equippedItems);
		}
		return equippedItems;
	}
}
