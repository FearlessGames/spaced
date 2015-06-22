package se.spaced.server.persistence.dao.impl.hibernate;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;
import se.spaced.server.model.PersistedCreatureType;
import se.spaced.server.persistence.dao.interfaces.CreatureTypeDao;

public class CreatureTypeDaoImpl extends FindableDaoImpl<PersistedCreatureType> implements CreatureTypeDao {

	@Inject
	public CreatureTypeDaoImpl(SessionFactory sessionFactory) {
		super(sessionFactory, PersistedCreatureType.class);
	}
}
