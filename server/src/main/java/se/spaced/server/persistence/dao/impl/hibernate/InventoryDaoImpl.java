package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.Inventory;
import se.spaced.server.model.items.InventoryType;
import se.spaced.server.model.items.PersistedInventory;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.persistence.dao.interfaces.InventoryDao;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import java.util.List;

public class InventoryDaoImpl extends DaoImpl<PersistedInventory> implements InventoryDao {
	@Inject
	public InventoryDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, PersistedInventory.class);
	}

	@Override
	@AutoTransaction
	public PersistedInventory findInventory(ServerEntity player, InventoryType type) {
		Criteria crit = getSession().createCriteria(clazz);
		crit.add(Restrictions.eq("owner.pk", player.getPk()));
		crit.add(Restrictions.eq("type", type));
		return (PersistedInventory) crit.uniqueResult();
	}

	@Override
	@AutoTransaction
	public PersistedInventory findInventory(ServerItem serverItem) {
		return (PersistedInventory) getSession().createQuery("select inventory from PersistedInventory inventory " +
				"join inventory.stackMap as serverItemStack " +
				"join serverItemStack.stack as items " +
				"where items.pk = :serverItemPk").

		/*return (PersistedInventory) getSession().createQuery(
				"select inventory from PersistedInventory inventory " +
						"join inventory.containingItems as containingItems " +
						"where containingItems.pk = :serverItemPk"
		).*/
				setParameter("serverItemPk", serverItem.getPk()).
				uniqueResult();
	}

	@Override
	@AutoTransaction
	public List<PersistedInventory> findByOwner(ServerEntity entity) {
		Criteria crit = getSession().createCriteria(clazz);
		crit.add(Restrictions.eq("owner.pk", entity.getPk()));
		return crit.list();
	}

	@Override
	@AutoTransaction
	public PersistedInventory refresh(Inventory inventory) {
		getSession().refresh(inventory);
		return (PersistedInventory) inventory;
	}


}
