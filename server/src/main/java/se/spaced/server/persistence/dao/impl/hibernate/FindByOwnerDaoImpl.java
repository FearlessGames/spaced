package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.impl.OwnedPersistableBase;
import se.spaced.server.persistence.dao.interfaces.FindByOwnerDao;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public class FindByOwnerDaoImpl<T extends OwnedPersistableBase> extends DaoImpl<T> implements FindByOwnerDao<T> {

	public FindByOwnerDaoImpl(SessionFactory sessionFactory, Class<T> clazz) {
		super(sessionFactory, clazz);
	}

	@Override
	public T findByPk(UUID key) {
		throw new RuntimeException(
				"Programming error - do not use findByPk for entities that are owned, use findbyOwner instead!");
	}

	@Override
	@AutoTransaction
	public T findByOwner(Persistable owner) {
		Criteria crit = getSession().createCriteria(clazz);
		crit.add(Restrictions.eq("ownerPk", owner.getPk()));
		return (T) crit.uniqueResult();
	}
}
