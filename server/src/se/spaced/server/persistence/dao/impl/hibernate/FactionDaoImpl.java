package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.PersistedFaction;
import se.spaced.server.persistence.dao.interfaces.FactionDao;

public class FactionDaoImpl extends FindableDaoImpl<PersistedFaction> implements FactionDao {
	@Inject
	public FactionDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, PersistedFaction.class);
	}
}