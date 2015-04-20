package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.spaced.server.model.Mob;
import se.spaced.server.model.items.EquippedItems;
import se.spaced.server.persistence.dao.interfaces.EquipmentDao;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public class EquipmentDaoImpl extends FindByOwnerDaoImpl<EquippedItems> implements EquipmentDao {
	private final Logger log = LoggerFactory.getLogger(getClass());

	public EquipmentDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, EquippedItems.class);
	}

	@Override
	@AutoTransaction
	public EquippedItems findByOwner(Persistable owner) {
		if (owner instanceof Mob) {
			return ((Mob) owner).getEquippedItems();
		}
		return super.findByOwner(owner);
	}

}
