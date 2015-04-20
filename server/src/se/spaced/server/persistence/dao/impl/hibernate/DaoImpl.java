package se.spaced.server.persistence.dao.impl.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import se.fearlessgames.common.util.uuid.UUID;
import se.spaced.server.persistence.dao.interfaces.Dao;
import se.spaced.server.persistence.dao.interfaces.Persistable;
import se.spaced.server.persistence.util.transactions.AutoTransaction;

import java.util.List;

public class DaoImpl<T extends Persistable> implements Dao<T> {

	private final SessionFactory sessionFactory;
	protected final Class<T> clazz;

	public DaoImpl(SessionFactory sessionFactory, Class<T> clazz) {
		this.sessionFactory = sessionFactory;
		this.clazz = clazz;
	}

	@AutoTransaction
	@Override
	public T persist(T obj) {
		getSession().saveOrUpdate(obj);
		return obj;
	}

	@AutoTransaction
	@Override
	public void delete(T obj) {
		getSession().delete(obj);
	}


	@AutoTransaction
	@Override
	public void deleteAll() {
		List<T> list = findAll();
		for (T t : list) {
			delete(t);
		}
	}

	@AutoTransaction
	@SuppressWarnings("unchecked")
	@Override
	public T findByPk(UUID key) {
		return (T) getSession().get(clazz, key);
	}

	@AutoTransaction
	@SuppressWarnings("unchecked")
	@Override
	public List<T> findAll() {
		return getSession().createQuery("from " + clazz.getName()).list();
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
}
