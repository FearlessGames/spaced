package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.Entity;

public class TransactionManager {
	private final SessionFactory sessionFactory;

	@Inject
	public TransactionManager(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Transaction beginTransaction() {
		return getCurrentSession().beginTransaction();
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	public void rebuildFromDataBase(Object o) {
		if (o.getClass().getAnnotation(Entity.class) != null) {
			getCurrentSession().refresh(o);
		}
	}

	public boolean isActive() {
		return getCurrentSession().getTransaction().isActive();
	}
}
