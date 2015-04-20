package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import se.spaced.server.persistence.dao.interfaces.FindableDao;
import se.spaced.server.persistence.dao.interfaces.NamedPersistable;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

public class FindableDaoImpl<T extends NamedPersistable> extends DaoImpl<T> implements FindableDao<T> {
	public FindableDaoImpl(SessionFactory sessionFactory, Class<T> clazz) {
		super(sessionFactory, clazz);
	}


	@Override
	@AutoTransaction
	public T findByName(String name) {
		Criteria crit = getSession().createCriteria(clazz);
		crit.add(Restrictions.eq("name", name));
		return (T) crit.uniqueResult();
	}
}
